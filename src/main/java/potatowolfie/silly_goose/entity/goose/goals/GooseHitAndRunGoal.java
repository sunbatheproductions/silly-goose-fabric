package potatowolfie.silly_goose.entity.goose.goals;

import potatowolfie.silly_goose.advancement.HitandRunAdvancementHandler;
import potatowolfie.silly_goose.advancement.HonkandRunAdvancementHandler;
import potatowolfie.silly_goose.damage.SillyGooseDamageTypes;
import potatowolfie.silly_goose.entity.goose.GooseEntity;
import potatowolfie.silly_goose.sound.SillyGooseSounds;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GooseHitAndRunGoal extends Goal {
    private final GooseEntity goose;
    private Player targetPlayer;
    private int cooldownTimer;
    private boolean isRunningAway;
    private double runAwayDistance;

    private Vec3 targetPos = null;
    private int navigationTimeout = 0;
    private int stuckCheckTimer = 0;
    private BlockPos lastPos = null;
    private boolean useDirectSwimming = false;
    private BlockPos nearestWaterPos = null;
    private int waterSearchCooldown = 0;

    private int waterExitTimer = 0;
    private static final int WATER_EXIT_BOOST_INTERVAL = 10;
    private static final double WATER_EXIT_BOOST_STRENGTH = 0.3;

    private static final int MIN_COOLDOWN = 6000;
    private static final int MAX_COOLDOWN = 8400;
    private static final double DETECTION_RADIUS = 32.0;
    private static final double ATTACK_RANGE = 12.0;
    private static final double MIN_RUN_DISTANCE = 8.0;
    private static final double MAX_RUN_DISTANCE = 16.0;
    private static final double RUN_SPEED_MULTIPLIER = 1.5;
    private static final double SWIM_SPEED_MULTIPLIER = 1.5;

    private static final int WATER_SEARCH_INTERVAL = 20;
    private static final int MAX_NAVIGATION_TIMEOUT = 100;
    private static final int STUCK_CHECK_INTERVAL = 20;
    private static final double STUCK_THRESHOLD = 1.0;

    public GooseHitAndRunGoal(GooseEntity goose) {
        this.goose = goose;
        this.cooldownTimer = this.getRandomCooldown();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private int getRandomCooldown() {
        return MIN_COOLDOWN + goose.getRandom().nextInt(MAX_COOLDOWN - MIN_COOLDOWN);
    }

    @Override
    public boolean canUse() {
        if (goose.isBaby()) {
            return false;
        }

        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }

        Player nearestPlayer = goose.level().getNearestPlayer(goose, ATTACK_RANGE);
        if (nearestPlayer == null || !nearestPlayer.isAlive() || nearestPlayer.isSpectator() || nearestPlayer.isCreative()) {
            return false;
        }

        if (hasRecentAttackerNearby()) {
            cooldownTimer = getRandomCooldown();
            return false;
        }

        if (!isChosenAttacker(nearestPlayer)) {
            return false;
        }

        this.targetPlayer = nearestPlayer;
        this.runAwayDistance = MIN_RUN_DISTANCE + goose.getRandom().nextDouble() * (MAX_RUN_DISTANCE - MIN_RUN_DISTANCE);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            return false;
        }

        if (isRunningAway) {
            double distanceToPlayer = goose.distanceToSqr(targetPlayer);
            return distanceToPlayer < runAwayDistance * runAwayDistance;
        }

        return true;
    }

    @Override
    public void start() {
        this.isRunningAway = false;
        updateNearestWater();
        goose.setInHitAndRunMode(true);
        this.waterExitTimer = WATER_EXIT_BOOST_INTERVAL;
    }

    @Override
    public void tick() {
        if (targetPlayer == null) {
            return;
        }

        waterSearchCooldown--;
        if (waterSearchCooldown <= 0) {
            updateNearestWater();
            waterSearchCooldown = WATER_SEARCH_INTERVAL;
        }

        stuckCheckTimer--;
        if (stuckCheckTimer <= 0) {
            checkIfStuck();
            stuckCheckTimer = STUCK_CHECK_INTERVAL;
        }

        if (goose.isInWater()) {
            if (!useDirectSwimming) {
                useDirectSwimming = true;
                goose.getNavigation().stop();
            }
        } else {
            if (useDirectSwimming) {
                useDirectSwimming = false;
            }
        }

        if (!isRunningAway) {
            if (useDirectSwimming) {
                Vec3 playerPos = targetPlayer.position();
                handleDirectSwimmingToTarget(playerPos);
            } else {
                goose.getNavigation().moveTo(targetPlayer, RUN_SPEED_MULTIPLIER);
            }

            goose.getLookControl().setLookAt(targetPlayer, 30.0F, 30.0F);

            if (goose.distanceToSqr(targetPlayer) <= 4.0) {
                goose.playSound(SillyGooseSounds.GOOSE_ATTACK, 1.0F, 1.0F);
                ResourceKey<DamageType> damageTypeKey;
                int random = goose.getRandom().nextInt(3);
                switch (random) {
                    case 0 -> damageTypeKey = SillyGooseDamageTypes.GOOSE_BOTHER;
                    case 1 -> damageTypeKey = SillyGooseDamageTypes.GOOSE_PECK;
                    default -> damageTypeKey = SillyGooseDamageTypes.GOOSE_HONK;
                }

                DamageSource damageSource = new DamageSource(
                        goose.level().registryAccess()
                                .lookupOrThrow(Registries.DAMAGE_TYPE)
                                .get(damageTypeKey.identifier()).get(),
                        goose
                );

                float damage = (float) goose.getAttributeValue(Attributes.ATTACK_DAMAGE);
                targetPlayer.hurtServer((ServerLevel) goose.level(), damageSource, damage);

                if (targetPlayer instanceof ServerPlayer serverPlayer) {
                    ItemStack mainhandItem = goose.getItemBySlot(EquipmentSlot.MAINHAND);
                    if (!mainhandItem.isEmpty()) {
                        HitandRunAdvancementHandler.grantHitandRunAdvancement(serverPlayer);
                    } else {
                        HonkandRunAdvancementHandler.grantHonkandRunAdvancement(serverPlayer);
                    }
                }

                isRunningAway = true;
                setupEscapeTarget();
                notifyNearbyGeese();
            }
        } else {
            if (targetPos != null) {
                navigationTimeout--;

                double distanceToTarget = goose.position().distanceTo(targetPos);
                if (distanceToTarget < 2.0 || goose.distanceToSqr(targetPlayer) >= runAwayDistance * runAwayDistance) {
                    targetPos = null;
                    goose.getNavigation().stop();
                    useDirectSwimming = false;
                    return;
                }

                if (useDirectSwimming) {
                    handleDirectSwimming();
                } else {
                    if (navigationTimeout % 20 == 0 && distanceToTarget > 3.0) {
                        goose.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, RUN_SPEED_MULTIPLIER);
                    }
                }

                if (navigationTimeout <= 0) {
                    setupEscapeTarget();
                }
            } else {
                setupEscapeTarget();
            }
        }
        checkAndApplyWaterExitBoost();
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.isRunningAway = false;
        this.cooldownTimer = getRandomCooldown();
        this.targetPos = null;
        this.useDirectSwimming = false;
        goose.getNavigation().stop();
        goose.setInHitAndRunMode(false);
    }

    private void setupEscapeTarget() {
        if (targetPlayer == null) return;

        double dx = goose.getX() - targetPlayer.getX();
        double dz = goose.getZ() - targetPlayer.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 0.1) {
            double angle = goose.getRandom().nextDouble() * Math.PI * 2.0;
            dx = Math.cos(angle);
            dz = Math.sin(angle);
            distance = 1.0;
        }

        double escapeIncrement = goose.isInWater() ? 8.0 : runAwayDistance;
        dx = (dx / distance) * escapeIncrement;
        dz = (dz / distance) * escapeIncrement;

        double targetX = goose.getX() + dx;
        double targetZ = goose.getZ() + dz;
        double targetY = goose.getY();

        BlockPos targetBlockPos = BlockPos.containing(targetX, targetY, targetZ);
        boolean targetIsWater = goose.level().getFluidState(targetBlockPos).is(net.minecraft.tags.FluidTags.WATER);

        if (targetIsWater || goose.isInWater()) {
            BlockPos checkPos = goose.isInWater() ? goose.blockPosition() : targetBlockPos;

            checkPos = BlockPos.containing(targetX, checkPos.getY(), targetZ);
            while (goose.level().getFluidState(checkPos).is(net.minecraft.tags.FluidTags.WATER)) {
                targetY = checkPos.getY() + 1.0;
                checkPos = checkPos.above();
            }
        }

        setTarget(new Vec3(targetX, targetY, targetZ));
    }

    private void checkAndApplyWaterExitBoost() {
        waterExitTimer--;
        if (waterExitTimer <= 0) {
            waterExitTimer = WATER_EXIT_BOOST_INTERVAL;

            if (goose.isInWater()) {
                BlockPos abovePos = goose.blockPosition().above();

                if (!goose.level().getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER)) {
                    boolean hasNearbyLand = false;
                    BlockPos currentPos = goose.blockPosition();

                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && z == 0) continue;

                            BlockPos checkPos = currentPos.offset(x, 0, z);
                            BlockPos checkPosAbove = currentPos.offset(x, 1, z);

                            boolean isLand = !goose.level().getFluidState(checkPos).is(net.minecraft.tags.FluidTags.WATER);
                            boolean isLandAbove = !goose.level().getFluidState(checkPosAbove).is(net.minecraft.tags.FluidTags.WATER);

                            if (isLand || isLandAbove) {
                                hasNearbyLand = true;
                                break;
                            }
                        }
                        if (hasNearbyLand) break;
                    }

                    if (hasNearbyLand) {
                        Vec3 velocity = goose.getDeltaMovement();
                        goose.setDeltaMovement(velocity.x, WATER_EXIT_BOOST_STRENGTH, velocity.z);
                        goose.needsSync = true;
                    }
                }
            }
        }
    }

    private void handleDirectSwimming() {
        if (targetPos == null) return;

        Vec3 currentPos = goose.position();
        Vec3 direction = targetPos.subtract(currentPos).normalize();

        double targetYaw = Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI);
        float currentYaw = goose.getYRot();

        while (targetYaw > 180) targetYaw -= 360;
        while (targetYaw < -180) targetYaw += 360;
        while (currentYaw > 180) currentYaw -= 360;
        while (currentYaw < -180) currentYaw += 360;

        float yawDiff = (float)(targetYaw - currentYaw);
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float maxRotation = 15.0f;
        float yawAdjustment = Math.max(-maxRotation, Math.min(maxRotation, yawDiff));
        float newYaw = currentYaw + yawAdjustment;

        goose.setYRot(newYaw);
        goose.setYBodyRot(newYaw);
        goose.setYHeadRot(newYaw);

        if (Math.abs(yawDiff) < 45) {
            double swimSpeed = SWIM_SPEED_MULTIPLIER * 0.15;
            Vec3 velocity = goose.getDeltaMovement();

            goose.setDeltaMovement(
                    direction.x * swimSpeed,
                    velocity.y,
                    direction.z * swimSpeed
            );
            goose.needsSync = true;
        } else {
            Vec3 velocity = goose.getDeltaMovement();
            goose.setDeltaMovement(
                    velocity.x * 0.5,
                    velocity.y,
                    velocity.z * 0.5
            );
            goose.needsSync = true;
        }
    }

    private void handleDirectSwimmingToTarget(Vec3 target) {
        Vec3 currentPos = goose.position();
        Vec3 direction = target.subtract(currentPos).normalize();

        double targetYaw = Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI);
        float currentYaw = goose.getYRot();

        while (targetYaw > 180) targetYaw -= 360;
        while (targetYaw < -180) targetYaw += 360;
        while (currentYaw > 180) currentYaw -= 360;
        while (currentYaw < -180) currentYaw += 360;

        float yawDiff = (float)(targetYaw - currentYaw);
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float maxRotation = 15.0f;
        float yawAdjustment = Math.max(-maxRotation, Math.min(maxRotation, yawDiff));
        float newYaw = currentYaw + yawAdjustment;

        goose.setYRot(newYaw);
        goose.setYBodyRot(newYaw);
        goose.setYHeadRot(newYaw);

        if (Math.abs(yawDiff) < 45) {
            double swimSpeed = SWIM_SPEED_MULTIPLIER * 0.15;
            Vec3 velocity = goose.getDeltaMovement();

            goose.setDeltaMovement(
                    direction.x * swimSpeed,
                    velocity.y,
                    direction.z * swimSpeed
            );
            goose.needsSync = true;
        } else {
            Vec3 velocity = goose.getDeltaMovement();
            goose.setDeltaMovement(
                    velocity.x * 0.5,
                    velocity.y,
                    velocity.z * 0.5
            );
            goose.needsSync = true;
        }
    }

    private void updateNearestWater() {
        BlockPos goosePos = goose.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                goosePos.offset(-32, -16, -32),
                goosePos.offset(32, 16, 32))) {
            if (goose.level().getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) {
                double distance = goosePos.distSqr(pos);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = pos.immutable();
                }
            }
        }

        this.nearestWaterPos = nearest;
    }

    private void setTarget(Vec3 target) {
        this.targetPos = target;
        this.navigationTimeout = MAX_NAVIGATION_TIMEOUT;

        if (goose.isInWater()) {
            useDirectSwimming = true;
            goose.getNavigation().stop();
        } else {
            useDirectSwimming = false;
            this.goose.getNavigation().moveTo(target.x, target.y, target.z, RUN_SPEED_MULTIPLIER);
        }
    }

    private void checkIfStuck() {
        BlockPos currentPos = goose.blockPosition();

        if (lastPos != null && targetPos != null) {
            double distanceMoved = currentPos.distSqr(lastPos);

            if (distanceMoved < STUCK_THRESHOLD) {
                targetPos = null;
                goose.getNavigation().stop();
            }
        }

        lastPos = currentPos.immutable();
    }

    private boolean hasRecentAttackerNearby() {
        AABB searchBox = goose.getBoundingBox().inflate(DETECTION_RADIUS);
        List<GooseEntity> nearbyGeese = goose.level().getEntitiesOfClass(
                GooseEntity.class,
                searchBox,
                g -> g != goose && g.isAlive()
        );

        for (GooseEntity otherGoose : nearbyGeese) {
            if (otherGoose.getNavigation().isInProgress() && isGooseAttacking(otherGoose)) {
                return true;
            }
        }

        return false;
    }

    private boolean isGooseAttacking(GooseEntity otherGoose) {
        if (!otherGoose.getNavigation().isInProgress()) {
            return false;
        }

        Player nearPlayer = otherGoose.level().getNearestPlayer(otherGoose, DETECTION_RADIUS);
        if (nearPlayer != null) {
            double dist = otherGoose.distanceToSqr(nearPlayer);
            return dist < MAX_RUN_DISTANCE * MAX_RUN_DISTANCE;
        }

        return false;
    }

    private boolean isChosenAttacker(Player player) {
        AABB searchBox = goose.getBoundingBox().inflate(DETECTION_RADIUS);
        List<GooseEntity> nearbyGeese = goose.level().getEntitiesOfClass(
                GooseEntity.class,
                searchBox,
                g -> g.isAlive() && !g.isBaby() && g.distanceToSqr(player) <= ATTACK_RANGE * ATTACK_RANGE
        );

        if (nearbyGeese.isEmpty()) {
            return true;
        }

        ItemStack ourMainhand = goose.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean weHaveWeapon = !ourMainhand.isEmpty() && ourMainhand.is(ItemTags.SWORDS);

        int geeseWithWeapons = 0;
        int totalEligibleGeese = 0;

        for (GooseEntity otherGoose : nearbyGeese) {
            totalEligibleGeese++;
            ItemStack theirMainhand = otherGoose.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!theirMainhand.isEmpty() && theirMainhand.is(ItemTags.SWORDS)) {
                geeseWithWeapons++;
            }
        }

        if (weHaveWeapon) {
            if (geeseWithWeapons > 0) {
                return goose.getRandom().nextInt(geeseWithWeapons + totalEligibleGeese - geeseWithWeapons) < geeseWithWeapons;
            }
            return goose.getRandom().nextFloat() < 0.75f;
        }

        if (geeseWithWeapons > 0) {
            return goose.getRandom().nextFloat() < 0.25f / totalEligibleGeese;
        }

        return goose.getRandom().nextInt(totalEligibleGeese) == 0;
    }

    private void notifyNearbyGeese() {
        AABB searchBox = goose.getBoundingBox().inflate(DETECTION_RADIUS);
        List<GooseEntity> nearbyGeese = goose.level().getEntitiesOfClass(
                GooseEntity.class,
                searchBox,
                g -> g != goose && g.isAlive()
        );

        for (GooseEntity otherGoose : nearbyGeese) {
        }
    }
}
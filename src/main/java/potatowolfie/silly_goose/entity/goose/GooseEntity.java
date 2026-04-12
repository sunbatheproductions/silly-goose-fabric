package potatowolfie.silly_goose.entity.goose;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import potatowolfie.silly_goose.advancement.UnfairTradeAdvancementHandler;
import potatowolfie.silly_goose.entity.SillyGooseEntities;
import potatowolfie.silly_goose.entity.goose.goals.*;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariant;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariants;
import potatowolfie.silly_goose.item.SillyGooseItems;
import potatowolfie.silly_goose.registry.SillyGooseDataComponentTypes;
import potatowolfie.silly_goose.registry.SillyGooseRegistryKeys;
import potatowolfie.silly_goose.registry.SillyGooseTrackedDataHandlerRegistry;
import potatowolfie.silly_goose.sound.SillyGooseSounds;

import java.util.List;
import java.util.UUID;

public class GooseEntity extends Animal {
    private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.scalable(0.3F, 0.6F).withEyeHeight(0.48F);

    private static final EntityDataAccessor<Holder<GooseVariant>> VARIANT;
    private static final EntityDataAccessor<Boolean> PREFERS_WATER;
    private static final EntityDataAccessor<Boolean> CAN_PICKUP_LOOT;
    private static final EntityDataAccessor<Boolean> IS_IN_HIT_AND_RUN_MODE;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState idleWaterAnimationState = new AnimationState();
    public final AnimationState wingsUpIdleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState swimFastAnimationState = new AnimationState();

    public final AnimationState babyIdleAnimationState = new AnimationState();
    public final AnimationState babyIdleWaterAnimationState = new AnimationState();
    public final AnimationState babyWingsUpIdleAnimationState = new AnimationState();
    public final AnimationState babyWalkAnimationState = new AnimationState();
    public final AnimationState babyRunAnimationState = new AnimationState();
    public final AnimationState babySwimAnimationState = new AnimationState();
    public final AnimationState babySwimFastAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private boolean isIdleAnimationRunning = false;
    private boolean isWalkingAnimationRunning = false;
    private boolean animationStartedThisTick = false;

    private int preferenceChangeTimer = 0;
    private static final int MIN_PREFERENCE_CHANGE_TIME = 4800;
    private static final int MAX_PREFERENCE_CHANGE_TIME = 7200;

    private static final float CHANCE_OF_PICK_UP_WEAPON = 1.0F;
    private int swordPickupCooldownTimer = 0;

    private int offPreferenceTimer = 0;
    private boolean isInOffPreferenceMode = false;
    private static final int MIN_OFF_PREFERENCE_TIME = 600;
    private static final int MAX_OFF_PREFERENCE_TIME = 1200;

    private int eggLayTimer = 0;
    private static final int MIN_EGG_LAY_TIME = 6000;
    private static final int MAX_EGG_LAY_TIME = 12000;

    private int babyPanicTimer = 0;
    private static final int BABY_PANIC_DURATION = 100;
    private static final int BABY_PANIC_DURATION_VARIANCE = 40;
    private static final double BABY_PANIC_SPEED_MULTIPLIER = 1.5;

    @Nullable
    private UUID revengeTargetUUID = null;
    private int revengeTimer = 0;
    private long revengeExpirationTime = 0;
    private static final int MIN_REVENGE_TIMER = 1200;
    private static final int MAX_REVENGE_TIMER = 2400;
    private static final long MIN_REVENGE_DURATION_MS = 2 * 60 * 60 * 1000L;
    private static final long MAX_REVENGE_DURATION_MS = 200 * 60 * 1000L;
    private static final double REVENGE_NOTIFICATION_RADIUS = 64.0;

    public GooseEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new GooseRevengeGoal(this));
        this.goalSelector.addGoal(1, new GooseHitAndRunGoal(this));
        this.goalSelector.addGoal(2, new GooseStealFromVillagerGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1, stack -> stack.is(ItemTags.CHICKEN_FOOD), false));
        this.goalSelector.addGoal(6, new BabyGooseFollowGoal(this, 1.1));
        this.goalSelector.addGoal(7, new GooseWanderGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createGooseAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.JUMP_STRENGTH, 0.42)
                .add(Attributes.TEMPT_RANGE, 10.0);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, VariantUtils.getDefaultOrAny(this.registryAccess(), GooseVariants.TEMPERATE));
        builder.define(PREFERS_WATER, this.random.nextBoolean());
        builder.define(CAN_PICKUP_LOOT, false);
        builder.define(IS_IN_HIT_AND_RUN_MODE, false);
    }

    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        VariantUtils.writeVariant(view, this.getVariant());
        view.putBoolean("PrefersWater", this.getPrefersWater());
        view.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        view.putInt("PreferenceChangeTimer", this.preferenceChangeTimer);
        view.putInt("OffPreferenceTimer", this.offPreferenceTimer);
        view.putBoolean("IsInOffPreferenceMode", this.isInOffPreferenceMode);
        view.putInt("EggLayTimer", this.eggLayTimer);
        view.putInt("SwordPickupCooldownTimer", this.swordPickupCooldownTimer);
        view.putInt("BabyPanicTimer", this.babyPanicTimer);

        if (this.revengeTargetUUID != null) {
            view.store("RevengeTargetUUID", UUIDUtil.AUTHLIB_CODEC, this.revengeTargetUUID);
        }
        view.putInt("RevengeTimer", this.revengeTimer);
        view.putLong("RevengeExpirationTime", this.revengeExpirationTime);
    }

    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        if (!spawnedFromEgg) {
            VariantUtils.readVariant(view, SillyGooseRegistryKeys.GOOSE_VARIANT).ifPresent(this::setVariant);
        }

        boolean savedPreference = view.getBooleanOr("PrefersWater", this.random.nextBoolean());
        this.setPrefersWater(savedPreference);

        boolean canPickup = view.getBooleanOr("CanPickUpLoot", false);
        this.setCanPickUpLoot(canPickup);
        this.preferenceChangeTimer = view.getIntOr("PreferenceChangeTimer", 0);
        this.offPreferenceTimer = view.getIntOr("OffPreferenceTimer", 0);
        this.isInOffPreferenceMode = view.getBooleanOr("IsInOffPreferenceMode", false);
        this.eggLayTimer = view.getIntOr("EggLayTimer", 0);
        this.swordPickupCooldownTimer = view.getIntOr("SwordPickupCooldownTimer", 0);
        this.babyPanicTimer = view.getIntOr("BabyPanicTimer", 0);

        view.read("RevengeTargetUUID", UUIDUtil.AUTHLIB_CODEC).ifPresent(uuid -> this.revengeTargetUUID = uuid);
        this.revengeTimer = view.getIntOr("RevengeTimer", 0);
        this.revengeExpirationTime = view.getLongOr("RevengeExpirationTime", 0L);
    }

    private boolean spawnedFromEgg = false;
    public void setSpawnedFromEgg(boolean spawnedFromEgg) {
        this.spawnedFromEgg = spawnedFromEgg;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            if (this.isBaby()) {
                this.idleAnimationState.stop();
                this.idleWaterAnimationState.stop();
                this.walkAnimationState.stop();
                this.runAnimationState.stop();
                this.swimAnimationState.stop();
                this.swimFastAnimationState.stop();

                boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;

                this.babyIdleAnimationState.animateWhen(!this.isInWater() && !this.isPanicking() && !isMoving, this.tickCount);

                if (this.isInWater()) {
                    this.babyIdleWaterAnimationState.animateWhen(!this.isPanicking() && !isMoving, this.tickCount);

                    if (isMoving) {
                        if (this.isPanicking()) {
                            this.babySwimFastAnimationState.startIfStopped(this.tickCount);
                            this.babySwimAnimationState.stop();
                        } else {
                            this.babySwimAnimationState.startIfStopped(this.tickCount);
                            this.babySwimFastAnimationState.stop();
                        }
                    } else {
                        this.babySwimAnimationState.stop();
                        this.babySwimFastAnimationState.stop();
                    }
                } else {
                    this.babyIdleWaterAnimationState.stop();
                    if (isMoving) {
                        if (this.isPanicking()) {
                            this.babyRunAnimationState.startIfStopped(this.tickCount);
                            this.babyWalkAnimationState.stop();
                        } else {
                            this.babyWalkAnimationState.startIfStopped(this.tickCount);
                            this.babyRunAnimationState.stop();
                        }
                    } else {
                        this.babyWalkAnimationState.stop();
                        this.babyRunAnimationState.stop();
                    }
                }
            } else {
                this.babyIdleAnimationState.stop();
                this.babyIdleWaterAnimationState.stop();
                this.babyWalkAnimationState.stop();
                this.babyRunAnimationState.stop();
                this.babySwimAnimationState.stop();
                this.babySwimFastAnimationState.stop();

                boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;

                this.idleAnimationState.animateWhen(!this.isInWater() && !this.isPanicking() && !isMoving, this.tickCount);

                if (this.isInWater()) {
                    this.idleWaterAnimationState.animateWhen(!this.isPanicking() && !isMoving, this.tickCount);
                    if (isMoving) {
                        if (this.isPanicking()) {
                            this.swimFastAnimationState.startIfStopped(this.tickCount);
                            this.swimAnimationState.stop();
                        } else {
                            this.swimAnimationState.startIfStopped(this.tickCount);
                            this.swimFastAnimationState.stop();
                        }
                    } else {
                        this.swimAnimationState.stop();
                        this.swimFastAnimationState.stop();
                    }
                } else {
                    this.idleWaterAnimationState.stop();
                    if (isMoving) {
                        if (this.isPanicking()) {
                            this.runAnimationState.startIfStopped(this.tickCount);
                            this.walkAnimationState.stop();
                        } else {
                            this.walkAnimationState.startIfStopped(this.tickCount);
                            this.runAnimationState.stop();
                        }
                    } else {
                        this.walkAnimationState.stop();
                        this.runAnimationState.stop();
                    }
                }
            }
        }
    }

    private void handleRevengeTimer() {
        if (revengeTargetUUID == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (revengeExpirationTime > 0 && currentTime >= revengeExpirationTime) {
            revengeTargetUUID = null;
            revengeTimer = 0;
            revengeExpirationTime = 0;
            return;
        }

        Player targetPlayer = getRevengeTargetPlayer();
        if (targetPlayer != null) {
            double distance = this.distanceToSqr(targetPlayer);

            if (distance <= 32.0 * 32.0) {
                if (revengeTimer > 0) {
                    revengeTimer--;
                }
            }
        }
    }

    private void handleBabyPanic() {
        if (this.babyPanicTimer > 0) {
            this.babyPanicTimer--;
        }
    }

    public boolean isInPanicMode() {
        return this.isBaby() && this.babyPanicTimer > 0;
    }

    @Override
    public float getSpeed() {
        float baseSpeed = super.getSpeed();
        if (isInPanicMode()) {
            return (float)(baseSpeed * BABY_PANIC_SPEED_MULTIPLIER);
        }
        return baseSpeed;
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        boolean damaged = super.hurtServer(world, source, amount);

        if (!damaged) {
            return false;
        }

        Entity attacker = source.getEntity();
        if (this.isBaby() && attacker instanceof Player player) {
            this.getNavigation().stop();
            this.getJumpControl().jump();
            this.getLookControl().setLookAt(player, 180.0F, 180.0F);
            this.getMoveControl().setWantedPosition(
                    this.getX() + (this.random.nextDouble() - 0.5) * 4.0,
                    this.getY(),
                    this.getZ() + (this.random.nextDouble() - 0.5) * 4.0,
                    1.6
            );

            this.babyPanicTimer = BABY_PANIC_DURATION +
                    this.random.nextInt(BABY_PANIC_DURATION_VARIANCE);

            alertAdultsOfBabyAttack(player);
        }
        if (damaged && this.isDeadOrDying() && attacker instanceof Player player) {
            notifyNearbyGeese(player);
        }

        return damaged;
    }

    private void alertAdultsOfBabyAttack(Player attacker) {
        AABB box = this.getBoundingBox().inflate(REVENGE_NOTIFICATION_RADIUS);

        List<GooseEntity> adults = this.level().getEntitiesOfClass(
                GooseEntity.class,
                box,
                goose -> goose != this && goose.isAlive() && !goose.isBaby()
        );

        for (GooseEntity adult : adults) {
            adult.setRevengeTarget(attacker);
        }
    }

    private void notifyNearbyGeese(Player killer) {
        AABB searchBox = this.getBoundingBox().inflate(REVENGE_NOTIFICATION_RADIUS);
        List<GooseEntity> nearbyGeese = this.level().getEntitiesOfClass(
                GooseEntity.class,
                searchBox,
                g -> g != this && g.isAlive()
        );

        for (GooseEntity goose : nearbyGeese) {
            goose.setRevengeTarget(killer);
        }
    }

    public void setRevengeTarget(Player player) {
        this.revengeTargetUUID = player.getUUID();
        this.revengeTimer = MIN_REVENGE_TIMER + this.random.nextInt(MAX_REVENGE_TIMER - MIN_REVENGE_TIMER);

        long durationMs = MIN_REVENGE_DURATION_MS +
                (long)(this.random.nextDouble() * (MAX_REVENGE_DURATION_MS - MIN_REVENGE_DURATION_MS));
        this.revengeExpirationTime = System.currentTimeMillis() + durationMs;
    }

    public boolean hasRevengeTarget() {
        if (revengeTargetUUID == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (revengeExpirationTime > 0 && currentTime >= revengeExpirationTime) {
            return false;
        }

        return true;
    }

    public boolean isRevengeTimerReady() {
        return revengeTimer <= 0;
    }

    public void resetRevengeTimer() {
        this.revengeTimer = MIN_REVENGE_TIMER + this.random.nextInt(MAX_REVENGE_TIMER - MIN_REVENGE_TIMER);
    }

    @Nullable
    public Player getRevengeTargetPlayer() {
        if (revengeTargetUUID == null) {
            return null;
        }

        if (this.level() instanceof ServerLevel serverWorld) {
            return serverWorld.getPlayerByUUID(revengeTargetUUID);
        }

        return null;
    }

    private void handleEggLaying() {
        if (this.isBaby()) {
            return;
        }

        if (this.eggLayTimer <= 0) {
            this.layEgg();

            this.eggLayTimer = MIN_EGG_LAY_TIME +
                    this.random.nextInt(MAX_EGG_LAY_TIME - MIN_EGG_LAY_TIME);
        } else {
            this.eggLayTimer--;
        }
    }

    private void layEgg() {
        ItemStack eggStack = getEggForVariant();

        if (!eggStack.isEmpty()) {
            ItemEntity eggEntity = new ItemEntity(
                    this.level(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    eggStack
            );

            eggEntity.setDeltaMovement(0, 0.05, 0);

            this.level().addFreshEntity(eggEntity);

            this.playSound(SoundEvents.CHICKEN_EGG, 0.5F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    private ItemStack getEggForVariant() {
        Holder<GooseVariant> variant = this.getVariant();

        if (variant.is(GooseVariants.TEMPERATE)) {
            return new ItemStack(SillyGooseItems.WHITE_EGG);
        } else if (variant.is(GooseVariants.COLD)) {
            return new ItemStack(SillyGooseItems.BIG_WHITE_EGG);
        } else if (variant.is(GooseVariants.WARM)) {
            return new ItemStack(SillyGooseItems.SMALL_WHITE_EGG);
        }

        return new ItemStack(SillyGooseItems.WHITE_EGG);
    }

    public static boolean canSpawn(EntityType<GooseEntity> type, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (!Animal.checkAnimalSpawnRules(type, world, spawnReason, pos, random)) {
            return false;
        }

        return hasWaterNearby(world, pos, 12, 5);
    }

    private static boolean hasWaterNearby(ServerLevelAccessor world, BlockPos center, int horizontalRadius, int verticalRange) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int waterBlocksFound = 0;
        int requiredWaterBlocks = 6;

        for (int x = -horizontalRadius; x <= horizontalRadius; x += 2) {
            for (int z = -horizontalRadius; z <= horizontalRadius; z += 2) {
                for (int y = -verticalRange; y <= verticalRange; y++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    if (world.getFluidState(mutable).is(FluidTags.WATER)) {
                        waterBlocksFound++;

                        if (waterBlocksFound >= requiredWaterBlocks) {
                            return true;
                        }
                    }
                    else if (world.getBlockState(mutable).is(BlockTags.ICE)) {
                        BlockPos below = mutable.below();
                        if (world.getFluidState(below).is(FluidTags.WATER)) {
                            waterBlocksFound++;

                            if (waterBlocksFound >= requiredWaterBlocks) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SillyGooseSounds.GOOSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SillyGooseSounds.GOOSE_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SillyGooseSounds.GOOSE_HONK;
    }

    @Override
    public EntityDimensions getDefaultDimensions(final Pose pose) {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    private void handlePreferenceChange() {
        if (this.isBaby()) {
            return;
        }

        if (this.preferenceChangeTimer <= 0) {
            if (this.random.nextFloat() < 0.75F) {
                this.setPrefersWater(!this.getPrefersWater());
            }
            this.preferenceChangeTimer = MIN_PREFERENCE_CHANGE_TIME +
                    this.random.nextInt(MAX_PREFERENCE_CHANGE_TIME - MIN_PREFERENCE_CHANGE_TIME);
        } else {
            this.preferenceChangeTimer--;
        }
    }

    private void handleOffPreferenceTimer() {
        boolean prefersWater = this.getPrefersWater();
        boolean inWater = this.isInWater();
        boolean inOffPreferenceZone = (prefersWater && !inWater) || (!prefersWater && inWater);

        if (inOffPreferenceZone) {
            if (!this.isInOffPreferenceMode) {
                this.isInOffPreferenceMode = true;
                this.offPreferenceTimer = MIN_OFF_PREFERENCE_TIME +
                        this.random.nextInt(MAX_OFF_PREFERENCE_TIME - MIN_OFF_PREFERENCE_TIME);
            } else {
                this.offPreferenceTimer--;
            }
        } else {
            this.isInOffPreferenceMode = false;
            this.offPreferenceTimer = 0;
        }
    }

    public boolean shouldReturnToPreference() {
        return this.isInOffPreferenceMode && this.offPreferenceTimer <= 0;
    }

    private void handleWaterFloating() {
        if (!this.isInWater() || this.isPassenger()) {
            return;
        }

        BlockPos pos = this.blockPosition();

        double entityHeight = this.isBaby() ? 1.375 * 0.7 : 1.375;
        int topBlockOffset = (int) Math.ceil(entityHeight);
        BlockPos airCheckPos = pos.above(topBlockOffset);

        boolean hasAirAbove = !this.level().getFluidState(airCheckPos).is(FluidTags.WATER) &&
                this.level().getBlockState(airCheckPos).isAir();

        if (!hasAirAbove) {
            Vec3 velocity = this.getDeltaMovement();
            double surfaceBoost = this.isBaby() ? 0.12 : 0.10;
            this.setDeltaMovement(velocity.x, surfaceBoost, velocity.z);
            this.needsSync = true;
            return;
        }

        double waterSurfaceY = pos.getY() + 1.0;
        BlockPos checkPos = new BlockPos((int)this.getX(), (int)waterSurfaceY, (int)this.getZ());
        while (this.level().getFluidState(checkPos).is(FluidTags.WATER)) {
            waterSurfaceY += 1.0;
            checkPos = new BlockPos((int)this.getX(), (int)waterSurfaceY, (int)this.getZ());
        }

        float floatOffset = this.isBaby() ? (3.75F / 16.0F) : (5.5F / 16.0F);
        double targetY = waterSurfaceY - floatOffset;
        double currentY = this.getY();
        double yDiff = targetY - currentY;

        Vec3 velocity = this.getDeltaMovement();
        boolean isSwimming = Math.abs(velocity.x) > 0.02 || Math.abs(velocity.z) > 0.02;

        if (isSwimming) {
            boolean nearLand = false;
            BlockPos[] checkPositions = {
                    pos.north(), pos.south(), pos.east(), pos.west()
            };

            for (BlockPos landCheck : checkPositions) {
                if (!this.level().getFluidState(landCheck).is(FluidTags.WATER) &&
                        this.level().getBlockState(landCheck).isRedstoneConductor(this.level(), landCheck)) {
                    nearLand = true;
                    break;
                }
                BlockPos upOne = landCheck.above();
                if (!this.level().getFluidState(upOne).is(FluidTags.WATER) &&
                        this.level().getBlockState(upOne).isRedstoneConductor(this.level(), upOne)) {
                    nearLand = true;
                    break;
                }
            }

            if (nearLand) {
                double boostStrength = this.isBaby() ? 0.15 : 0.14;
                this.setDeltaMovement(velocity.x, Math.max(velocity.y, boostStrength), velocity.z);
                this.needsSync = true;
                return;
            }
        }

        if (!this.onGround() && Math.abs(yDiff) < 2.0) {
            double newYVelocity;

            if (yDiff > 0.1) {
                newYVelocity = Math.min(0.08, yDiff * 0.1);
            } else if (yDiff < -0.1) {
                newYVelocity = Math.max(-0.03, yDiff * 0.1);
            } else {
                newYVelocity = velocity.y * 0.5;
            }

            if (isSwimming) {
                newYVelocity = velocity.y * 0.8 + newYVelocity * 0.2;
            }

            this.setDeltaMovement(velocity.x, newYVelocity, velocity.z);
            this.needsSync = true;
        }
    }

    private void setupAnimationStates() {
        boolean inWater = this.isInWater();
        boolean isMovingHorizontally = this.getDeltaMovement().horizontalDistanceSqr() > 0.001;
        boolean isInHitAndRun = this.isInHitAndRunMode();
        boolean isRunning = isMovingHorizontally && isInHitAndRun;

        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 2;

            AnimationState targetAnimation = null;

            if (inWater) {
                if (isRunning) {
                    targetAnimation = this.swimFastAnimationState;
                } else if (isMovingHorizontally) {
                    targetAnimation = this.swimAnimationState;
                } else {
                    targetAnimation = this.idleWaterAnimationState;
                }
            } else {
                if (isRunning) {
                    targetAnimation = this.runAnimationState;
                } else if (isMovingHorizontally) {
                    targetAnimation = this.walkAnimationState;
                } else if (isInHitAndRun) {
                    targetAnimation = this.wingsUpIdleAnimationState;
                } else {
                    targetAnimation = this.idleAnimationState;
                }
            }

            if (targetAnimation != null && !targetAnimation.isStarted()) {
                this.idleAnimationState.stop();
                this.idleWaterAnimationState.stop();
                this.walkAnimationState.stop();
                this.runAnimationState.stop();
                this.swimAnimationState.stop();
                this.swimFastAnimationState.stop();
                this.wingsUpIdleAnimationState.stop();

                targetAnimation.start(this.tickCount);
            }
        } else {
            --this.idleAnimationTimeout;
        }
    }

    public boolean isInHitAndRunMode() {
        return this.entityData.get(IS_IN_HIT_AND_RUN_MODE);
    }

    public void setInHitAndRunMode(boolean inMode) {
        this.entityData.set(IS_IN_HIT_AND_RUN_MODE, inMode);
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        if (this.isBaby()) {
            return false;
        }

        EquipmentSlot equipmentSlot = this.getEquipmentSlotForItem(stack);
        if (!this.getItemBySlot(equipmentSlot).isEmpty()) {
            return false;
        }
        return equipmentSlot == EquipmentSlot.MAINHAND &&
                stack.is(ItemTags.SWORDS) &&
                this.canPickUpLoot();
    }

    @Override
    public boolean canPickUpLoot() {
        return this.entityData.get(CAN_PICKUP_LOOT);
    }

    public void setCanPickUpLoot(boolean canPickUpLoot) {
        this.entityData.set(CAN_PICKUP_LOOT, canPickUpLoot);
    }

    @Override
    protected void pickUpItem(ServerLevel world, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (this.canHoldItem(stack)) {
            this.onItemPickup(itemEntity);
            this.setItemSlot(EquipmentSlot.MAINHAND, stack.split(1));
            this.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
            this.take(itemEntity, stack.getCount());
            if (stack.isEmpty()) {
                itemEntity.discard();
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if (stackInHand.is(ItemTags.CHICKEN_FOOD) &&
                !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() &&
                this.getItemBySlot(EquipmentSlot.MAINHAND).is(ItemTags.SWORDS)) {

            if (!this.level().isClientSide()) {
                ItemStack sword = this.getItemBySlot(EquipmentSlot.MAINHAND).copy();
                ItemEntity itemEntity = new ItemEntity(
                        this.level(),
                        this.getX(),
                        this.getY() + 0.5,
                        this.getZ(),
                        sword
                );
                this.level().addFreshEntity(itemEntity);
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

                this.setCanPickUpLoot(false);
                this.swordPickupCooldownTimer = 400;
                if (!player.getAbilities().instabuild) {
                    stackInHand.shrink(1);
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    UnfairTradeAdvancementHandler.grantUnfairTradeAdvancement(serverPlayer);
                }
                this.playSound(SoundEvents.GENERIC_EAT.value(), 1.0F, 1.0F);
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private void handleSwordPickupCooldown() {
        if (this.swordPickupCooldownTimer > 0) {
            this.swordPickupCooldownTimer--;

            if (this.swordPickupCooldownTimer <= 0) {
                this.setCanPickUpLoot(true);
            }
        }
    }

    @Nullable
    public GooseEntity getBreedOffspring(ServerLevel serverWorld, AgeableMob passiveEntity) {
        GooseEntity gooseEntity = (GooseEntity)SillyGooseEntities.GOOSE.create(serverWorld, EntitySpawnReason.BREEDING);
        if (gooseEntity != null && passiveEntity instanceof GooseEntity gooseEntity2) {
            gooseEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : gooseEntity2.getVariant());
            gooseEntity.setPrefersWater(this.getPrefersWater());
        }

        return gooseEntity;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason != EntitySpawnReason.TRIGGERED && spawnReason != EntitySpawnReason.BREEDING && spawnReason != EntitySpawnReason.COMMAND) {
            VariantUtils.selectVariantToSpawn(SpawnContext.create(world, this.blockPosition()), SillyGooseRegistryKeys.GOOSE_VARIANT)
                    .ifPresent(this::setVariant);
        }

        if (!this.isBaby()) {
            this.preferenceChangeTimer = MIN_PREFERENCE_CHANGE_TIME +
                    this.random.nextInt(MAX_PREFERENCE_CHANGE_TIME - MIN_PREFERENCE_CHANGE_TIME);

            this.setCanPickUpLoot(this.random.nextFloat() < CHANCE_OF_PICK_UP_WEAPON);

            this.eggLayTimer = MIN_EGG_LAY_TIME +
                    this.random.nextInt(MAX_EGG_LAY_TIME - MIN_EGG_LAY_TIME);

            if (spawnReason == EntitySpawnReason.NATURAL && this.random.nextFloat() < 0.3F) {
                entityData = new GooseGroupData(true);
            }

            if (this.random.nextFloat() < 0.05F) {
                float swordRoll = this.random.nextFloat();
                ItemStack spawnSword;
                if (swordRoll < 0.50F) {
                    spawnSword = new ItemStack(Items.WOODEN_SWORD);
                } else if (swordRoll < 0.85F) {
                    spawnSword = new ItemStack(Items.STONE_SWORD);
                } else {
                    spawnSword = new ItemStack(Items.IRON_SWORD);
                }
                int maxDamage = spawnSword.getMaxDamage();
                int damage = this.random.nextInt((int)(maxDamage * 0.25F) + 1);
                spawnSword.setDamageValue(damage);
                this.setItemSlot(EquipmentSlot.MAINHAND, spawnSword);
                this.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
            }
        }

        if (entityData instanceof GooseGroupData groupData && groupData.shouldSpawnBabies()) {
            int babyCount = 1 + this.random.nextInt(3);

            for (int i = 0; i < babyCount; i++) {
                GooseEntity baby = (GooseEntity) SillyGooseEntities.GOOSE.create(world.getLevel(), EntitySpawnReason.NATURAL);
                if (baby != null) {
                    baby.setBaby(true);
                    baby.setVariant(this.getVariant());
                    baby.setPrefersWater(this.getPrefersWater());

                    double offsetX = this.random.nextGaussian() * 0.5;
                    double offsetZ = this.random.nextGaussian() * 0.5;
                    baby.snapTo(
                            this.getX() + offsetX,
                            this.getY(),
                            this.getZ() + offsetZ,
                            this.random.nextFloat() * 360.0F,
                            0.0F
                    );

                    world.addFreshEntity(baby);
                }
            }
        }

        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }


    public static class GooseGroupData implements SpawnGroupData {
        private final boolean shouldSpawnBabies;

        public GooseGroupData(boolean shouldSpawnBabies) {
            this.shouldSpawnBabies = shouldSpawnBabies;
        }

        public boolean shouldSpawnBabies() {
            return this.shouldSpawnBabies;
        }
    }

    @Override
    public void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.preferenceChangeTimer == 0) {
            this.preferenceChangeTimer = MIN_PREFERENCE_CHANGE_TIME +
                    this.random.nextInt(MAX_PREFERENCE_CHANGE_TIME - MIN_PREFERENCE_CHANGE_TIME);
        }

        if (this.eggLayTimer == 0) {
            this.eggLayTimer = MIN_EGG_LAY_TIME +
                    this.random.nextInt(MAX_EGG_LAY_TIME - MIN_EGG_LAY_TIME);
        }
    }

    public void setVariant(Holder<GooseVariant> variant) {
        this.entityData.set(VARIANT, variant);
    }

    public Holder<GooseVariant> getVariant() {
        return (Holder)this.entityData.get(VARIANT);
    }

    public boolean getPrefersWater() {
        return this.entityData.get(PREFERS_WATER);
    }

    public void setPrefersWater(boolean prefersWater) {
        this.entityData.set(PREFERS_WATER, prefersWater);
    }

    @Nullable
    @Override
    public <T> T get(DataComponentType<? extends T> type) {
        return type == SillyGooseDataComponentTypes.GOOSE_VARIANT
                ? castComponentValue(type, this.getVariant())
                : super.get(type);
    }

    protected void applyImplicitComponents(final DataComponentGetter components) {
        this.applyImplicitComponentIfPresent(components, SillyGooseDataComponentTypes.GOOSE_VARIANT);
        super.applyImplicitComponents(components);
    }

    @Override
    public float getAgeScale() {
        return this.isBaby() ? 0.7F : 1.0F;
    }

    @Override
    protected <T> boolean applyImplicitComponent(DataComponentType<T> type, T value) {
        if (type == SillyGooseDataComponentTypes.GOOSE_VARIANT) {
            this.setVariant((Holder<GooseVariant>) value);
            return true;
        } else {
            return super.applyImplicitComponent(type, value);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.CHICKEN_FOOD);
    }

    static {
        VARIANT = SynchedEntityData.defineId(GooseEntity.class, SillyGooseTrackedDataHandlerRegistry.GOOSE_VARIANT);
        PREFERS_WATER = SynchedEntityData.defineId(GooseEntity.class, EntityDataSerializers.BOOLEAN);
        CAN_PICKUP_LOOT = SynchedEntityData.defineId(GooseEntity.class, EntityDataSerializers.BOOLEAN);
        IS_IN_HIT_AND_RUN_MODE = SynchedEntityData.defineId(GooseEntity.class, EntityDataSerializers.BOOLEAN);
    }


}

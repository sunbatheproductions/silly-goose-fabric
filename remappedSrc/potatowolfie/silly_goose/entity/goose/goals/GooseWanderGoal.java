package old.silly_goose.entity.goose.goals;

import org.jetbrains.annotations.Nullable;
import old.silly_goose.entity.goose.GooseEntity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class GooseWanderGoal extends Goal {
    private final GooseEntity goose;
    private final double speed;

    private Vec3 targetPos = null;
    private int navigationTimeout = 0;
    private int stuckCheckTimer = 0;
    private BlockPos lastPos = null;
    private boolean useDirectSwimming = false;

    private BlockPos nearestWaterPos = null;
    private int waterSearchCooldown = 0;

    private MovementMode currentMode = MovementMode.IDLE;
    private int modeTimer = 0;

    private static final int WATER_SEARCH_INTERVAL = 20;
    private static final int MAX_NAVIGATION_TIMEOUT = 100;
    private static final int STUCK_CHECK_INTERVAL = 20;
    private static final double STUCK_THRESHOLD = 1.0;

    private static final int IDLE_TIME_MIN = 20;
    private static final int IDLE_TIME_MAX = 60;
    private static final int WANDER_TIME_MIN = 80;
    private static final int WANDER_TIME_MAX = 200;

    public enum MovementMode {
        IDLE,
        WANDERING,
        SEEKING_WATER,
        SEEKING_LAND,
        RETURNING
    }

    public GooseWanderGoal(GooseEntity goose, double speed) {
        this.goose = goose;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.goose.isBaby()) {
            return false;
        }

        if (this.goose.isPassenger()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        updateNearestWater();
        determineMovementMode();
    }

    @Override
    public void stop() {
        this.goose.getNavigation().stop();
        this.targetPos = null;
        this.currentMode = MovementMode.IDLE;
    }

    @Override
    public void tick() {
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
            if (!useDirectSwimming && targetPos != null) {
                useDirectSwimming = true;
                goose.getNavigation().stop();
            }
        } else {
            if (useDirectSwimming) {
                useDirectSwimming = false;
            }
        }

        if (nearestWaterPos != null) {
            double distanceToWater = goose.blockPosition().distSqr(nearestWaterPos);

            if (distanceToWater > 256.0) {
                currentMode = MovementMode.RETURNING;
                modeTimer = 0;
            }
        }

        if (goose.shouldReturnToPreference()) {
            handleReturnToPreference();
            return;
        }

        modeTimer++;

        switch (currentMode) {
            case IDLE:
                handleIdleMode();
                break;
            case WANDERING:
                handleWanderingMode();
                break;
            case SEEKING_WATER:
                handleSeekingWaterMode();
                break;
            case SEEKING_LAND:
                handleSeekingLandMode();
                break;
            case RETURNING:
                handleReturningMode();
                break;
        }

        if (targetPos != null) {
            navigationTimeout--;

            double distanceToTarget = goose.position().distanceTo(targetPos);
            if (distanceToTarget < 2.0) {
                targetPos = null;
                goose.getNavigation().stop();
                useDirectSwimming = false;
                transitionToMode(MovementMode.IDLE);
                return;
            }

            if (useDirectSwimming) {
                handleDirectSwimming();
            } else {
                if (navigationTimeout % 20 == 0 && distanceToTarget > 3.0) {
                    goose.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, this.speed);
                }
            }

            if (navigationTimeout <= 0) {
                targetPos = null;
                goose.getNavigation().stop();
                useDirectSwimming = false;
                transitionToMode(MovementMode.IDLE);
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
            double swimSpeed = this.speed * 0.15;
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

    private void handleReturnToPreference() {
        boolean prefersWater = goose.getPrefersWater();
        boolean inWater = goose.isInWater();

        if (prefersWater && !inWater) {
            if (nearestWaterPos != null) {
                setTarget(Vec3.atBottomCenterOf(nearestWaterPos));
                currentMode = MovementMode.SEEKING_WATER;
            }
        } else if (!prefersWater && inWater) {
            Vec3 landPos = findLandNearWater();
            if (landPos != null) {
                setTarget(landPos);
                currentMode = MovementMode.SEEKING_LAND;
            }
        }
    }

    private void handleIdleMode() {
        int idleDuration = IDLE_TIME_MIN + goose.getRandom().nextInt(IDLE_TIME_MAX - IDLE_TIME_MIN);

        if (modeTimer >= idleDuration) {
            determineMovementMode();
        }
    }

    private void handleWanderingMode() {
        boolean prefersWater = goose.getPrefersWater();
        boolean inWater = goose.isInWater();

        if (targetPos == null) {
            Vec3 wanderTarget = findWanderTarget();
            if (wanderTarget != null) {
                setTarget(wanderTarget);
            } else {
                transitionToMode(MovementMode.IDLE);
            }
        }

        if (modeTimer > 40 && goose.getRandom().nextFloat() < 0.02f) {
            transitionToMode(MovementMode.IDLE);
        }

        if (modeTimer > 60 && goose.getRandom().nextFloat() < 0.3f) {
            targetPos = null;
        }
    }

    private void handleSeekingWaterMode() {
        boolean inWater = goose.isInWater();

        if (inWater) {
            targetPos = null;
            transitionToMode(MovementMode.WANDERING);
            return;
        }

        if (targetPos == null || nearestWaterPos == null) {
            updateNearestWater();
            if (nearestWaterPos != null) {
                setTarget(Vec3.atBottomCenterOf(nearestWaterPos));
            } else {
                transitionToMode(MovementMode.IDLE);
            }
        }

        if (modeTimer > 200) {
            transitionToMode(MovementMode.IDLE);
        }
    }

    private void handleSeekingLandMode() {
        boolean inWater = goose.isInWater();

        if (!inWater && goose.onGround()) {
            targetPos = null;
            transitionToMode(MovementMode.WANDERING);
            return;
        }

        if (targetPos == null) {
            Vec3 landPos = findLandNearWater();
            if (landPos != null) {
                setTarget(landPos);
            } else {
                transitionToMode(MovementMode.IDLE);
            }
        }

        if (modeTimer > 200) {
            transitionToMode(MovementMode.IDLE);
        }
    }

    private void handleReturningMode() {
        if (nearestWaterPos == null) {
            updateNearestWater();
        }

        if (nearestWaterPos != null) {
            double distanceToWater = goose.blockPosition().distSqr(nearestWaterPos);

            if (distanceToWater <= 256.0) {
                targetPos = null;
                determineMovementMode();
                return;
            }

            boolean prefersWater = goose.getPrefersWater();
            if (prefersWater) {
                setTarget(Vec3.atBottomCenterOf(nearestWaterPos));
            } else {
                Vec3 landPos = findLandNearWater();
                if (landPos != null) {
                    setTarget(landPos);
                } else {
                    setTarget(Vec3.atBottomCenterOf(nearestWaterPos));
                }
            }
        }
    }

    private void determineMovementMode() {
        if (nearestWaterPos == null) {
            updateNearestWater();
        }

        if (nearestWaterPos != null) {
            double distanceToWater = goose.blockPosition().distSqr(nearestWaterPos);
            if (distanceToWater > 256.0) {
                transitionToMode(MovementMode.RETURNING);
                return;
            }
        }

        boolean prefersWater = goose.getPrefersWater();
        boolean inWater = goose.isInWater();

        if (prefersWater) {
            if (!inWater) {
                if (goose.getRandom().nextFloat() < 0.7f) {
                    transitionToMode(MovementMode.SEEKING_WATER);
                } else {
                    transitionToMode(goose.getRandom().nextBoolean() ? MovementMode.IDLE : MovementMode.WANDERING);
                }
            } else {
                transitionToMode(goose.getRandom().nextFloat() < 0.85f ? MovementMode.WANDERING : MovementMode.IDLE);
            }
        } else {
            if (inWater) {
                if (goose.getRandom().nextFloat() < 0.8f) {
                    transitionToMode(MovementMode.SEEKING_LAND);
                } else {
                    transitionToMode(goose.getRandom().nextFloat() < 0.7f ? MovementMode.WANDERING : MovementMode.IDLE);
                }
            } else {
                transitionToMode(goose.getRandom().nextFloat() < 0.85f ? MovementMode.WANDERING : MovementMode.IDLE);
            }
        }
    }

    private void transitionToMode(MovementMode newMode) {
        this.currentMode = newMode;
        this.modeTimer = 0;
        this.targetPos = null;
        this.useDirectSwimming = false;
        this.goose.getNavigation().stop();
    }

    @Nullable
    private Vec3 findWanderTarget() {
        boolean prefersWater = goose.getPrefersWater();
        boolean inWater = goose.isInWater();

        if ((prefersWater && inWater) || (!prefersWater && !inWater)) {
            return findNearbyWanderPos();
        }

        if (goose.getRandom().nextFloat() < 0.3f) {
            return findNearbyWanderPos();
        } else {
            if (prefersWater) {
                return nearestWaterPos != null ? Vec3.atBottomCenterOf(nearestWaterPos) : findNearbyWanderPos();
            } else {
                Vec3 landPos = findLandNearWater();
                return landPos != null ? landPos : findNearbyWanderPos();
            }
        }
    }

    @Nullable
    private Vec3 findNearbyWanderPos() {
        boolean prefersWater = goose.getPrefersWater();

        for (int attempt = 0; attempt < 10; attempt++) {
            double distance = 8.0 + goose.getRandom().nextDouble() * 4.0;
            double angle = goose.getRandom().nextDouble() * Math.PI * 2.0;

            double randomX = goose.getX() + Math.cos(angle) * distance;
            double randomZ = goose.getZ() + Math.sin(angle) * distance;

            BlockPos targetBlockPos = BlockPos.containing(randomX, goose.getY(), randomZ);

            if (nearestWaterPos != null) {
                if (targetBlockPos.distSqr(nearestWaterPos) > 256.0) {
                    continue;
                }
            }

            boolean targetIsWater = goose.level().getFluidState(targetBlockPos).is(net.minecraft.tags.FluidTags.WATER);

            if (prefersWater == targetIsWater) {
                double targetY = goose.getY();
                if (targetIsWater) {
                    BlockPos checkPos = targetBlockPos;
                    while (goose.level().getFluidState(checkPos).is(net.minecraft.tags.FluidTags.WATER)) {
                        targetY = checkPos.getY() + 1.0;
                        checkPos = checkPos.above();
                    }
                } else {
                    targetY = goose.getY() + (goose.getRandom().nextDouble() - 0.5) * 2.0;
                }

                return new Vec3(randomX, targetY, randomZ);
            }
        }

        return null;
    }

    @Nullable
    private Vec3 findLandNearWater() {
        if (nearestWaterPos == null) {
            return null;
        }

        for (int attempt = 0; attempt < 15; attempt++) {
            BlockPos landPos = nearestWaterPos.offset(
                    goose.getRandom().nextInt(17) - 8,
                    goose.getRandom().nextInt(5) - 2,
                    goose.getRandom().nextInt(17) - 8
            );

            if (!goose.level().getFluidState(landPos).is(net.minecraft.tags.FluidTags.WATER) &&
                    goose.level().getBlockState(landPos.below()).isSolid()) {
                return Vec3.atBottomCenterOf(landPos);
            }
        }

        return null;
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
            this.goose.getNavigation().moveTo(target.x, target.y, target.z, this.speed);
        }
    }

    private void checkIfStuck() {
        BlockPos currentPos = goose.blockPosition();

        if (lastPos != null && targetPos != null) {
            double distanceMoved = currentPos.distSqr(lastPos);

            if (distanceMoved < STUCK_THRESHOLD) {
                targetPos = null;
                goose.getNavigation().stop();
                transitionToMode(MovementMode.IDLE);
            }
        }

        lastPos = currentPos.immutable();
    }
}
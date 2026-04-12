package potatowolfie.silly_goose.entity.goose.goals;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;

public class BabyGooseFollowGoal extends Goal {
    private final Animal animal;
    @Nullable
    private Animal parent;
    private final double speed;
    private int delay;

    private int waterExitTimer = 0;
    private static final int WATER_EXIT_BOOST_INTERVAL = 10;
    private static final double WATER_EXIT_BOOST_STRENGTH = 0.3;

    private float targetYaw = 0;
    private Vec3 lastParentPos = null;

    public BabyGooseFollowGoal(Animal animal, double speed) {
        this.animal = animal;
        this.speed = speed;
    }

    public boolean canUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        } else {
            List<? extends Animal> list = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0, 4.0, 8.0));
            Animal animalEntity = null;
            double d = Double.MAX_VALUE;
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                Animal animalEntity2 = (Animal)var5.next();
                if (animalEntity2.getAge() >= 0) {
                    double e = this.animal.distanceToSqr(animalEntity2);
                    if (!(e > d)) {
                        d = e;
                        animalEntity = animalEntity2;
                    }
                }
            }

            if (animalEntity == null) {
                return false;
            } else if (d < 9.0) {
                return false;
            } else {
                this.parent = animalEntity;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        } else if (!this.parent.isAlive()) {
            return false;
        } else {
            double d = this.animal.distanceToSqr(this.parent);
            return !(d < 9.0) && !(d > 256.0);
        }
    }

    public void start() {
        this.delay = 0;
        this.lastParentPos = this.parent != null ? this.parent.position() : null;
        this.targetYaw = this.animal.getYRot();
        this.waterExitTimer = WATER_EXIT_BOOST_INTERVAL;
    }

    public void stop() {
        this.parent = null;
        this.lastParentPos = null;
    }

    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.adjustedTickDelay(3);

            if (this.animal.isInWater() && this.parent != null) {
                handleDirectSwimming();
            } else {
                this.animal.getNavigation().moveTo(this.parent, this.speed);
            }
        } else {
            if (this.animal.isInWater() && this.parent != null) {
                updateRotation();
            }
        }
        checkAndApplyWaterExitBoost();
    }

    private void handleDirectSwimming() {
        if (this.parent == null) return;

        Vec3 targetPos = this.parent.position();
        Vec3 currentPos = this.animal.position();

        double distance = currentPos.distanceTo(targetPos);
        if (distance < 0.5) {
            updateRotation();
            return;
        }

        Vec3 parentVelocity = this.parent.getDeltaMovement();
        Vec3 predictedPos = targetPos.add(parentVelocity.scale(2.0));

        Vec3 direction = predictedPos.subtract(currentPos).normalize();

        double newTargetYaw = Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI);

        while (newTargetYaw > 180) newTargetYaw -= 360;
        while (newTargetYaw < -180) newTargetYaw += 360;

        this.targetYaw = (float)newTargetYaw;

        float currentYaw = this.animal.getYRot();
        while (currentYaw > 180) currentYaw -= 360;
        while (currentYaw < -180) currentYaw += 360;

        float yawDiff = this.targetYaw - currentYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float maxRotation = 12.0f;
        float yawAdjustment = Math.max(-maxRotation, Math.min(maxRotation, yawDiff));
        float newYaw = currentYaw + yawAdjustment;

        this.animal.setYRot(newYaw);
        this.animal.setYBodyRot(newYaw);
        this.animal.setYHeadRot(newYaw);

        if (Math.abs(yawDiff) < 60 && distance > 0.8) {
            double speedMultiplier = Math.min(1.5, distance / 4.0);
            double swimSpeed = this.speed * 0.18 * speedMultiplier;

            Vec3 velocity = this.animal.getDeltaMovement();

            this.animal.setDeltaMovement(
                    direction.x * swimSpeed,
                    velocity.y,
                    direction.z * swimSpeed
            );
            this.animal.needsSync = true;
        } else {
            Vec3 velocity = this.animal.getDeltaMovement();
            this.animal.setDeltaMovement(
                    velocity.x * 0.7,
                    velocity.y,
                    velocity.z * 0.7
            );
            this.animal.needsSync = true;
        }

        this.animal.getNavigation().stop();

        this.lastParentPos = targetPos;
    }

    private void updateRotation() {
        if (this.parent == null) return;

        Vec3 targetPos = this.parent.position();
        Vec3 currentPos = this.animal.position();
        Vec3 direction = targetPos.subtract(currentPos).normalize();

        double newTargetYaw = Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI);
        while (newTargetYaw > 180) newTargetYaw -= 360;
        while (newTargetYaw < -180) newTargetYaw += 360;

        this.targetYaw = (float)newTargetYaw;

        float currentYaw = this.animal.getYRot();
        while (currentYaw > 180) currentYaw -= 360;
        while (currentYaw < -180) currentYaw += 360;

        float yawDiff = this.targetYaw - currentYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float maxRotation = 8.0f;
        float yawAdjustment = Math.max(-maxRotation, Math.min(maxRotation, yawDiff));
        float newYaw = currentYaw + yawAdjustment;

        this.animal.setYRot(newYaw);
        this.animal.setYBodyRot(newYaw);
        this.animal.setYHeadRot(newYaw);
    }

    private void checkAndApplyWaterExitBoost() {
        waterExitTimer--;
        if (waterExitTimer <= 0) {
            waterExitTimer = WATER_EXIT_BOOST_INTERVAL;

            if (animal.isInWater()) {
                BlockPos abovePos = animal.blockPosition().above();

                if (!animal.level().getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER)) {
                    boolean hasNearbyLand = false;
                    BlockPos currentPos = animal.blockPosition();

                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && z == 0) continue;

                            BlockPos checkPos = currentPos.offset(x, 0, z);
                            BlockPos checkPosAbove = currentPos.offset(x, 1, z);

                            boolean isLand = !animal.level().getFluidState(checkPos).is(net.minecraft.tags.FluidTags.WATER);
                            boolean isLandAbove = !animal.level().getFluidState(checkPosAbove).is(net.minecraft.tags.FluidTags.WATER);

                            if (isLand || isLandAbove) {
                                hasNearbyLand = true;
                                break;
                            }
                        }
                        if (hasNearbyLand) break;
                    }

                    if (hasNearbyLand) {
                        Vec3 velocity = animal.getDeltaMovement();
                        animal.setDeltaMovement(velocity.x, WATER_EXIT_BOOST_STRENGTH, velocity.z);
                        animal.needsSync = true;
                    }
                }
            }
        }
    }
}
package potatowolfie.silly_goose.entity.egg;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import potatowolfie.silly_goose.entity.SillyGooseEntities;
import potatowolfie.silly_goose.entity.goose.GooseEntity;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariants;
import potatowolfie.silly_goose.item.SillyGooseItems;

public class GooseEggEntity extends ThrowableItemProjectile {
    private static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

    public GooseEggEntity(EntityType<? extends GooseEggEntity> entityType, Level world) {
        super(entityType, world);
    }

    public GooseEggEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(SillyGooseEntities.WHITE_EGG, owner, world, stack);
    }

    public GooseEggEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(SillyGooseEntities.WHITE_EGG, x, y, z, world, stack);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, this.getItem().getItem()),
                        this.getX(), this.getY(), this.getZ(),
                        ((double)this.random.nextFloat() - 0.5) * 0.08,
                        ((double)this.random.nextFloat() - 0.5) * 0.08,
                        ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            if (this.random.nextInt(8) == 0) {
                int count = this.random.nextInt(32) == 0 ? 4 : 1;

                for(int j = 0; j < count; ++j) {
                    GooseEntity gooseEntity = SillyGooseEntities.GOOSE.create(this.level(), EntitySpawnReason.TRIGGERED);

                    if (gooseEntity != null) {
                        gooseEntity.setAge(-24000);
                        gooseEntity.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                        gooseEntity.setSpawnedFromEgg(true);

                        if (!gooseEntity.fudgePositionAfterSizeChange(EMPTY_DIMENSIONS)) {
                            break;
                        }

                        this.level().addFreshEntity(gooseEntity);

                        gooseEntity.setVariant(VariantUtils.getDefaultOrAny(
                                ((ServerLevel)this.level()).registryAccess(),
                                GooseVariants.TEMPERATE
                        ));
                    }
                }
            }

            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return SillyGooseItems.WHITE_EGG;
    }
}
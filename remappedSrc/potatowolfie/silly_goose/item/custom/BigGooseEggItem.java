package old.silly_goose.item.custom;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import old.silly_goose.entity.egg.BigGooseEggEntity;

public class BigGooseEggItem extends Item implements ProjectileItem {
    public static final float POWER = 1.5F;

    public BigGooseEggItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (world instanceof ServerLevel serverWorld) {
            Projectile.spawnProjectileFromRotation(BigGooseEggEntity::new, serverWorld, itemStack, user, 0.0F, 1.5F, 1.0F);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, user);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        return new BigGooseEggEntity(world, pos.x(), pos.y(), pos.z(), stack);
    }
}
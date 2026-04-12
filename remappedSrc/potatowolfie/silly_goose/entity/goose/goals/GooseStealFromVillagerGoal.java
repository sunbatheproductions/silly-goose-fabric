package old.silly_goose.entity.goose.goals;

import old.silly_goose.entity.goose.GooseEntity;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GooseStealFromVillagerGoal extends Goal {
    private final GooseEntity goose;
    private Villager targetVillager;
    private int stealDelay = 0;

    public GooseStealFromVillagerGoal(GooseEntity goose) {
        this.goose = goose;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!goose.canPickUpLoot() || !goose.getMainHandItem().isEmpty() || goose.isBaby()) {
            return false;
        }

        // Find nearby villagers
        List<Villager> list = goose.level().getEntitiesOfClass(
            Villager.class, 
            goose.getBoundingBox().inflate(8.0, 4.0, 8.0), 
            villager -> !villager.getInventory().isEmpty()
        );

        if (!list.isEmpty()) {
            this.targetVillager = list.get(goose.getRandom().nextInt(list.size()));
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return targetVillager != null && targetVillager.isAlive() && goose.getMainHandItem().isEmpty();
    }

    @Override
    public void start() {
        this.stealDelay = 0;
    }

    @Override
    public void tick() {
        goose.getLookControl().setLookAt(targetVillager, 30.0F, 30.0F);
        
        if (goose.distanceToSqr(targetVillager) < 1.5) {
            if (++stealDelay >= 5) {
                stealItem();
            }
        } else {
            goose.getNavigation().moveTo(targetVillager, 1.2);
            stealDelay = 0;
        }
    }

    private void stealItem() {
        var inventory = targetVillager.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            // Define "Valuable": Emeralds, food, or specific modded items
            if (!stack.isEmpty() && (stack.is(Items.EMERALD) || stack.is(Items.WHEAT))) {
                ItemStack stolenStack = stack.split(1);
                goose.setItemSlot(EquipmentSlot.MAINHAND, stolenStack);
                goose.setDropChance(EquipmentSlot.MAINHAND, 2.0F);
                
                goose.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                targetVillager.playSound(SoundEvents.VILLAGER_NO, 1.0F, 1.0F);
                
                // Set goose into "Hit and Run" mode to escape with the loot
                goose.setInHitAndRunMode(true);
                this.targetVillager = null;
                break;
            }
        }
    }
}

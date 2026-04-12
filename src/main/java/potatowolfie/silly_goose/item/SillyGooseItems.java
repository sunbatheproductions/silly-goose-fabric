package potatowolfie.silly_goose.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import potatowolfie.silly_goose.SillyGoose;
import potatowolfie.silly_goose.entity.SillyGooseEntities;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariants;
import potatowolfie.silly_goose.item.custom.BigGooseEggItem;
import potatowolfie.silly_goose.item.custom.GooseEggItem;
import potatowolfie.silly_goose.item.custom.SmallGooseEggItem;
import potatowolfie.silly_goose.registry.SillyGooseDataComponentTypes;

import java.util.List;

public class SillyGooseItems {
    public static final Item GOOSE_SPAWN_EGG = registerItem("goose_spawn_egg",
            new SpawnEggItem(new Item.Properties().spawnEgg(SillyGooseEntities.GOOSE)
                            .setId(createItemRegistryKey("goose_spawn_egg"))));

    public static final Item WHITE_EGG = registerItem("white_egg",
            new GooseEggItem(new Item.Properties().stacksTo(16)
                    .delayedHolderComponent(SillyGooseDataComponentTypes.GOOSE_VARIANT, GooseVariants.TEMPERATE)
                    .setId(createItemRegistryKey("white_egg"))));

    public static final Item BIG_WHITE_EGG = registerItem("big_white_egg",
            new BigGooseEggItem(new Item.Properties().stacksTo(16)
                    .delayedHolderComponent(SillyGooseDataComponentTypes.GOOSE_VARIANT, GooseVariants.COLD)
                    .setId(createItemRegistryKey("big_white_egg"))));

    public static final Item SMALL_WHITE_EGG = registerItem("small_white_egg",
            new SmallGooseEggItem(new Item.Properties().stacksTo(16)
                    .delayedHolderComponent(SillyGooseDataComponentTypes.GOOSE_VARIANT, GooseVariants.WARM)
                    .setId(createItemRegistryKey("small_white_egg"))));

    public static final Item RAW_GOOSE = registerItem("raw_goose",
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(3)
                            .saturationModifier(0.4f)
                            .build())
                    .setId(createItemRegistryKey("raw_goose"))));

    public static final Item COOKED_GOOSE = registerItem("cooked_goose",
            new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(7)
                            .saturationModifier(0.8f)
                            .build())
                    .setId(createItemRegistryKey("cooked_goose"))));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, createItemRegistryKey(name), item);
    }

    private static ResourceKey<Item> createItemRegistryKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, name));
    }

    public static void registerModItems() {
        SillyGoose.LOGGER.info("Registering goose-related Items for " + SillyGoose.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(output -> {
                    output.insertAfter(Items.CHICKEN_SPAWN_EGG, List.of(
                            new ItemStack(SillyGooseItems.GOOSE_SPAWN_EGG)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(output -> {
                    output.insertAfter(Items.BLUE_EGG, List.of(
                            new ItemStack(SillyGooseItems.WHITE_EGG),
                            new ItemStack(SillyGooseItems.BIG_WHITE_EGG),
                            new ItemStack(SillyGooseItems.SMALL_WHITE_EGG)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(output -> {
                    output.insertAfter(Items.BLUE_EGG, List.of(
                            new ItemStack(SillyGooseItems.WHITE_EGG),
                            new ItemStack(SillyGooseItems.BIG_WHITE_EGG),
                            new ItemStack(SillyGooseItems.SMALL_WHITE_EGG)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register(output -> {
                    output.insertAfter(Items.COOKED_CHICKEN, List.of(
                            new ItemStack(SillyGooseItems.RAW_GOOSE),
                            new ItemStack(SillyGooseItems.COOKED_GOOSE)
                    ), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
    }
}

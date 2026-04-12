package old.silly_goose.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.item.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import old.silly_goose.SillyGoose;
import old.silly_goose.entity.SillyGooseEntities;
import old.silly_goose.entity.goose.variant.GooseVariants;
import old.silly_goose.item.custom.BigGooseEggItem;
import old.silly_goose.item.custom.GooseEggItem;
import old.silly_goose.item.custom.SmallGooseEggItem;
import old.silly_goose.registry.SillyGooseDataComponentTypes;

public class SillyGooseItems {
    public static final Item GOOSE_SPAWN_EGG = registerItem("goose_spawn_egg",
            new SpawnEggItem(new Item.Properties().spawnEgg(SillyGooseEntities.GOOSE)
                            .setId(createItemRegistryKey("goose_spawn_egg"))));

    public static final Item WHITE_EGG = registerItem("white_egg",
            new GooseEggItem(new Item.Properties().stacksTo(16)
                    .component(SillyGooseDataComponentTypes.GOOSE_VARIANT,
                            new EitherHolder<>(GooseVariants.TEMPERATE))
                    .setId(createItemRegistryKey("white_egg"))));

    public static final Item BIG_WHITE_EGG = registerItem("big_white_egg",
            new BigGooseEggItem(new Item.Properties().stacksTo(16)
                    .component(SillyGooseDataComponentTypes.GOOSE_VARIANT,
                            new EitherHolder<>(GooseVariants.COLD))
                    .setId(createItemRegistryKey("big_white_egg"))));

    public static final Item SMALL_WHITE_EGG = registerItem("small_white_egg",
            new SmallGooseEggItem(new Item.Properties().stacksTo(16)
                    .component(SillyGooseDataComponentTypes.GOOSE_VARIANT,
                            new EitherHolder<>(GooseVariants.WARM))
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

    private static void customSpawnEggs(FabricItemGroupEntries entries) {
        entries.addAfter(Items.CHICKEN_SPAWN_EGG, GOOSE_SPAWN_EGG);
    }
    private static void customCombat(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLUE_EGG, WHITE_EGG);
        entries.addAfter(WHITE_EGG, BIG_WHITE_EGG);
        entries.addAfter(BIG_WHITE_EGG, SMALL_WHITE_EGG);
    }
    private static void customIngredients(FabricItemGroupEntries entries) {
        entries.addAfter(Items.BLUE_EGG, WHITE_EGG);
        entries.addAfter(WHITE_EGG, BIG_WHITE_EGG);
        entries.addAfter(BIG_WHITE_EGG, SMALL_WHITE_EGG);
    }

    private static void customFood(FabricItemGroupEntries entries) {
        entries.addAfter(Items.COOKED_CHICKEN, RAW_GOOSE);
        entries.addAfter(RAW_GOOSE, COOKED_GOOSE);
    }

    public static void registerModItems() {
        SillyGoose.LOGGER.info("Registering goose-related Items for " + SillyGoose.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(SillyGooseItems::customSpawnEggs);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(SillyGooseItems::customCombat);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(SillyGooseItems::customIngredients);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(SillyGooseItems::customFood);
    }
}

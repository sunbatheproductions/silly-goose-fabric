package potatowolfie.silly_goose.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import potatowolfie.silly_goose.SillyGoose;
import potatowolfie.silly_goose.entity.egg.BigGooseEggEntity;
import potatowolfie.silly_goose.entity.egg.GooseEggEntity;
import potatowolfie.silly_goose.entity.egg.SmallGooseEggEntity;
import potatowolfie.silly_goose.entity.goose.GooseEntity;

public class SillyGooseEntities {

    public static final EntityType<GooseEntity> GOOSE = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose"),
            EntityType.Builder.of(GooseEntity::new, MobCategory.CREATURE)
                    .clientTrackingRange(48).sized(0.625F, 1.375F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose"))));

    public static final EntityType<GooseEggEntity> WHITE_EGG = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "white_egg"),
            EntityType.Builder.<GooseEggEntity>of(GooseEggEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "white_egg"))));

    public static final EntityType<BigGooseEggEntity> BIG_WHITE_EGG = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "big_white_egg"),
            EntityType.Builder.<BigGooseEggEntity>of(BigGooseEggEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "big_white_egg"))));

    public static final EntityType<SmallGooseEggEntity> SMALL_WHITE_EGG = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "small_white_egg"),
            EntityType.Builder.<SmallGooseEggEntity>of(SmallGooseEggEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "small_white_egg"))));

    public static void registerModEntities() {
        SillyGoose.LOGGER.info("Registering the HONK HONK (goose) Entities for " + SillyGoose.MOD_ID);
    }
}
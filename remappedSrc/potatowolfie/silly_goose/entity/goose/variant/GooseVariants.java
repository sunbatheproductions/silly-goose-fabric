package old.silly_goose.entity.goose.variant;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;
import old.silly_goose.SillyGoose;
import old.silly_goose.registry.SillyGooseRegistryKeys;

public class GooseVariants {
    public static final ResourceKey<GooseVariant> TEMPERATE;
    public static final ResourceKey<GooseVariant> WARM;
    public static final ResourceKey<GooseVariant> COLD;
    public static final ResourceKey<GooseVariant> DEFAULT;

    public GooseVariants() {
    }

    private static ResourceKey<GooseVariant> of(Identifier id) {
        return ResourceKey.create(SillyGooseRegistryKeys.GOOSE_VARIANT, id);
    }

    public static void bootstrap(BootstrapContext<GooseVariant> registry) {
        register(registry, TEMPERATE, GooseVariant.Model.NORMAL, "temperate_goose", SpawnPrioritySelectors.fallback(0));
        register(registry, WARM, GooseVariant.Model.WARM, "warm_goose", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
        register(registry, COLD, GooseVariant.Model.COLD, "cold_goose", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
    }

    private static void register(BootstrapContext<GooseVariant> registry, ResourceKey<GooseVariant> key, GooseVariant.Model model, String textureName, TagKey<Biome> biomes) {
        HolderSet<Biome> registryEntryList = registry.lookup(Registries.BIOME).getOrThrow(biomes);
        register(registry, key, model, textureName, SpawnPrioritySelectors.single(new BiomeCheck(registryEntryList), 1));
    }

    private static void register(BootstrapContext<GooseVariant> registry, ResourceKey<GooseVariant> key, GooseVariant.Model model, String textureName, SpawnPrioritySelectors spawnConditions) {
        Identifier identifier = Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "textures/entity/goose/" + textureName);
        registry.register(key, new GooseVariant(new ModelAndTexture<>(model, identifier), spawnConditions));
    }

    static {
        TEMPERATE = of(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "temperate"));
        WARM = of(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "warm"));
        COLD = of(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "cold"));
        DEFAULT = TEMPERATE;
    }
}
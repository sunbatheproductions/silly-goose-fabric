package potatowolfie.silly_goose.entity.goose.variant;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;
import potatowolfie.silly_goose.SillyGoose;
import potatowolfie.silly_goose.registry.SillyGooseRegistryKeys;

public class GooseVariants {
    public static final ResourceKey<GooseVariant> TEMPERATE;
    public static final ResourceKey<GooseVariant> WARM;
    public static final ResourceKey<GooseVariant> COLD;
    public static final ResourceKey<GooseVariant> DEFAULT;
    public GooseVariants() {
    }

    private static ResourceKey<GooseVariant> createKey(String path) {
        return ResourceKey.create(SillyGooseRegistryKeys.GOOSE_VARIANT,
                Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, path));
    }

    public static void bootstrap(BootstrapContext<GooseVariant> context) {
        register(context, TEMPERATE, GooseVariant.Model.NORMAL, "temperate_goose", "goose_temperate_baby", SpawnPrioritySelectors.fallback(0));
        register(context, WARM, GooseVariant.Model.WARM, "warm_goose", "goose_warm_baby", BiomeTags.SPAWNS_WARM_VARIANT_FARM_ANIMALS);
        register(context, COLD, GooseVariant.Model.COLD, "cold_goose", "goose_cold_baby", BiomeTags.SPAWNS_COLD_VARIANT_FARM_ANIMALS);
    }

    private static void register(BootstrapContext<GooseVariant> context, ResourceKey<GooseVariant> name, GooseVariant.Model model, String textureName, String babyTextureName, TagKey<Biome> spawnBiome) {
        HolderSet<Biome> biomes = context.lookup(Registries.BIOME).getOrThrow(spawnBiome);
        register(context, name, model, textureName, babyTextureName, SpawnPrioritySelectors.single(new BiomeCheck(biomes), 1));
    }

    private static void register(BootstrapContext<GooseVariant> context, ResourceKey<GooseVariant> name, GooseVariant.Model model, String textureName, String babyTextureName, SpawnPrioritySelectors selectors) {
        Identifier textureId = Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "entity/goose/" + textureName);
        Identifier babyTextureId = Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "entity/goose/" + babyTextureName);

        context.register(name, new GooseVariant(
                new ModelAndTexture<>(model, textureId),
                new ClientAsset.ResourceTexture(babyTextureId),
                selectors
        ));
    }

    static {
        TEMPERATE = createKey(TemperatureVariants.TEMPERATE.getPath());
        WARM = createKey(TemperatureVariants.WARM.getPath());
        COLD = createKey(TemperatureVariants.COLD.getPath());
        DEFAULT = TEMPERATE;
    }
}
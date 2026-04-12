package old.silly_goose.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import old.silly_goose.registry.SillyGooseRegistryKeys;

import java.util.concurrent.CompletableFuture;

public class SillyGooseRegistryDataGenerator extends FabricDynamicRegistryProvider {
    public SillyGooseRegistryDataGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(SillyGooseRegistryKeys.GOOSE_VARIANT));
        entries.addAll(registries.lookupOrThrow(Registries.TRIM_MATERIAL));
        entries.addAll(registries.lookupOrThrow(Registries.TRIM_PATTERN));
        entries.addAll(registries.lookupOrThrow(Registries.BIOME));
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
    }

    @Override
    public String getName() {
        return "HONK HONK data that is registry";
    }
}
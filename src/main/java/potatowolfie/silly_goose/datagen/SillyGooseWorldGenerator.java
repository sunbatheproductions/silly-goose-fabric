package potatowolfie.silly_goose.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import potatowolfie.silly_goose.registry.SillyGooseRegistryKeys;

import java.util.concurrent.CompletableFuture;

public class SillyGooseWorldGenerator extends FabricDynamicRegistryProvider {
    public SillyGooseWorldGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.CONFIGURED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.PLACED_FEATURE));
        entries.addAll(registries.lookupOrThrow(Registries.BIOME));
        entries.addAll(registries.lookupOrThrow(SillyGooseRegistryKeys.GOOSE_VARIANT));
    }

    @Override
    public String getName() {
        return "World Gen";
    }
}
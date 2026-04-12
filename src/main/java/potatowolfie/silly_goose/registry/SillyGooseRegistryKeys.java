package potatowolfie.silly_goose.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import potatowolfie.silly_goose.SillyGoose;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariant;

public class SillyGooseRegistryKeys {
    public static final ResourceKey<net.minecraft.core.Registry<GooseVariant>> GOOSE_VARIANT =
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose_variant"));

    public static void registerModRegistryKeys() {
        SillyGoose.LOGGER.info("Registering Mod Registry Keys for " + SillyGoose.MOD_ID + " (Love you too warm, cold, and temperate geese)");

        DynamicRegistries.registerSynced(
                GOOSE_VARIANT,
                GooseVariant.DIRECT_CODEC,
                GooseVariant.NETWORK_CODEC
        );
    }
}
package old.silly_goose.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import old.silly_goose.SillyGoose;
import old.silly_goose.entity.goose.variant.GooseVariant;

public class SillyGooseTrackedDataHandlerRegistry {
    public static final EntityDataSerializer<Holder<GooseVariant>> GOOSE_VARIANT =
            EntityDataSerializer.forValueType(GooseVariant.ENTRY_PACKET_CODEC);

    public static void register() {
        FabricTrackedDataRegistry.register(
                Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose_variant"),
                GOOSE_VARIANT
        );
    }
}
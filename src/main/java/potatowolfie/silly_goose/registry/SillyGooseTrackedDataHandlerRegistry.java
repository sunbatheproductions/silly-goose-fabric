package potatowolfie.silly_goose.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import potatowolfie.silly_goose.SillyGoose;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariant;

public class SillyGooseTrackedDataHandlerRegistry {

    public static final EntityDataSerializer<Holder<GooseVariant>> GOOSE_VARIANT =
            EntityDataSerializer.forValueType(GooseVariant.STREAM_CODEC);

    public static void register() {
        FabricEntityDataRegistry.register(
                Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose_variant"),
                GOOSE_VARIANT
        );
    }
}
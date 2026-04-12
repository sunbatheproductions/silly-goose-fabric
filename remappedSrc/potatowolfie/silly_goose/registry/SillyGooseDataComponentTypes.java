package old.silly_goose.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.EitherHolder;
import old.silly_goose.SillyGoose;
import old.silly_goose.entity.goose.variant.GooseVariant;

public class SillyGooseDataComponentTypes {
    public static final DataComponentType<EitherHolder<GooseVariant>> GOOSE_VARIANT =
            register("goose_variant", DataComponentType.<EitherHolder<GooseVariant>>builder()
                    .persistent(EitherHolder.codec(
                            SillyGooseRegistryKeys.GOOSE_VARIANT,
                            GooseVariant.ENTRY_CODEC))
                    .networkSynchronized(EitherHolder.streamCodec(
                            SillyGooseRegistryKeys.GOOSE_VARIANT,
                            GooseVariant.ENTRY_PACKET_CODEC))
                    .build());

    private static <T> DataComponentType<T> register(String id, DataComponentType<T> componentType) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, id), componentType);
    }

    public static void register() {
        SillyGoose.LOGGER.info("Registering Data Component Types (just one, its the geese variants again) for " + SillyGoose.MOD_ID);
    }
}
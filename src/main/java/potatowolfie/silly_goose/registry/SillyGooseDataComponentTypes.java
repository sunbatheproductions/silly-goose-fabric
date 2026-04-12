package potatowolfie.silly_goose.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import potatowolfie.silly_goose.SillyGoose;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariant;

import java.util.function.UnaryOperator;

public class SillyGooseDataComponentTypes {
    public static final DataComponentType<Holder<GooseVariant>> GOOSE_VARIANT = register("goose_variant", (b) -> {
        return b.persistent(GooseVariant.CODEC).networkSynchronized(GooseVariant.STREAM_CODEC);
    });

    private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, id),
                builder.apply(DataComponentType.builder()).build()
        );
    }

    public static void register() {
        SillyGoose.LOGGER.info("Registering Data Component Types (just one, its the geese variants again) for " + SillyGoose.MOD_ID);
    }
}
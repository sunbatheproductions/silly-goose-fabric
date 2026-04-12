package old.silly_goose.entity.goose.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import old.silly_goose.registry.SillyGooseRegistryKeys;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record GooseVariant(ModelAndTexture<Model> modelAndTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {
    public static final Codec<GooseVariant> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ModelAndTexture.codec(GooseVariant.Model.CODEC, GooseVariant.Model.NORMAL).forGetter(GooseVariant::modelAndTexture), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(GooseVariant::spawnConditions)).apply(instance, GooseVariant::new);
    });
    public static final Codec<GooseVariant> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ModelAndTexture.codec(GooseVariant.Model.CODEC, GooseVariant.Model.NORMAL).forGetter(GooseVariant::modelAndTexture)).apply(instance, GooseVariant::new);
    });
    public static final Codec<Holder<GooseVariant>> ENTRY_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<GooseVariant>> ENTRY_PACKET_CODEC;

    private GooseVariant(ModelAndTexture<Model> modelAndTexture) {
        this(modelAndTexture, SpawnPrioritySelectors.EMPTY);
    }

    public GooseVariant(ModelAndTexture<Model> modelAndTexture, SpawnPrioritySelectors spawnConditions) {
        this.modelAndTexture = modelAndTexture;
        this.spawnConditions = spawnConditions;
    }

    public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
        return this.spawnConditions.selectors();
    }

    public ModelAndTexture<Model> modelAndTexture() {
        return this.modelAndTexture;
    }

    public SpawnPrioritySelectors spawnConditions() {
        return this.spawnConditions;
    }

    static {
        ENTRY_CODEC = RegistryFixedCodec.create(SillyGooseRegistryKeys.GOOSE_VARIANT);
        ENTRY_PACKET_CODEC = ByteBufCodecs.holderRegistry(SillyGooseRegistryKeys.GOOSE_VARIANT);
    }

    public static enum Model implements StringRepresentable {
        NORMAL("normal"),
        COLD("cold"),
        WARM("warm");

        public static final Codec<Model> CODEC = StringRepresentable.fromEnum(Model::values);
        private final String id;

        private Model(final String id) {
            this.id = id;
        }

        public String getSerializedName() {
            return this.id;
        }
    }
}

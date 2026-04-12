package potatowolfie.silly_goose.entity.goose.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.ClientAsset;
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
import potatowolfie.silly_goose.registry.SillyGooseRegistryKeys;

import java.util.List;

public record GooseVariant(ModelAndTexture<Model> modelAndTexture, ClientAsset.ResourceTexture babyTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider<SpawnContext, SpawnCondition> {

    public static final Codec<GooseVariant> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ModelAndTexture.codec(Model.CODEC, Model.NORMAL).forGetter(GooseVariant::modelAndTexture),
                ClientAsset.ResourceTexture.CODEC.fieldOf("baby_texture").forGetter(GooseVariant::babyTexture),
                SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(GooseVariant::spawnConditions)
        ).apply(instance, GooseVariant::new);
    });

    public static final Codec<GooseVariant> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                ModelAndTexture.codec(Model.CODEC, Model.NORMAL).forGetter(GooseVariant::modelAndTexture),
                ClientAsset.ResourceTexture.CODEC.fieldOf("baby_texture").forGetter(GooseVariant::babyTexture),
                SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(GooseVariant::spawnConditions)
        ).apply(instance, GooseVariant::new);
    });

    public static final Codec<Holder<GooseVariant>> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<GooseVariant>> STREAM_CODEC;

    public GooseVariant(ModelAndTexture<Model> modelAndTexture, SpawnPrioritySelectors spawnConditions) {
        this(modelAndTexture, (ClientAsset.ResourceTexture) modelAndTexture.asset(), spawnConditions);
    }

    public GooseVariant(ModelAndTexture<Model> modelAndTexture, ClientAsset.ResourceTexture babyTexture, SpawnPrioritySelectors spawnConditions) {
        this.modelAndTexture = modelAndTexture;
        this.babyTexture = babyTexture;
        this.spawnConditions = spawnConditions;
    }

    @Override
    public List<Selector<SpawnContext, SpawnCondition>> selectors() {
        return this.spawnConditions.selectors();
    }

    static {
        CODEC = RegistryFixedCodec.create(SillyGooseRegistryKeys.GOOSE_VARIANT);
        STREAM_CODEC = ByteBufCodecs.holderRegistry(SillyGooseRegistryKeys.GOOSE_VARIANT);
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

        @Override
        public String getSerializedName() {
            return this.id;
        }
    }
}

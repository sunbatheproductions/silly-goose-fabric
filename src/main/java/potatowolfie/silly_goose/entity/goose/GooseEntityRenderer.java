package potatowolfie.silly_goose.entity.goose;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import potatowolfie.silly_goose.entity.client.SillyGooseEntityModelLayers;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariant;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class GooseEntityRenderer extends MobRenderer<GooseEntity, GooseEntityRenderState, GooseEntityModel> {
    private final Map<GooseVariant.Model, AdultAndBabyModelPair<GooseEntityModel>> babyModelPairMap;
    private final ItemModelResolver itemModelManager;

    public GooseEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)), 0.7F);
        this.babyModelPairMap = createBabyModelPairMap(context);
        this.itemModelManager = context.getItemModelResolver();

        this.addLayer(new ItemInHandLayer<>(this));
    }

    private static Map<GooseVariant.Model, AdultAndBabyModelPair<GooseEntityModel>> createBabyModelPairMap(EntityRendererProvider.Context context) {
        Map<GooseVariant.Model, AdultAndBabyModelPair<GooseEntityModel>> map = Maps.newEnumMap(GooseVariant.Model.class);

        map.put(GooseVariant.Model.NORMAL, new AdultAndBabyModelPair<>(
                new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)),
                new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.BABY_GOOSE))
        ));

        map.put(GooseVariant.Model.COLD, new AdultAndBabyModelPair<>(
                new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)),
                new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.BABY_GOOSE))
        ));

        map.put(GooseVariant.Model.WARM, new AdultAndBabyModelPair<>(
                new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)),
                new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.BABY_GOOSE))
        ));

        return map;
    }

    @Override
    public Identifier getTextureLocation(GooseEntityRenderState state) {
        if (state.variant == null) {
            return MissingTextureAtlasSprite.getLocation();
        }
        if (state.isBaby) {
            return state.variant.babyTexture().texturePath();
        }
        return state.variant.modelAndTexture().asset().texturePath();
    }

    public GooseEntityRenderState createRenderState() {
        return new GooseEntityRenderState();
    }

    @Override
    public void extractRenderState(GooseEntity entity, GooseEntityRenderState state, float f) {
        super.extractRenderState(entity, state, f);

        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelManager, f);

        GooseVariant variant = (GooseVariant)entity.getVariant().value();
        state.variant = variant;

        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.idleWaterAnimationState.copyFrom(entity.idleWaterAnimationState);
        state.walkAnimationState.copyFrom(entity.walkAnimationState);
        state.runAnimationState.copyFrom(entity.runAnimationState);
        state.swimAnimationState.copyFrom(entity.swimAnimationState);
        state.swimFastAnimationState.copyFrom(entity.swimFastAnimationState);
        state.wingsUpIdleAnimationState.copyFrom(entity.wingsUpIdleAnimationState);

        state.babyIdleAnimationState.copyFrom(entity.babyIdleAnimationState);
        state.babyIdleWaterAnimationState.copyFrom(entity.babyIdleWaterAnimationState);
        state.babyWalkAnimationState.copyFrom(entity.babyWalkAnimationState);
        state.babyRunAnimationState.copyFrom(entity.babyRunAnimationState);
        state.babySwimAnimationState.copyFrom(entity.babySwimAnimationState);
        state.babySwimFastAnimationState.copyFrom(entity.babySwimFastAnimationState);
        state.babyWingsUpIdleAnimationState.copyFrom(entity.babyWingsUpIdleAnimationState);

        if (entity.isBaby()) {
            state.babyTexture = variant.babyTexture().texturePath();
        } else {
            state.babyTexture = variant.modelAndTexture().asset().texturePath();
        }
    }

    public void render(GooseEntityRenderState state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.variant != null) {
            AdultAndBabyModelPair<GooseEntityModel> pair = this.babyModelPairMap.get(state.variant.modelAndTexture().model());

            if (pair != null) {
                this.model = pair.getModel(state.isBaby);
                super.submit(state, matrixStack, collector, camera);
            }
        }
    }

    @Override
    public void submit(GooseEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.variant != null) {
            this.model = (GooseEntityModel)((AdultAndBabyModelPair)this.babyModelPairMap.get(state.variant.modelAndTexture().model()))
                    .getModel(state.isBaby);

            super.submit(state, poseStack, collector, camera);
        }
    }
}
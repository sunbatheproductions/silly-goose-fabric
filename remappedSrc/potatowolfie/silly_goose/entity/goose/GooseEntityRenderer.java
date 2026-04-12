package old.silly_goose.entity.goose;

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
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import old.silly_goose.entity.client.SillyGooseEntityModelLayers;
import old.silly_goose.entity.goose.variant.GooseVariant;

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
        return Maps.newEnumMap(Map.of(
                GooseVariant.Model.NORMAL, new AdultAndBabyModelPair<>(
                        new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)),
                        new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.BABY_GOOSE))
                ),
                GooseVariant.Model.WARM, new AdultAndBabyModelPair<>(
                        new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)),
                        new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.BABY_GOOSE))
                ),
                GooseVariant.Model.COLD, new AdultAndBabyModelPair<>(
                        new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.GOOSE)),
                        new GooseEntityModel(context.bakeLayer(SillyGooseEntityModelLayers.BABY_GOOSE))
                )
        ));
    }

    public Identifier getTexture(GooseEntityRenderState gooseEntityRenderState) {
        return gooseEntityRenderState.variant == null ? MissingTextureAtlasSprite.getLocation() : gooseEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    public GooseEntityRenderState createRenderState() {
        return new GooseEntityRenderState();
    }

    public void updateRenderState(GooseEntity gooseEntity, GooseEntityRenderState gooseEntityRenderState, float f) {
        super.extractRenderState(gooseEntity, gooseEntityRenderState, f);
        ArmedEntityRenderState.extractArmedEntityRenderState(gooseEntity, gooseEntityRenderState, this.itemModelManager, f);
        gooseEntityRenderState.variant = (GooseVariant)gooseEntity.getVariant().value();

        gooseEntityRenderState.idleAnimationState.copyFrom(gooseEntity.idleAnimationState);
        gooseEntityRenderState.idleWaterAnimationState.copyFrom(gooseEntity.idleWaterAnimationState);
        gooseEntityRenderState.walkAnimationState.copyFrom(gooseEntity.walkAnimationState);
        gooseEntityRenderState.runAnimationState.copyFrom(gooseEntity.runAnimationState);
        gooseEntityRenderState.swimAnimationState.copyFrom(gooseEntity.swimAnimationState);
        gooseEntityRenderState.swimFastAnimationState.copyFrom(gooseEntity.swimFastAnimationState);
    }

    public void render(GooseEntityRenderState gooseEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (gooseEntityRenderState.variant != null) {
            this.model = (GooseEntityModel)((AdultAndBabyModelPair)this.babyModelPairMap.get(gooseEntityRenderState.variant.modelAndTexture().model())).getModel(gooseEntityRenderState.isBaby);
            super.submit(gooseEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        }
    }
}
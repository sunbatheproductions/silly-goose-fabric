package old.silly_goose;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import old.silly_goose.entity.SillyGooseEntities;
import old.silly_goose.entity.client.SillyGooseEntityModelLayers;
import old.silly_goose.entity.goose.GooseEntityRenderer;
import old.silly_goose.entity.goose.GooseEntityModel;

public class SillyGooseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityModelLayerRegistry.registerModelLayer(
                SillyGooseEntityModelLayers.GOOSE,
                GooseEntityModel::getTexturedModelData
        );
        EntityModelLayerRegistry.registerModelLayer(
                SillyGooseEntityModelLayers.BABY_GOOSE,
                () -> GooseEntityModel.getTexturedModelData().apply(GooseEntityModel.BABY_TRANSFORMER)
        );
        EntityRendererRegistry.register(SillyGooseEntities.GOOSE, GooseEntityRenderer::new);
        EntityRendererRegistry.register(SillyGooseEntities.WHITE_EGG, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SillyGooseEntities.BIG_WHITE_EGG, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SillyGooseEntities.SMALL_WHITE_EGG, ThrownItemRenderer::new);

    }
}
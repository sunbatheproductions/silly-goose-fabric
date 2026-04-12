package potatowolfie.silly_goose;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import potatowolfie.silly_goose.entity.SillyGooseEntities;
import potatowolfie.silly_goose.entity.client.SillyGooseEntityModelLayers;
import potatowolfie.silly_goose.entity.goose.GooseEntityRenderer;
import potatowolfie.silly_goose.entity.goose.GooseEntityModel;

public class SillyGooseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ModelLayerRegistry.registerModelLayer(
                SillyGooseEntityModelLayers.GOOSE,
                GooseEntityModel::getTexturedModelData
        );
        ModelLayerRegistry.registerModelLayer(
                SillyGooseEntityModelLayers.BABY_GOOSE,
                GooseEntityModel::createBabyBodyLayer
        );
        EntityRendererRegistry.register(SillyGooseEntities.GOOSE, GooseEntityRenderer::new);
        EntityRendererRegistry.register(SillyGooseEntities.WHITE_EGG, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SillyGooseEntities.BIG_WHITE_EGG, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SillyGooseEntities.SMALL_WHITE_EGG, ThrownItemRenderer::new);

    }
}
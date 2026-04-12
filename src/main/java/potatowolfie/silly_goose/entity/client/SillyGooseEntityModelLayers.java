package potatowolfie.silly_goose.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import potatowolfie.silly_goose.SillyGoose;

public class SillyGooseEntityModelLayers {

    public static final ModelLayerLocation GOOSE =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose"), "main");
    public static final ModelLayerLocation BABY_GOOSE =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "baby_goose"), "main");
}
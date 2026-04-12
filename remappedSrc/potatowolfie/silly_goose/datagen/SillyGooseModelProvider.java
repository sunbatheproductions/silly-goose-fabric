package old.silly_goose.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import old.silly_goose.item.SillyGooseItems;

public class SillyGooseModelProvider extends FabricModelProvider {
    public SillyGooseModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(SillyGooseItems.GOOSE_SPAWN_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(SillyGooseItems.WHITE_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(SillyGooseItems.BIG_WHITE_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(SillyGooseItems.SMALL_WHITE_EGG, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(SillyGooseItems.RAW_GOOSE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(SillyGooseItems.COOKED_GOOSE, ModelTemplates.FLAT_ITEM);
    }
}
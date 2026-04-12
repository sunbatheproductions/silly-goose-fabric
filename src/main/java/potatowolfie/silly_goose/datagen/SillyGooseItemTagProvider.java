package potatowolfie.silly_goose.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import potatowolfie.silly_goose.item.SillyGooseItems;
import potatowolfie.silly_goose.registry.SillyGooseItemTags;

import java.util.concurrent.CompletableFuture;

public class SillyGooseItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public SillyGooseItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(ItemTags.WOLF_FOOD)
                .add(SillyGooseItems.RAW_GOOSE)
                .add(SillyGooseItems.COOKED_GOOSE);

        valueLookupBuilder(ItemTags.MEAT)
                .add(SillyGooseItems.RAW_GOOSE)
                .add(SillyGooseItems.COOKED_GOOSE);

        valueLookupBuilder(SillyGooseItemTags.Item.GOOSE_EGGS)
                .add(SillyGooseItems.WHITE_EGG)
                .add(SillyGooseItems.BIG_WHITE_EGG)
                .add(SillyGooseItems.SMALL_WHITE_EGG);
    }
}

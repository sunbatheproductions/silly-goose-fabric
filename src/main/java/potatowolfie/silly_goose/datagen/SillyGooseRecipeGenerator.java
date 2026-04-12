package potatowolfie.silly_goose.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import potatowolfie.silly_goose.item.SillyGooseItems;

import java.util.concurrent.CompletableFuture;

public class SillyGooseRecipeGenerator extends FabricRecipeProvider {
    public SillyGooseRecipeGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                simpleCookingRecipe("smelting", SmeltingRecipe::new,
                        200, SillyGooseItems.RAW_GOOSE, SillyGooseItems.COOKED_GOOSE, 0.35f);

                simpleCookingRecipe("smoking", SmokingRecipe::new,
                        100, SillyGooseItems.RAW_GOOSE, SillyGooseItems.COOKED_GOOSE, 0.35f);

                simpleCookingRecipe("campfire_cooking", CampfireCookingRecipe::new,
                        600, SillyGooseItems.RAW_GOOSE, SillyGooseItems.COOKED_GOOSE, 0.35f);
            }
        };
    }

    @Override
    public String getName() {
        return "The one things where you- HONK- craft the things together or smth like that";
    }
}
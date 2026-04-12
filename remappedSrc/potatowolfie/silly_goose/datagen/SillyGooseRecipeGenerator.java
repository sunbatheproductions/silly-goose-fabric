package old.silly_goose.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import old.silly_goose.item.SillyGooseItems;

import java.util.concurrent.CompletableFuture;

public class SillyGooseRecipeGenerator extends FabricRecipeProvider {
    public SillyGooseRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                simpleCookingRecipe("smelting", RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new,
                        200, SillyGooseItems.RAW_GOOSE, SillyGooseItems.COOKED_GOOSE, 0.35f);

                simpleCookingRecipe("smoking", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new,
                        100, SillyGooseItems.RAW_GOOSE, SillyGooseItems.COOKED_GOOSE, 0.35f);

                simpleCookingRecipe("campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new,
                        600, SillyGooseItems.RAW_GOOSE, SillyGooseItems.COOKED_GOOSE, 0.35f);
            }
        };
    }

    @Override
    public String getName() {
        return "The one things where you- HONK- craft the things together or smth like that";
    }
}
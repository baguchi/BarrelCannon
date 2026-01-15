package baguchi.barrel_cannon.data;

import baguchi.barrel_cannon.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CraftingGenerator extends RecipeProvider {
    public CraftingGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModItems.BARREL_CANNON.get(), 1)
                .pattern("B")
                .pattern("S")
                .pattern("W")
                .define('B', Tags.Items.BARRELS)
                .define('S', ItemTags.WOODEN_SLABS)
                .define('W', Items.WIND_CHARGE)
                .unlockedBy("has_item", has(Items.WIND_CHARGE))
                .save(consumer);
    }
}

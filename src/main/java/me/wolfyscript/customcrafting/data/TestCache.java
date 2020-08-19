package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;

import java.util.HashMap;

public class TestCache extends CustomCache {

    private Setting setting;
    //RECIPE_LIST OF ALL RECIPE SAVED IN CACHE
    private final HashMap<RecipeType, ICustomRecipe> recipes = new HashMap<>();

    private final CustomCrafting customCrafting;
    private String subSetting;

    private Items items = new Items();
    private final KnowledgeBook knowledgeBook = new KnowledgeBook();
    private final VariantsData variantsData = new VariantsData();
    private EliteWorkbench eliteWorkbench = new EliteWorkbench();
    private final ChatLists chatLists = new ChatLists();
    private final ParticleCache particleCache = new ParticleCache();
    private ApplyItem applyItem;
    private RecipeType recipeType;

    public TestCache() {
        this.customCrafting = CustomCrafting.getInst();
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;
        this.recipeType = RecipeType.WORKBENCH;

        setCustomRecipe(new CustomAnvilRecipe());
        setCustomRecipe(new ShapedCraftRecipe());
        setCustomRecipe(new ShapedEliteCraftRecipe());
        setCustomRecipe(new CustomBlastRecipe());
        setCustomRecipe(new CustomCampfireRecipe());
        setCustomRecipe(new CustomSmokerRecipe());
        setCustomRecipe(new CustomFurnaceRecipe());
        setCustomRecipe(new CustomStonecutterRecipe());
        setCustomRecipe(new GrindstoneRecipe());
        setCustomRecipe(new CauldronRecipe());
        setCustomRecipe(new BrewingRecipe());
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType recipeType) {
        this.recipeType = recipeType;
    }

    public String getSubSetting() {
        return subSetting;
    }

    public void setSubSetting(String setting) {
        this.subSetting = setting;
    }

    public ChatLists getChatLists() {
        return chatLists;
    }

    public VariantsData getVariantsData() {
        return variantsData;
    }

    public KnowledgeBook getKnowledgeBook() {
        return knowledgeBook;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public void setApplyItem(ApplyItem applyItem) {
        this.applyItem = applyItem;
    }

    public void applyItem(CustomItem customItem) {
        if (applyItem != null) {
            applyItem.applyItem(getItems(), this, customItem);
            applyItem = null;
        }
    }

    public ParticleCache getParticleCache() {
        return particleCache;
    }

    public EliteWorkbench getEliteWorkbench() {
        return eliteWorkbench;
    }

    public void setEliteWorkbench(EliteWorkbench eliteWorkbench) {
        this.eliteWorkbench = eliteWorkbench;
    }

    public void setCustomRecipe(ICustomRecipe customRecipe) {
        recipes.put(customRecipe.getRecipeType(), customRecipe);
    }

    public ICustomRecipe getCustomRecipe(RecipeType recipeType) {
        return recipes.get(recipeType);
    }


    /***************************************************************
     * Util methods to get specific kinds of Recipes that are cached into this class
     * Used for the GUI Recipe Creators!
     *
     ***************************************************************/
    public CustomCookingRecipe<?> getCookingRecipe() {
        if (recipeType.equals(RecipeType.CAMPFIRE) || recipeType.equals(RecipeType.SMOKER) || recipeType.equals(RecipeType.FURNACE) || recipeType.equals(RecipeType.BLAST_FURNACE)) {
            return (CustomCookingRecipe<?>) getCustomRecipe(recipeType);
        }
        return null;
    }

    public void resetCookingRecipe() {
        switch (getRecipeType()) {
            case CAMPFIRE:
                setCustomRecipe(new CustomCampfireRecipe());
            case SMOKER:
                setCustomRecipe(new CustomSmokerRecipe());
            case FURNACE:
                setCustomRecipe(new CustomFurnaceRecipe());
            case BLAST_FURNACE:
                setCustomRecipe(new CustomBlastRecipe());
        }
    }

    public void resetRecipe(){
        switch (getRecipeType()) {
            case CAMPFIRE:
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
                resetCookingRecipe();
                break;
            case ELITE_WORKBENCH:
                setCustomRecipe(new ShapedEliteCraftRecipe());
                break;
            case WORKBENCH:
                setCustomRecipe(new ShapedCraftRecipe());
                break;
            case ANVIL:
                setCustomRecipe(new CustomAnvilRecipe());
                break;
            case STONECUTTER:
                setCustomRecipe(new CustomStonecutterRecipe());
                break;
            case CAULDRON:
                setCustomRecipe(new CauldronRecipe());
                break;
            case GRINDSTONE:
                setCustomRecipe(new GrindstoneRecipe());
                break;
            case BREWING_STAND:
                setCustomRecipe(new BrewingRecipe());
        }
    }

    public ICustomRecipe getRecipe() {
        return getCustomRecipe(getRecipeType());
    }

    public CraftingRecipe getWorkbenchRecipe() {
        return (CraftingRecipe) getCustomRecipe(getRecipeType());
    }

    /***************************************************************
     * Getters and setters for all the Recipes that are saved in this cache.
     * Usage for the GUI Creator!
     *
     ***************************************************************/
    public CraftingRecipe getCraftingRecipe() {
        return (CraftingRecipe) getCustomRecipe(RecipeType.WORKBENCH);
    }

    public CustomAnvilRecipe getAnvilRecipe() {
        return (CustomAnvilRecipe) getCustomRecipe(RecipeType.ANVIL);
    }

    public EliteCraftingRecipe getEliteCraftingRecipe() {
        return (EliteCraftingRecipe) getCustomRecipe(RecipeType.ELITE_WORKBENCH);
    }

    public CustomBlastRecipe getBlastRecipe() {
        return (CustomBlastRecipe) getCustomRecipe(RecipeType.BLAST_FURNACE);
    }

    public CustomCampfireRecipe getCampfireRecipe() {
        return (CustomCampfireRecipe) getCustomRecipe(RecipeType.CAMPFIRE);
    }

    public CauldronRecipe getCauldronRecipe() {
        return (CauldronRecipe) getCustomRecipe(RecipeType.CAULDRON);
    }

    public CustomSmokerRecipe getSmokerRecipe() {
        return (CustomSmokerRecipe) getCustomRecipe(RecipeType.SMOKER);
    }

    public CustomStonecutterRecipe getStonecutterRecipe() {
        return (CustomStonecutterRecipe) getCustomRecipe(RecipeType.STONECUTTER);
    }

    public CustomFurnaceRecipe getFurnaceRecipe() {
        return (CustomFurnaceRecipe) getCustomRecipe(RecipeType.FURNACE);
    }

    public GrindstoneRecipe getGrindstoneRecipe() {
        return (GrindstoneRecipe) getCustomRecipe(RecipeType.GRINDSTONE);
    }

    public BrewingRecipe getBrewingRecipe() {
        return (BrewingRecipe) getCustomRecipe(RecipeType.BREWING_STAND);
    }
}

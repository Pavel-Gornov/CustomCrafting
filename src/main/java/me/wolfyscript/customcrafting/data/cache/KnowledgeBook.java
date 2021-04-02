package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeBook {

    private final CustomCrafting customCrafting;
    private final Map<CategorySettings, List<CustomItem>> cachedEliteCategoryItems;
    private Category category;
    private WorkbenchFilter workbenchFilter;

    private BukkitTask timerTask;
    private Map<Integer, Integer> timerTimings;
    private int page, subFolderPage;
    private Map<CustomItem, List<ICustomRecipe<?, ?>>> cachedSubFolderRecipes;
    private List<CustomItem> researchItems;

    public KnowledgeBook() {
        this.customCrafting = (CustomCrafting) Bukkit.getPluginManager().getPlugin("CustomCrafting");
        this.page = 0;
        this.subFolderPage = 0;
        this.category = null;
        this.researchItems = new ArrayList<>();
        this.timerTask = null;
        this.timerTimings = new HashMap<>();
        workbenchFilter = WorkbenchFilter.ALL;
        this.cachedEliteCategoryItems = new HashMap<>();
        this.cachedSubFolderRecipes = new HashMap<>();
    }

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(BukkitTask task) {
        this.timerTask = task;
    }

    public void stopTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTimings = new HashMap<>();
        }
    }

    public ICustomRecipe<?, ?> getCurrentRecipe() {
        if (getSubFolderPage() >= 0 && getSubFolderPage() < getSubFolderRecipes().size()) {
            return getSubFolderRecipes().get(getSubFolderPage());
        }
        return null;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Map<Character, ArrayList<CustomItem>> getIngredients() {
        return new HashMap<>();
    }

    public WorkbenchFilter getWorkbenchFilter() {
        return workbenchFilter;
    }

    public void setWorkbenchFilter(WorkbenchFilter workbenchFilter) {
        this.workbenchFilter = workbenchFilter;
    }

    public int getSubFolder() {
        return researchItems.size();
    }

    public List<ICustomRecipe<?, ?>> getSubFolderRecipes() {
        return this.cachedSubFolderRecipes.getOrDefault(getResearchItem(), new ArrayList<>());
    }

    public void setSubFolderRecipes(CustomItem customItem, List<ICustomRecipe<?, ?>> subFolderRecipes) {
        this.cachedSubFolderRecipes.put(customItem, subFolderRecipes);
    }

    public int getSubFolderPage() {
        return subFolderPage;
    }

    public void setSubFolderPage(int subFolderPage) {
        this.subFolderPage = subFolderPage;
    }

    public List<CustomItem> getResearchItems() {
        return researchItems;
    }

    public void addResearchItem(CustomItem item) {
        researchItems.add(0, item);
    }

    public void removePreviousResearchItem() {
        researchItems.remove(0);
    }

    public void setResearchItems(List<CustomItem> researchItems) {
        this.researchItems = researchItems;
    }

    public CustomItem getResearchItem() {
        return getResearchItems().get(0);
    }

    public List<CustomItem> getEliteRecipeItems(CategorySettings switchCategory) {
        return cachedEliteCategoryItems.getOrDefault(switchCategory, new ArrayList<>());
    }

    public void setEliteRecipeItems(CategorySettings switchCategory, List<CustomItem> recipeItems) {
        this.cachedEliteCategoryItems.put(switchCategory, recipeItems);
    }

    public void applyRecipeToButtons(GuiHandler<CCCache> guiHandler, ICustomRecipe<?,?> recipe) {
        recipe.prepareMenu(guiHandler, guiHandler.getInvAPI().getGuiCluster("recipe_book"));
    }

    public void setCachedSubFolderRecipes(Map<CustomItem, List<ICustomRecipe<?,?>>> cachedSubFolderRecipes) {
        this.cachedSubFolderRecipes = cachedSubFolderRecipes;
    }

    public enum WorkbenchFilter {
        ALL,
        ADVANCED,
        NORMAL;

        public static WorkbenchFilter next(WorkbenchFilter filter) {
            switch (filter) {
                case ALL:
                    return ADVANCED;
                case ADVANCED:
                    return NORMAL;
                case NORMAL:
                    return ALL;
            }
            return filter;
        }
    }
}

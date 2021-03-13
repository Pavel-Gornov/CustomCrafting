package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class IngredientContainerButton extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<CCCache>, List<CustomItem>> variantsMap = new HashMap<>();
    private final HashMap<GuiHandler<CCCache>, Integer> timings = new HashMap<>();

    private final HashMap<GuiHandler<CCCache>, Runnable> tasks = new HashMap<>();
    private final HashMap<GuiHandler<CCCache>, Runnable> tasksQueue = new HashMap<>();

    public IngredientContainerButton(int slot, CustomCrafting customCrafting) {
        super("ingredient.container_" + slot, ButtonType.DUMMY);
        this.customCrafting = customCrafting;
    }

    @Override
    public void init(GuiWindow guiWindow) {
        //NOT NEEDED
    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {
        //NOT NEEDED
    }

    public static void resetButtons(GuiHandler<CCCache> guiHandler) {
        GuiCluster<CCCache> cluster = guiHandler.getInvAPI().getGuiCluster("recipe_book");
        for (int i = 0; i < 54; i++) {
            Button<CCCache> btn = cluster.getButton("ingredient.container_" + i);
            if (btn != null) {
                IngredientContainerButton button = (IngredientContainerButton) btn;
                if (button.getTask(guiHandler) != null) {
                    button.setTask(guiHandler, null);
                }
                button.removeVariants(guiHandler);
                button.setTiming(guiHandler, 0);
            }
        }
    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        CCCache cache = guiHandler.getCustomCache();
        KnowledgeBook book = cache.getKnowledgeBook();
        if (getTiming(guiHandler) < getVariantsMap(guiHandler).size()) {
            CustomItem customItem = getVariantsMap(guiHandler).get(getTiming(guiHandler));
            if (!customItem.equals(book.getResearchItem())) {
                List<ICustomRecipe<?, ?>> recipes = customCrafting.getRecipeHandler().getAvailableRecipesBySimilarResult(customItem.create(), player);
                if (!recipes.isEmpty()) {
                    GuiCluster<CCCache> cluster = guiHandler.getInvAPI().getGuiCluster("recipe_book");
                    for (int i = 0; i < 36; i++) {
                        IngredientContainerButton button = (IngredientContainerButton) cluster.getButton("ingredient.container_" + i);
                        if (button.getTask(guiHandler) != null) {
                            button.removeTask(guiHandler);
                        }
                        button.removeVariants(guiHandler);
                        button.setTiming(guiHandler, 0);
                    }
                    book.stopTimerTask();
                    book.setSubFolderPage(0);
                    book.addResearchItem(customItem);
                    book.setSubFolderRecipes(customItem, recipes);
                    book.applyRecipeToButtons(guiHandler, recipes.get(0));
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        List<CustomItem> variants = getVariantsMap(guiHandler);
        inventory.setItem(slot, variants.isEmpty() ? ItemUtils.AIR : variants.get(getTiming(guiHandler)).create());
        if (getTask(guiHandler) == null) {
            setTask(guiHandler, () -> {
                if (player != null && slot < inventory.getSize()) {
                    if (!variants.isEmpty()) {
                        int variant = getTiming(guiHandler);
                        variant = ++variant < variants.size() ? variant : 0;
                        guiInventory.setItem(slot, variants.get(variant).create());
                        setTiming(guiHandler, variant);
                    }
                }
            });
        }
    }

    public void setTiming(GuiHandler<CCCache> guiHandler, int timing) {
        timings.put(guiHandler, timing);
    }

    public int getTiming(GuiHandler<CCCache> guiHandler) {
        return timings.getOrDefault(guiHandler, 0);
    }

    @NotNull
    public List<CustomItem> getVariantsMap(GuiHandler<CCCache> guiHandler) {
        return variantsMap.getOrDefault(guiHandler, new ArrayList<>());
    }

    public void removeVariants(GuiHandler<CCCache> guiHandler) {
        variantsMap.remove(guiHandler);
    }

    public void setVariants(GuiHandler<CCCache> guiHandler, Ingredient ingredient) {
        setVariants(guiHandler, ingredient.getChoices());
    }

    public void setVariants(GuiHandler<CCCache> guiHandler, Result<?> result) {
        setVariants(guiHandler, result.getChoices());
    }

    public void setVariants(GuiHandler<CCCache> guiHandler, List<CustomItem> variants) {
        if (variants != null) {
            Iterator<CustomItem> iterator = variants.iterator();
            while (iterator.hasNext()) {
                CustomItem customItem = iterator.next();
                if (!customItem.hasPermission()) {
                    continue;
                }
                if (!guiHandler.getPlayer().hasPermission(customItem.getPermission())) {
                    iterator.remove();
                }
            }
        }
        this.variantsMap.put(guiHandler, variants);
    }

    public void setTask(GuiHandler<CCCache> guiHandler, Runnable task) {
        synchronized (tasks) {
            tasks.put(guiHandler, task);
        }
    }

    public void removeTask(GuiHandler<CCCache> guiHandler) {
        synchronized (tasks) {
            tasks.remove(guiHandler);
        }
    }

    public Runnable getTask(GuiHandler<CCCache> guiHandler) {
        synchronized (tasks) {
            return tasks.get(guiHandler);
        }
    }

    public Collection<Runnable> getTasks() {
        synchronized (tasks) {
            return tasks.values();
        }
    }

    public void updateTasks() {
        tasks.putAll(tasksQueue);
    }
}

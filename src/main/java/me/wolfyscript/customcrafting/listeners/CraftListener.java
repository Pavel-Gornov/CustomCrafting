package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.types.ICraftingRecipe;
import me.wolfyscript.customcrafting.utils.RecipeUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Stream;

public class CraftListener implements Listener {

    private final CustomCrafting customCrafting;
    private final RecipeUtils recipeUtils;
    private final WolfyUtilities api;

    public CraftListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.recipeUtils = customCrafting.getRecipeUtils();
        this.api = WolfyUtilities.get(customCrafting);
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
            if (!customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof CraftingInventory)) return;
        CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
        if (event.getSlot() == 0) {
            ItemStack resultItem = inventory.getResult();
            if (ItemUtils.isAirOrNull(resultItem) || (!ItemUtils.isAirOrNull(event.getCursor()) && !event.getCursor().isSimilar(resultItem) && !event.isShiftClick())) {
                event.setCancelled(true);
                return;
            }
            if (recipeUtils.has(event.getWhoClicked().getUniqueId())) {
                event.setCancelled(true);
                ItemStack[] matrix = inventory.getMatrix();
                recipeUtils.consumeRecipe(resultItem, matrix, event);
                //Possible if there are some tick/timing base issues! inventory.setMatrix(new ItemStack[9]);
                Bukkit.getScheduler().runTask(customCrafting, () -> inventory.setMatrix(matrix));
                recipeUtils.remove(event.getWhoClicked().getUniqueId());
            }
        } else {
            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                PrepareItemCraftEvent event1 = new PrepareItemCraftEvent(inventory, event.getView(), false);
                Bukkit.getPluginManager().callEvent(event1);
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        Player player = (Player) e.getView().getPlayer();
        try {
            RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
            ItemStack[] matrix = e.getInventory().getMatrix();
            ItemStack result = recipeUtils.preCheckRecipe(matrix, player, e.isRepair(), e.getInventory(), false, true);
            if (!ItemUtils.isAirOrNull(result)) {
                e.getInventory().setResult(result);
                return;
            }
            //No valid custom recipes found
            if (!(e.getRecipe() instanceof Keyed)) return;
            //Vanilla Recipe is available.
            //api.sendDebugMessage("Detected recipe: " + ((Keyed) e.getRecipe()).getKey());
            //Check for custom recipe that overrides the vanilla recipe
            ICraftingRecipe recipe = recipeHandler.getAdvancedCraftingRecipe(NamespacedKey.of(((Keyed) e.getRecipe()).getKey()));
            if (recipeHandler.getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey().toString()) || recipe != null) {
                //Recipe is disabled or it is a custom recipe!
                e.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
            //Check for items that are not allowed in vanilla recipes.
            //If one is found, then cancel the recipe.
            if (Stream.of(matrix).parallel().map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes())) {
                e.getInventory().setResult(new ItemStack(Material.AIR));
            }
            //At this point the vanilla recipe is valid and can be crafted
            //player.updateInventory();
        } catch (Exception ex) {
            System.out.println("-------- WHAT HAPPENED? Please report! --------");
            ex.printStackTrace();
            System.out.println("-------- WHAT HAPPENED? Please report! --------");
            recipeUtils.remove(player.getUniqueId());
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}

package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.*;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AnvilCreator extends RecipeCreator {

    public AnvilCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("anvil", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new SaveButton());

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());
        registerButton(new HiddenButton());

        registerButton(new AnvilContainerButton(0, customCrafting));
        registerButton(new AnvilContainerButton(1, customCrafting));
        registerButton(new AnvilContainerButton(2, customCrafting));

        registerButton(new ActionButton("mode", new ButtonState("mode", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                CustomAnvilRecipe.Mode mode = ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getMode();
                int id = mode.getId();
                if (id < 2) {
                    id++;
                } else {
                    id = 0;
                }
                ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setMode(CustomAnvilRecipe.Mode.getById(id));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%MODE%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getMode().name());
                return itemStack;
            }
        })));
        registerButton(new ActionButton("repair_mode", new ButtonState("repair_mode", Material.GLOWSTONE_DUST, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getRepairCostMode()) + 1;
                ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getRepairCostMode().name());
                return itemStack;
            }
        })));
        registerButton(new ToggleButton("repair_apply", new ButtonState("repair_apply.true", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setApplyRepairCost(false);
            return true;
        }), new ButtonState("repair_apply.false", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton("block_repair", false, new ButtonState("block_repair.true", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(false);
            return true;
        }), new ButtonState("block_repair.false", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ToggleButton("block_rename", false, new ButtonState("block_rename.true", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockRename(false);
            return true;
        }), new ButtonState("block_rename.false", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton("block_enchant", false, new ButtonState("block_enchant.true", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(false);
            return true;
        }), new ButtonState("block_enchant.false", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton("repair_cost", new ButtonState("repair_cost", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getRepairCost());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton("durability", new ButtonState("durability", Material.IRON_SWORD, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getDurability());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setDurability(durability);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            event.setButton(0, "back");
            CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), anvilRecipe.isExactMeta());
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), anvilRecipe.isHidden());
            event.setButton(1, "hidden");
            event.setButton(3, "recipe_creator", "conditions");
            event.setButton(5, "priority");
            event.setButton(7, "exact_meta");
            event.setButton(19, "container_0");
            event.setButton(21, "container_1");
            if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                event.setButton(25, "container_2");
            } else if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                event.setButton(25, "durability");
            } else {
                event.setItem(25, new ItemStack(Material.BARRIER));
            }
            event.setButton(23, "mode");
            event.setButton(36, "block_enchant");
            event.setButton(37, "block_rename");
            event.setButton(38, "block_repair");
            event.setButton(40, "repair_apply");
            event.setButton(41, "repair_cost");
            event.setButton(42, "repair_mode");
            event.setButton(44, "save");
        }
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
        if (InventoryUtils.isCustomItemsListEmpty(anvilRecipe.getInputLeft()) && InventoryUtils.isCustomItemsListEmpty(anvilRecipe.getInputRight()))
            return false;
        System.out.println("Not empty!");
        return !anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT) || anvilRecipe.getCustomResults() != null && !anvilRecipe.getCustomResults().isEmpty();
    }
}

package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.WorldBiomeCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public class WorldBiomeConditionButton extends ActionButton<CCCache> {

    public WorldBiomeConditionButton() {
        super("conditions.world_biome", new ButtonState<>("world_biome", Material.SAND, (cache, guiHandler, player, inventory, slot, event) -> {
            GuiWindow<CCCache> window = guiHandler.getWindow();
            ICustomRecipe<?,?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("world_biome").toggleOption();
                    recipeConfig.setConditions(conditions);
                } else if (!((InventoryClickEvent) event).isShiftClick()) {
                    //CONFIGURE ELITE WORKBENCHES
                    guiHandler.getWindow().openChat("world_biome", guiHandler, (guiHandler1, player1, s, args) -> {
                        if (!s.isEmpty()) {
                            try {
                                Biome biome = Biome.valueOf(s.toUpperCase(Locale.ROOT));
                                WorldBiomeCondition condition = (WorldBiomeCondition) conditions.getByID("world_biome");
                                if (condition.getBiomes().contains(biome.toString())) {
                                    window.sendMessage(player1, "already_existing");
                                    return true;
                                }
                                ((WorldBiomeCondition) conditions.getByID("world_biome")).addBiome(biome.toString());
                                recipeConfig.setConditions(conditions);
                                return false;
                            } catch (IllegalArgumentException ex) {
                                window.sendMessage(player1, "invalid_biome");
                            }
                        }
                        return true;
                    });
                } else {
                    if (((WorldBiomeCondition) conditions.getByID("world_biome")).getBiomes().size() > 0) {
                        ((WorldBiomeCondition) conditions.getByID("world_biome")).getBiomes().remove(((WorldBiomeCondition) conditions.getByID("world_biome")).getBiomes().size() - 1);
                        recipeConfig.setConditions(conditions);
                    }
                }
            }

            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            WorldBiomeCondition condition = (WorldBiomeCondition) guiHandler.getCustomCache().getRecipe().getConditions().getByID("world_biome");
            hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.getApi()));
            for (int i = 0; i < 4; i++) {
                if (i < condition.getBiomes().size()) {
                    hashMap.put("%var" + i + "%", condition.getBiomes().get(i));
                } else {
                    hashMap.put("%var" + i + "%", "...");
                }
            }
            return itemStack;
        }));
    }
}

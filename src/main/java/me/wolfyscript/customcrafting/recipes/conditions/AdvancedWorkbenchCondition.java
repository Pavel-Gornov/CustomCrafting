package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Location;

public class AdvancedWorkbenchCondition extends Condition {

    public AdvancedWorkbenchCondition() {
        super("advanced_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof CraftingRecipe) {
            if (data.getBlock() != null) {
                Location location = data.getBlock().getLocation();
                CustomItem customItem = WorldUtils.getWorldCustomItemStore().getCustomItem(location);
                return customItem != null && customItem.getNamespacedKey().equals(new NamespacedKey("customcrafting", "advanced_crafting_table"));
            }
            return false;
        }
        return true;
    }
}

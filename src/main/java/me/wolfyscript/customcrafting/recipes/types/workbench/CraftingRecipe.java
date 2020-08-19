package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CraftingRecipe extends CustomRecipe implements ICraftingRecipe {

    protected static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    protected boolean shapeless;

    protected List<CustomItem> result;
    protected Map<Character, List<CustomItem>> ingredients;

    public CraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        //Get Ingredients
        {
            Map<Character, List<CustomItem>> ingredients = new TreeMap<>();
            JsonNode ingredientsNode = node.path("ingredients");
            ingredientsNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                List<CustomItem> data = new ArrayList<>();
                entry.getValue().elements().forEachRemaining(item -> data.add(new CustomItem(mapper.convertValue(item, APIReference.class))));
                ingredients.put(key.charAt(0), data.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList()));
            });
            this.ingredients = ingredients;
        }
    }

    public CraftingRecipe(){
        super();
        this.result = new ArrayList<>();
        this.ingredients = new HashMap<>();
    }

    public CraftingRecipe(CraftingRecipe craftingRecipe){
        super(craftingRecipe);
        this.result = craftingRecipe.getCustomResults();
        this.ingredients = craftingRecipe.getIngredients();
    }

    @Override
    public Map<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public List<CustomItem> getIngredients(char key) {
        return new ArrayList<>(getIngredients().getOrDefault(key, new ArrayList<>()));
    }

    @Override
    public List<CustomItem> getIngredients(int slot) {
        return getIngredients(LETTERS[slot]);
    }

    @Override
    public void setIngredients(char key, List<CustomItem> ingredients) {
        this.ingredients.put(key, ingredients);
    }

    @Override
    public void setIngredients(int slot, List<CustomItem> ingredients) {
        this.ingredients.put(LETTERS[slot], ingredients);
    }

    @Override
    public CustomItem getIngredient(int slot) {
        return getIngredient(LETTERS[slot]);
    }

    @Override
    public CustomItem getIngredient(char key) {
        List<CustomItem> list = getIngredients(key);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void setIngredient(int slot, int variant, CustomItem customItem) {
        setIngredient(LETTERS[slot], variant, customItem);
    }

    @Override
    public void setIngredient(char key, int variant, CustomItem customItem) {
        List<CustomItem> ingredient = getIngredients(key);
        if (variant < ingredient.size()) {
            if (ItemUtils.isAirOrNull(customItem)) {
                ingredient.remove(variant);
            } else {
                ingredient.set(variant, customItem);
            }
        } else if (!ItemUtils.isAirOrNull(customItem)) {
            ingredient.add(customItem);
        }
        ingredients.put(key, ingredient);
    }

    public void setIngredients(Map<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public void setResult(int variant, CustomItem customItem) {
        if (variant < result.size()) {
            if (ItemUtils.isAirOrNull(customItem)) {
                result.remove(variant);
            } else {
                result.set(variant, customItem);
            }
        } else if (!ItemUtils.isAirOrNull(customItem)) {
            result.add(customItem);
        }
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return new ArrayList<>(result);
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.WORKBENCH;
    }

    @Override
    public boolean isShapeless() {
        return shapeless;
    }

    @Override
    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
        event.setButton(0, "back");
        if (!getIngredients().isEmpty()) {
            if (getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT)) {
                for (int i = 1; i < 9; i++) {
                    event.setButton(i, "none", "glass_purple");
                }
                for (int i = 36; i < 45; i++) {
                    event.setButton(i, "none", "glass_purple");
                }
            }
            if (getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT)) {
                //TODO display for admins
            }
            List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("advanced_workbench") && !condition.getId().equals("permission")).collect(Collectors.toList());
            int startSlot = 9 / (conditions.size() + 1);
            int slot = 0;
            for (Condition condition : conditions) {
                if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                    event.setButton(36 + startSlot + slot, "recipe_book", "conditions." + condition.getId());
                    slot += 2;
                }
            }
            event.setButton(23, "recipe_book", isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");
            int invSlot;
            for (int i = 0; i < 9; i++) {
                invSlot = 10 + i + (i / 3) * 6;
                event.setButton(invSlot, "recipe_book", "ingredient.container_" + invSlot);
            }
            event.setButton(25, "recipe_book", "ingredient.container_25");
        }
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("shapeless", shapeless);
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : getCustomResults()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeObjectFieldStart("ingredients");
            for (Map.Entry<Character, List<CustomItem>> entry : ingredients.entrySet()) {
                List<CustomItem> ingred = entry.getValue();
                if(!InventoryUtils.isCustomItemsListEmpty(ingred)){
                    gen.writeArrayFieldStart(entry.getKey().toString());
                    for (CustomItem customItem : ingred) {
                        gen.writeObject(customItem.getApiReference());
                    }
                    gen.writeEndArray();
                }
            }
            gen.writeEndObject();
        }
    }
}
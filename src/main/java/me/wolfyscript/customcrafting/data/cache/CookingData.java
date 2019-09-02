package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class CookingData {

    private boolean exactMeta;
    private RecipePriority priority;

    private HashMap<Integer, List<CustomItem>> ingredients;
    private List<CustomItem> source;
    private List<CustomItem> result;

    private boolean advFurnace;
    private int cookingTime;
    private float experience;

    public CookingData() {
        this.ingredients = new HashMap<>();
        this.source = Collections.singletonList(new CustomItem(Material.AIR));
        this.result = Collections.singletonList(new CustomItem(Material.AIR));

        this.experience = 0.2f;

        this.advFurnace = true;
        this.cookingTime = 60;

        this.exactMeta = true;
    }

    public void setSource(List<CustomItem> source) {
        ingredients.put(0, source);
    }

    public List<CustomItem> getSource() {
        return ingredients.getOrDefault(0, Collections.singletonList(new CustomItem(Material.AIR)));
    }

    public List<CustomItem> getResult() {
        return ingredients.getOrDefault(1, Collections.singletonList(new CustomItem(Material.AIR)));
    }

    public void setResult(List<CustomItem> result) {
        ingredients.put(1, result);
    }

    public void setIngredients(int slot, List<CustomItem> ingredient) {
        ingredients.put(slot, ingredient);
    }

    public List<CustomItem> getIngredients(int slot) {
        return ingredients.getOrDefault(slot, Collections.singletonList(new CustomItem(Material.AIR)));
    }

    public void setIngredient(int slot, int variant, CustomItem ingredient) {
        List<CustomItem> ingredients = getIngredients(slot);
        if (variant < ingredients.size())
            ingredients.set(variant, ingredient);
        else
            ingredients.add(ingredient);
        setIngredients(slot, ingredients);
    }

    public boolean isAdvFurnace() {
        return advFurnace;
    }

    public void setAdvFurnace(boolean advFurnace) {
        this.advFurnace = advFurnace;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }
}

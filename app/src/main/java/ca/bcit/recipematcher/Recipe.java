package ca.bcit.recipematcher;

import java.util.List;

public class Recipe {
    private String recipeName;
    private String ingredients;
    private String category;
    private int rating;
    private List<String> stepList;

    // Empty constructor required by Firestore
    public Recipe() {};

    public Recipe(String recipeName, String ingredients, String category, List<String> stepList) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.category = category;
        this.stepList = stepList;
        rating = 0;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getCategory() {
        return category;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getStepList() {
        return stepList;
    }
}

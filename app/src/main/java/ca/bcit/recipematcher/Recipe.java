package ca.bcit.recipematcher;

import java.util.List;

public class Recipe {
    private String recipeName;
    private List<String> ingredients;
    private String category;
    private double rating;
    private List<String> stepList;
    private String imageURL;
    private String userUid;
    private int ratingCount;

    // Empty constructor required by Firestore
    public Recipe() {};

    public Recipe(String recipeName, List<String> ingredients, String category, List<String> stepList, String imageURL, String userUid) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.category = category;
        this.stepList = stepList;
        this.imageURL = imageURL;
        this.userUid = userUid;
        rating = 0;
        ratingCount = 0;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getCategory() {
        return category;
    }

    public double getRating() {
        return rating;
    }

    public List<String> getStepList() {
        return stepList;
    }

    public String getImageURL() { return imageURL; }

    public String getUserUid() { return userUid; }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStepList(List<String> stepList) {
        this.stepList = stepList;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}

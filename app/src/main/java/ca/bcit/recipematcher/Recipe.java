package ca.bcit.recipematcher;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {
    private String recipeName;
    private String ingredients;
    private String category;
    private int rating;
    private List<String> stepList;
    private String imageURL;
    private String userUid;

    // Empty constructor required by Firestore
    public Recipe() {};

    public Recipe(String recipeName, String ingredients, String category, List<String> stepList, String imageURL, String userUid) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.category = category;
        this.stepList = stepList;
        this.imageURL = imageURL;
        this.userUid = userUid;
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

    public String getImageURL() { return imageURL; }

    public String getUserUid() { return userUid; }

    /**
     * These methods are needed to parcel the recipe from main activity to the fragment
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}

package ca.bcit.recipematcher;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String phone;
    private String image;
    private List<String> recipes;
    private List<String> favourites;

    public User() {

    }
    public User(String name, String email, String phone, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.recipes = new ArrayList<>();
        this.favourites = new ArrayList<>();
    }

    public List<String> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<String> favourites) {
        this.favourites = favourites;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getRecipes() { return recipes; }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRecipes(List<String> recipes) { this.recipes = recipes; };
}

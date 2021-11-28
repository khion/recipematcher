package ca.bcit.recipematcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavouritedRecipesActivity extends AppCompatActivity {

    // Firebase variables
    private FirebaseUser user;
    private FirebaseFirestore db;

    // Layout variables
    private ListView mListFavouritedRecipes;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourited_recipes);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        db = FirebaseFirestore.getInstance();

        mListFavouritedRecipes = findViewById(R.id.favourited_recipe_list);

        List<Recipe> recipeList = new ArrayList<>();
        DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                displayFavouritedRecipes(recipeList, documentSnapshot);
            }
        });
    }

    /**
     * Display all recipes the user has uploaded. If the user has not uploaded any recipes, display
     * a text view saying You Haven't Uploaded Any Recipes
     * @param recipeList list of recipes
     * @param documentSnapshot DocumentSnapshot of the user
     */
    public void displayFavouritedRecipes(List<Recipe> recipeList, DocumentSnapshot documentSnapshot) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int recipesAdded = 0;
                User user = documentSnapshot.toObject(User.class);
                List<String> recipeIDs = user.getFavourites();
                for (String s: recipeIDs) {
                    recipesAdded++;
                    DocumentReference recipeRef = db.collection("recipes").document(s);
                    recipeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
                            recipeList.add(recipe);
                            if (recipeList.size() == recipeIDs.size()) {
                                RecipeFavouritedAdapter adapter = new RecipeFavouritedAdapter(
                                        FavouritedRecipesActivity.this, recipeList, recipeIDs, db, userID);
                                mListFavouritedRecipes.setAdapter(adapter);
                            }
                        }
                    });
                }
                int finalRecipesAdded = recipesAdded;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalRecipesAdded == 0) {
                            TextView noRecipesTextView = findViewById(R.id.no_favs_tv);
                            noRecipesTextView.setText(getResources().getString(R.string.no_favs_text));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
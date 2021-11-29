package ca.bcit.recipematcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ViewRecipeActivity extends AppCompatActivity {

    // Firebase variables
    FirebaseFirestore db;
    FirebaseUser user;

    // Layout variables
    Button favButton;
    Button submitRatingButton;

    private String userID;
    private String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        favButton = findViewById(R.id.add_fav_btn);

        Bundle bundle = getIntent().getExtras();
        recipeID = bundle.getString("RecipeID");
        displayRecipe(recipeID);

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userID != null) {
                    DocumentReference userRef = db.collection("users").document(userID);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            List<String> favList = user.getFavourites();
                            if (!favList.contains(recipeID)) {
                                userRef.update("favourites", FieldValue.arrayUnion(recipeID));
                            } else {
                                Toast.makeText(getApplicationContext(), "Recipe already added to favourites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if (userID == null) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewRecipeActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.login_dialog, null);
                    dialogBuilder.setView(dialogView);

                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();


                    final Button cancel_button = dialogView.findViewById(R.id.cancel_button);
                    final Button login_button = dialogView.findViewById(R.id.btnLogin);

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    login_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ViewRecipeActivity.this, LandingActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    /**
     * Display the recipe stored in the database under the given ID
     * @param recipeID the ID of a recipe
     */
    public void displayRecipe(String recipeID) {
        TextView name = findViewById(R.id.recipe_name);
        TextView ingredients = findViewById(R.id.recipe_ingredients);
        TextView steps = findViewById(R.id.recipe_steps);
        ImageView image = findViewById(R.id.recipe_image);
        TextView ratingText = findViewById(R.id.recipe_rating_tv);
        submitRatingButton = findViewById(R.id.submit_rating_button);
        TextView submittedTV = findViewById(R.id.submitted_tv);
        DocumentReference recipeDocRef = db.collection("recipes").document(recipeID);
        recipeDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Recipe r = documentSnapshot.toObject(Recipe.class);
                name.setText(r.getRecipeName());

                String ingredientsString = "";

                for (int i = 0; i < r.getIngredients().size(); i++) {
                    ingredientsString += "• " + r.getIngredients().get(i);

                    if (i + 1 < r.getIngredients().size()) {
                        ingredientsString += "\n\n";
                    }
                }

                ingredients.setText(ingredientsString);

                List<String> stepList = r.getStepList();
                String stepListString = "";

                for (int i = 0; i < stepList.size(); i++) {
                    stepListString += "Step " + (i + 1) + ": " + stepList.get(i);

                    if (i + 1 < r.getStepList().size()) {
                        stepListString += "\n\n";
                    }
                }

                steps.setText(stepListString);

                if (user == null) {
                    submitRatingButton.setVisibility(View.INVISIBLE);
                    submittedTV.setText("Log in to rate");
                }

                double recipeRating = r.getRating();

                if (userID != null) {
                    handleRatings(r, recipeDocRef, recipeRating);
                }


                Picasso.get().load(r.getImageURL()).into(image);

                int ratingCount = r.getRatingCount();
                double rating = recipeRating / ratingCount;
                if (ratingCount == 0) {
                    ratingText.setText(getResources().getString(R.string.no_ratings_text));
                } else {
                    // Format rating to show one decimal point
                    ratingText.setText(String.format(Locale.CANADA, "%.1f", rating));
                }

            }
        });
    }

    /**
     * Allow logged in users to rate the recipes. Users can only rate once. Users without an account
     * cannot rate
     * @param r a Recipe
     * @param recipeDocRef DocumentReference to a recipe
     * @param recipeRating the Recipe's rating
     */
    public void handleRatings(Recipe r, DocumentReference recipeDocRef, double recipeRating) {
        DocumentReference userDocRef = db.collection("users").document(userID);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                List<String> ratedRecipes = user.getRatedRecipes();
                if (recipeID != null) {
                    if (ratedRecipes.contains(recipeID)) {
                        submitRatingButton.setVisibility(View.INVISIBLE);
                    } else {
                        submitRatingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                submitRatingButton.setVisibility(View.INVISIBLE);
                                RatingBar ratingBar = findViewById(R.id.recipe_rating_bar);
                                int newRating = (int) ratingBar.getRating();
                                int ratingCount = r.getRatingCount();
                                ratingCount++;
                                recipeDocRef.update("rating", (recipeRating + newRating));
                                recipeDocRef.update("ratingCount", ratingCount);
                                userDocRef.update("ratedRecipes", FieldValue.arrayUnion(recipeID));
                            }
                        });
                    }
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
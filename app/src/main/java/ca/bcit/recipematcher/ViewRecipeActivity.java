package ca.bcit.recipematcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewRecipeActivity extends AppCompatActivity {

    // Firebase variables
    FirebaseFirestore db;
    FirebaseUser user;

    // Layout variables
    Button favButton;

    private String userID;

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
        String recipeID = bundle.getString("RecipeID");
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
        DocumentReference recipeDocRef = db.collection("recipes").document(recipeID);
        recipeDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Recipe recipe = documentSnapshot.toObject(Recipe.class);
                TextView name = findViewById(R.id.recipe_name_tv);
                ImageView image = findViewById(R.id.recipe_image_view);
                assert recipe != null;
                name.setText(recipe.getRecipeName());
                Picasso.get().load(recipe.getImageURL()).into(image);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
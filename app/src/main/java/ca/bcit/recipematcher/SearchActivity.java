package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final Object TAG = 1;
    private EditText search_view;
    private FirebaseFirestore mRef;
    private ListView lvRecipeList;
    private List<Recipe> recipeResults;
    private LottieAnimationView animationView;

    private FirebaseUser currentUser;
    private String userID;
    FirebaseFirestore db;
    private List<String> recipeDocIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        search_view = findViewById(R.id.edit_Search);
        mRef = FirebaseFirestore.getInstance();
        lvRecipeList = findViewById(R.id.searchList);
        recipeResults = new ArrayList<Recipe>();
        animationView = findViewById(R.id.logo_animate);


         recipeDocIds = new ArrayList<>();

        lvRecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Recipe recipe = recipeResults.get(i);
                String recipeId = recipeDocIds.get(i);

                showRecipeDialog(recipeId, recipe.getRecipeName(),
                        recipe.getCategory(),
                        recipe.getIngredients().toString(), recipe.getImageURL(),
                        recipe.getStepList());
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
        }
        db = FirebaseFirestore.getInstance();


    }


    /**
     * On click method that will display a list of recipes corresponding to
     * the search string values
     *
     * @param view view
     */
    public void onSearchClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        String search_string = search_view.getText().toString().trim();
        if (TextUtils.isEmpty(search_string)) {
            Toast.makeText(SearchActivity.this, "Enter some keywords", Toast.LENGTH_SHORT).show();
        } else {
            mRef.collection("recipes")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipeDocIds.add(document.getId());
                            String recipeName = recipe.getRecipeName();
                            String recipeIng = "";
                            for (String ing : recipe.getIngredients()) {
                                if (ing.equals(search_string)) {
                                    recipeIng = ing;
                                }
                            }
                            if (recipeName.contains(search_string) || recipeIng.contains(search_string)) {
                                recipeResults.add(recipe);
                            }

                            animationView.setVisibility(View.INVISIBLE);
                        }
                        RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                        lvRecipeList.setAdapter(adapter);
                    }
                }


            });
        }
    }

    public void onPastaClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeDocIds.add(document.getId());
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Pasta")) {
                            recipeResults.add(recipe);
                        }
                        animationView.setVisibility(View.INVISIBLE);
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });


    }

    public void onAsianClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeDocIds.add(document.getId());
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Asian")) {
                            recipeResults.add(recipe);
                        } else {
                            Toast.makeText(SearchActivity.this, "No Asian Recipes", Toast.LENGTH_SHORT).show();
                        }
                        animationView.setVisibility(View.INVISIBLE);
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onMainClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeDocIds.add(document.getId());
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Main")) {
                            recipeResults.add(recipe);
                        }
                        animationView.setVisibility(View.INVISIBLE);
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onDessertClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeDocIds.add(document.getId());
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Dessert")) {
                            recipeResults.add(recipe);
                        }
                        animationView.setVisibility(View.INVISIBLE);
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onHotClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeDocIds.add(document.getId());
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Hot")) {
                            recipeResults.add(recipe);
                        }
                        animationView.setVisibility(View.INVISIBLE);
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onColdClick(View view) {
        recipeResults.clear();
        recipeDocIds.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeDocIds.add(document.getId());
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Cold")) {
                            recipeResults.add(recipe);
                        }
                        animationView.setVisibility(View.INVISIBLE);
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }


    /**
     * This will show dialog of the recipe information.
     *
     * @param recipeName        recipeName
     * @param recipeCategory    recipeCategory
     * @param recipeIngredients recipeIngredients
     * @param imageURl          imageURL
     */
    public void showRecipeDialog(final String recipeId, final String recipeName, String recipeCategory, String recipeIngredients, String imageURl, List<String> steps) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.search_dialog, null);
        dialogBuilder.setView(dialogView);

        final ImageView imageView = dialogView.findViewById(R.id.image_id);
        Picasso.get().load(imageURl).into(imageView);

        final TextView tvRecipeName = dialogView.findViewById(R.id.recipe_name);
        tvRecipeName.setText(recipeName);

        final TextView tvRecipeCategory = dialogView.findViewById(R.id.recipe_category);
        tvRecipeCategory.setText(recipeCategory);

        final TextView tvRecipeIngredients = dialogView.findViewById(R.id.recipe_ingredients);
        tvRecipeIngredients.setText(recipeIngredients);

        TableLayout instructionTable = (TableLayout) dialogView.findViewById(R.id.recipe_instructions);


        for (int i = 0; i < steps.size(); i++) {


            TableRow instructionRow = new TableRow(SearchActivity.this);

            instructionRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            // New step text
            TextView recipeStepText = new TextView(SearchActivity.this);
            recipeStepText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            String text = "Step " + (i + 1) + ":";
            recipeStepText.setText(text);

            // New step input
            TextView recipeStepInput = new TextView(SearchActivity.this);
            recipeStepInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            System.out.println(steps.get(i));
            recipeStepInput.setText(steps.get(i));

            instructionRow.addView(recipeStepText);
            instructionRow.addView(recipeStepInput);

            if (instructionTable != null) {
                instructionTable.addView(instructionRow);
            }
        }

        final AlertDialog alertDialog = dialogBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        alertDialog.show();
        alertDialog.getWindow().setAttributes(lp);


        final TextView exit_btn = dialogView.findViewById(R.id.exit);

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        final Button favButton = dialogView.findViewById(R.id.add_fav_btn);


        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userID != null) {
                    DocumentReference userRef = db.collection("users").document(userID);

                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            assert user != null;
                            List<String> favList = user.getFavourites();
                            if (!favList.contains(recipeId)) {
                                userRef.update("favourites", FieldValue.arrayUnion(recipeId));
                            } else {
                                Toast.makeText(getApplicationContext(), "Recipe already added to favourites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if (userID == null) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SearchActivity.this);
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
                            Intent intent = new Intent(SearchActivity.this, LandingActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onLogoutClick(MenuItem menu) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onUserProfileClick(MenuItem menu) {
        if (userID != null) {
            Intent intent = new Intent(SearchActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }
        if (userID == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SearchActivity.this);
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
                    Intent intent = new Intent(SearchActivity.this, LandingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
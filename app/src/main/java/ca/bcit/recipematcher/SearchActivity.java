package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final Object TAG = 1;
    private EditText search_view;
    private FirebaseFirestore mref;
    private ListView lvRecipeList;
    private List<Recipe> recipeResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        search_view = findViewById(R.id.edit_Search);
        mref = FirebaseFirestore.getInstance();
        lvRecipeList = findViewById(R.id.searchList);
        recipeResults = new ArrayList<Recipe>();

        lvRecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Recipe recipe = recipeResults.get(i);

                showRecipeDialog(recipe.getRecipeName(),
                        recipe.getCategory(),
                        recipe.getIngredients());
            }
        });

    }


    /**
     * On click method that will display a list of recipes corresponding to
     * the search string values
     * @param view view
     */
    public void onSearchClick(View view) {
        String search_string = search_view.getText().toString().trim();
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeName = recipe.getRecipeName();
                        String recipeIng = recipe.getIngredients();
                        if (recipeName.contains(search_string) || recipeIng.contains(search_string)) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onPastaClick(View view) {
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Pasta")) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });

    }

    public void onAsianClick(View view) {
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Asian")) {
                            recipeResults.add(recipe);
                        } else {
                            Toast.makeText(SearchActivity.this, "No Asian Recipes", Toast.LENGTH_SHORT).show();
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onMainClick(View view) {
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Main")) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onDessertClick(View view) {
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Dessert")) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onHotClick(View view) {
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Hot")) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }

    public void onColdClick(View view) {
        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeCategory = recipe.getCategory();
                        if (recipeCategory != null && recipeCategory.equals("Cold")) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);
                }
            }
        });
    }


    public void showRecipeDialog(final String recipeName, String recipeCategory, String recipeIngredients) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.search_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView tvRecipeName = dialogView.findViewById(R.id.recipe_name);
        tvRecipeName.setText(recipeName);

        final TextView tvRecipeCategory = dialogView.findViewById(R.id.recipe_category);
        tvRecipeCategory.setText(recipeCategory);

        final TextView tvRecipeIngredients = dialogView.findViewById(R.id.recipe_ingredients);
        tvRecipeIngredients.setText(recipeIngredients);

        final TextView tvRecipeSteps = dialogView.findViewById(R.id.recipe_steps);
//        tvRecipeSteps.setText(steps);


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


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onLogoutClick(MenuItem menu) {
//        FirebaseAuth fAuth = FirebaseAuth.getInstance();
//        fAuth.signOut();
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onUserProfileClick(MenuItem menu) {
//        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
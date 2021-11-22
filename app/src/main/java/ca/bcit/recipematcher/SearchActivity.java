package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

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
    private ProgressBar progressBar;


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
    }


    public void onSearchClick(View view) {

        String search_string = search_view.getText().toString().trim();
        System.out.println("=====================");
        System.out.println(search_string);

        mref.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        String recipeName = recipe.getRecipeName();
                        if (recipeName.contains(search_string)) {
                            recipeResults.add(recipe);
                        }
                    }
                    RecipeSearchAdapter adapter = new RecipeSearchAdapter(SearchActivity.this, recipeResults);
                    lvRecipeList.setAdapter(adapter);

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
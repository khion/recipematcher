package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Menu menu_bar;
    private List<Recipe> recipeList;
    private FirebaseFirestore mRef;
    private int swipeListIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRef = FirebaseFirestore.getInstance();
        recipeList = new ArrayList<Recipe>();
        recipeList.clear();
        mRef.collection("recipes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeList.add(recipe);
                    }
                }
            }
        });

        swipeListIndex = new Random().nextInt(recipeList.size()); // errors out due to recipeList not being filled

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RecipeDisplayFragment fragment = new RecipeDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentRecipe", recipeList.get(swipeListIndex));
        fragment.setArguments(bundle);
        ft.replace(R.id.recipe_placeholder, fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onLogoutClick(MenuItem menu) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * This will check if the user signed in and if user is signed in,
     * it will direct to UserProfile Activity be the button is clicked
     * @param menu menu
     */
    public void onUserProfileClick(MenuItem menu) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(this, UserProfileActivity.class);
        } else {
            intent = new Intent(this, LandingActivity.class);
        }
        startActivity(intent);
    }

    public void onSearchClick(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);

    }

    public void onUploadClick(View view) {
        Intent intent = new Intent(this, UploadRecipeActivity.class);
        startActivity(intent);
    }

    public void onStreamingClick(View view) {
        Intent intent = new Intent(this, VideoStreamingActivity.class);
        startActivity(intent);
    }
}
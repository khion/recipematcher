package ca.bcit.recipematcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Menu menu_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.recipe_placeholder, new RecipeDisplayFragment());
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
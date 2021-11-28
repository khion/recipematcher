package ca.bcit.recipematcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Menu menu_bar;
    private FirebaseUser currentUser;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.recipe_placeholder, new RecipeDisplayFragment());
        ft.commit();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
        }
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
        if (userID != null) {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }
        if (userID == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
                    Intent intent = new Intent(MainActivity.this, LandingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void onSearchClick(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);

    }

    public void onUploadClick(View view) {
        if (userID != null) {
            Intent intent = new Intent(MainActivity.this, UploadRecipeActivity.class);
            startActivity(intent);
        }
        if (userID == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
                    Intent intent = new Intent(MainActivity.this, LandingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void onStreamingClick(View view) {
        Intent intent = new Intent(this, VideoStreamingActivity.class);
        startActivity(intent);
    }
}
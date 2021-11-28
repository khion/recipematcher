package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    private String userID;

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView mButtonChooseImage;
    private Button mButtonSave;
    private Button mButtonRecipeList;
    private ListView mListUploadedRecipes;

    private CircleImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;


    private String fullName;
    private String email;
    private String phone;
    private String imageUrl;

    private User userProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        db = FirebaseFirestore.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("user");

        // Display user name, email//
        TextView fullNameTextView = (TextView) findViewById(R.id.user_name);
        TextView emailTextView = (TextView) findViewById(R.id.user_email);

        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mListUploadedRecipes = findViewById(R.id.uploaded_recipe_list);


        DocumentReference userDocRef = db.collection("users").document(userID);
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    fullName = user.getName();
                    email = user.getEmail();
                    phone = user.getPhone();
                    imageUrl = user.getImage();

                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);
                }
            }
        });


        // Image upload codes in on create method ///
        mButtonChooseImage = findViewById(R.id.edit_image);
        mButtonSave = findViewById(R.id.save_image);
        mButtonRecipeList = findViewById(R.id.collection_btn);
        mImageView = findViewById(R.id.profile_image);
        mProgressBar = findViewById(R.id.progress_bar);
        mButtonSave.setVisibility(View.INVISIBLE);
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        mButtonRecipeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUploadedRecipesActivity();
            }
        });
    }

    private void startUploadedRecipesActivity() {
        Intent i = new Intent(this, UploadedRecipesActivity.class);
        startActivity(i);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void onUploadRecipeClick(View view) {
        Intent intent = new Intent(this, UploadRecipeActivity.class);
        startActivity(intent);
    }

    /**
     * This will display the image that user choose into the imageview
     * @param requestCode int
     * @param resultCode int
     * @param data int
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);

            mButtonSave.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This will allow user to logout to the app.
     * @param view view
     */
    public void onLogoutClick(View view) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
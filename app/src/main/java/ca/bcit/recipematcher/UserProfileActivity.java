package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
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
    private Button mButtonFavouriteList;
    private ListView mListUploadedRecipes;

    private CircleImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;


    private String fullName;
    private String email;
    private String phone;
    private String imageUrl;
    private Button editProfile;

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
                    imageUrl = user.getImage();

                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);

                    if (imageUrl == null) {
                        mImageView.setImageResource(R.drawable.default_profile);
                    } else {

                        Picasso.get().load(imageUrl).into(mImageView);
                    }
                }
            }
        });


        // Image upload codes in on create method ///
        mButtonChooseImage = findViewById(R.id.edit_image);
        mButtonSave = findViewById(R.id.save_image);
        mButtonRecipeList = findViewById(R.id.collection_btn);
        mButtonFavouriteList = findViewById(R.id.favourites_btn);
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

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(UserProfileActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mButtonFavouriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFavouriteRecipesActivity();
            }
        });

        editProfile = findViewById(R.id.edit);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog(fullName, email, phone);
            }
        });
    }


    private void startFavouriteRecipesActivity() {
        Intent i = new Intent(this, FavouritedRecipesActivity.class);
        startActivity(i);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * TO get teh extension file type of the image.
     * @param uri Uri
     * @return string extension file type
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * This method uploads image
     */
    private void uploadFile() {

        if(mImageUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference sRef = mStorageRef.child(userID + "." + getFileExtension(mImageUri));

            sRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl =  uri.toString();

                                    User user = new User(fullName,email,phone, downloadUrl);
                                    db.collection("users").document(userID).set(user);
                                }
                            });
                            Toast.makeText(getApplicationContext(), "File Upload", Toast.LENGTH_SHORT).show();
                            mButtonSave.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

                }
            });
        }
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
     * This method will update user data from the firebase database
     * @param Username as string for user name
     * @param email as string for user email
     * @param phone as string for phone number
     */
    private void updateUser(String Username, String email, String phone) {
        DocumentReference dbRef = db.collection("users").document(userID);
        User user = new User(Username,email,phone, imageUrl);

        Task setValueTask = dbRef.set(user);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UserProfileActivity.this,
                        "Your profile is Updated.",Toast.LENGTH_LONG).show();

                Intent i = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                startActivity(i);
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this,
                        "Something went wrong.\n" + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method will show a dialog to allow user to edit their email and name
     * @param userName user name
     * @param email user email
     * @param phone user phone number
     */
    private void showUpdateDialog(String userName, String email, String phone) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_user_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextUserName = dialogView.findViewById(R.id.editTextUserName);
        editTextUserName.setText(userName);

        final EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        editTextEmail.setText(email);

        final Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle("Change profile information");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = editTextUserName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    editTextUserName.setError("User Name is required");
                    return;
                } else if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("email is required");
                    return;
                }

                updateUser(userName, email, phone);

                alertDialog.dismiss();
            }
        });

    }


    public void onUploadRecipeClick(View view) {
        Intent intent = new Intent(this, UploadRecipeActivity.class);
        startActivity(intent);
    }

    private void startUploadedRecipesActivity() {
        Intent i = new Intent(this, UploadedRecipesActivity.class);
        startActivity(i);
    }

    public void onYoutubeClick(View view) {
        Intent intent = new Intent(this, VideoStreamingActivity.class);
        startActivity(intent);
    }
}
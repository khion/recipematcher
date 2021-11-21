package ca.bcit.recipematcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadRecipeActivity extends AppCompatActivity {

    ImageView viewImage;
    Button selectImage;
    Button uploadRecipe;
    Spinner categorySpinner;
    int stepNum = 1;
    Uri selectedImage;
    List<EditText> stepsList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        // ActionBar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        stepsList = new ArrayList<>();

        // Image Selector
        selectImage = (Button) findViewById(R.id.btnSelectPhoto);
        uploadRecipe = findViewById(R.id.btnUpload);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        categorySpinner = findViewById(R.id.category_spinner);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        uploadRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText recipeNameET = findViewById(R.id.upload_recipe_name_et);
                EditText ingredientsET = findViewById(R.id.upload_recipe_ingredients_et);
                EditText stepET = findViewById(R.id.upload_recipe_step1_et);
                List<String> stepListStrings = new ArrayList<>();


                String recipeName = recipeNameET.getText().toString();
                String ingredients = ingredientsET.getText().toString();
                String step = stepET.getText().toString();
                String category = String.valueOf(categorySpinner.getSelectedItem());
                stepListStrings.add(step);
                for (EditText et: stepsList) {
                    stepListStrings.add(et.getText().toString());
                }
                Recipe recipe = new Recipe(recipeName, ingredients, category, stepListStrings);
                db.collection("recipes")
                        .add(recipe)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Tag", "Error adding document", e);
                            }
                        });
            }
        });

//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);
//
//// Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("Tag", "Error adding document", e);
//                    }
//                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadRecipeActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }


        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : Objects.requireNonNull(f.listFiles())) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    viewImage.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    OutputStream outFile;
                    File file = new File(path, System.currentTimeMillis() + ".jpg");

                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                Picasso.get().load(selectedImage).into(viewImage);
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("Path of image from gallery:", picturePath + "");
                viewImage.setImageBitmap(thumbnail);
            }
        }
    }


    public void onAddStepClick(View view) {
        stepNum++;

        TableLayout instructionTable = (TableLayout) findViewById(R.id.recipe_instructions);

        TableRow instructionRow = new TableRow(this);
        instructionRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        // New step text
        TextView recipeStepText = new TextView(this);
        recipeStepText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        String text = "Step " + stepNum + ":";
        recipeStepText.setText(text);

        // New step input
        EditText recipeStepInput = new EditText(this);
        recipeStepInput.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        recipeStepInput.setHint("Instruction details");
        stepsList.add(recipeStepInput);

        // Add to instructions
        instructionRow.addView(recipeStepText);
        instructionRow.addView(recipeStepInput);
        instructionTable.addView(instructionRow);
    }
}
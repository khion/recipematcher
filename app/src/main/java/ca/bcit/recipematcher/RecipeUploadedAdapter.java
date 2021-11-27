package ca.bcit.recipematcher;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeUploadedAdapter extends ArrayAdapter<Recipe> {

    private Activity context;
    private List<Recipe> recipeList;
    private List<String> recipeIDs;
    private FirebaseFirestore db;
    private String userID;

    public RecipeUploadedAdapter(Activity context, List<Recipe> recipeList, List<String> recipeIDs, FirebaseFirestore db, String userID) {
        super(context, R.layout.list_search_layout, recipeList);
        this.context = context;
        this.recipeList = recipeList;
        this.recipeIDs = recipeIDs;
        this.db = db;
        this.userID = userID;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_uploads_layout, null, true);

        TextView tvRecipeName = listViewItem.findViewById(R.id.upload_recipe_name);
        TextView tvCategory = listViewItem.findViewById(R.id.upload_category_text);
        TextView tvRating = listViewItem.findViewById(R.id.upload_rating_txt);
        ImageView recipeImage = listViewItem.findViewById(R.id.upload_image_view);
        Button deleteButton = listViewItem.findViewById(R.id.button_delete_upload);

        Recipe recipe = recipeList.get(position);
        String recipeID = recipeIDs.get(position);
        tvRecipeName.setText(recipe.getRecipeName());
        tvCategory.setText("Category: " + recipe.getCategory());
        tvRating.setText("Rating: " + recipe.getRating());
        String imageURL = recipe.getImageURL();
        Picasso.get().load(imageURL).into(recipeImage);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("recipes").document(recipeID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listViewItem.setVisibility(View.INVISIBLE);
                        DocumentReference userDocRef = db.collection("users").document(userID);
                        userDocRef.update("recipes", FieldValue.arrayRemove(recipeID));
                        recipeList.remove(recipe);
                        recipeIDs.remove(recipeID);
                    }
                });
            }
        });

        return listViewItem;
    }

}

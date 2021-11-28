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
import java.util.Locale;

public class RecipeFavouritedAdapter extends ArrayAdapter<Recipe> {
    private Activity context;
    private List<Recipe> recipeList;
    private List<String> recipeIDs;
    private FirebaseFirestore db;
    private String userID;

    public RecipeFavouritedAdapter(Activity context, List<Recipe> recipeList, List<String> recipeIDs, FirebaseFirestore db, String userID) {
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

        View listViewItem = inflater.inflate(R.layout.list_favourites_layout, null, true);

        TextView tvRecipeName = listViewItem.findViewById(R.id.favourite_recipe_name);
        TextView tvCategory = listViewItem.findViewById(R.id.favourite_category_text);
        TextView tvRating = listViewItem.findViewById(R.id.favourite_rating_txt);
        ImageView recipeImage = listViewItem.findViewById(R.id.favourite_image_view);
        Button removeButton = listViewItem.findViewById(R.id.button_remove_favourite);

        Recipe recipe = recipeList.get(position);
        String recipeID = recipeIDs.get(position);
        tvRecipeName.setText(recipe.getRecipeName());
        tvCategory.setText("Category: " + recipe.getCategory());
        int ratingCount = recipe.getRatingCount();
        if (ratingCount == 0) {
            tvRating.setText(getContext().getResources().getString(R.string.no_ratings_text));
        } else {
            String ratingFormatted = String.format(Locale.CANADA, "%.1f", recipe.getRating() / ratingCount);
            tvRating.setText("Rating: " + ratingFormatted);
        }
        String imageURL = recipe.getImageURL();
        Picasso.get().load(imageURL).into(recipeImage);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewItem.setVisibility(View.INVISIBLE);
                DocumentReference userDocRef = db.collection("users").document(userID);
                userDocRef.update("favourites", FieldValue.arrayRemove(recipeID));
                recipeList.remove(recipe);
                recipeIDs.remove(recipeID);
            }
        });

        return listViewItem;
    }
}

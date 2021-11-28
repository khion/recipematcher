package ca.bcit.recipematcher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class RecipeSearchAdapter extends ArrayAdapter<Recipe> {
    private Activity context;
    private List<Recipe> recipeList;

    public RecipeSearchAdapter(Activity context, List<Recipe> recipeList) {
        super(context, R.layout.list_search_layout, recipeList);
        this.context = context;
        this.recipeList = recipeList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_search_layout, null, true);

        TextView tvRecipeName = listViewItem.findViewById(R.id.recipe_name);
        TextView tvCategory = listViewItem.findViewById(R.id.category_text);
        TextView tvRating = listViewItem.findViewById(R.id.rating_txt);
        ImageView recipeImage = listViewItem.findViewById(R.id.image_view);

        Recipe recipe = recipeList.get(position);
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

        return listViewItem;
    }


}

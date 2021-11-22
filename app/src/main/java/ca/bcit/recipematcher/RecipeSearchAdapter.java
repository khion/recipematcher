package ca.bcit.recipematcher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;

import java.util.List;

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

        Recipe recipe = recipeList.get(position);
        tvRecipeName.setText(recipe.getRecipeName());
        tvCategory.setText("Category: " + recipe.getCategory());
        tvRating.setText("Rating: " + recipe.getRating());

        return listViewItem;
    }


}

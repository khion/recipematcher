package ca.bcit.recipematcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RecipeDisplayFragment extends Fragment {

    private FirebaseFirestore db;
    private int recipeDocIdsIndex;


    public RecipeDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_display, container, false);

        db = FirebaseFirestore.getInstance();
        List<String> recipeDocIds = new ArrayList<>();

        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot recipeDocument : task.getResult()) {
                            recipeDocIds.add(recipeDocument.getId());
                        }
                        View v = getView();
                        Random random = new Random();
                        recipeDocIdsIndex = random.nextInt(recipeDocIds.size());
                        displayRecipeInfo(v, recipeDocIds.get(recipeDocIdsIndex));

                        view.setOnTouchListener(new onSwipeTouchListener(view.getContext()) {
                            String recipeDocId = "";

                            public void onSwipeTop() {
                                Intent i = new Intent(getActivity(), ViewRecipeActivity.class);
                                i.putExtra("RecipeID", recipeDocIds.get(recipeDocIdsIndex));
                                startActivity(i);
                            }

                            public void onSwipeRight() {
                                if (recipeDocIds.size() > recipeDocIdsIndex) {
                                    recipeDocIdsIndex++;
                                    if (recipeDocIdsIndex >= recipeDocIds.size()) {
                                        recipeDocIdsIndex = 0;
                                    }
                                    recipeDocId = recipeDocIds.get(recipeDocIdsIndex);
                                    displayRecipeInfo(v, recipeDocId);
                                }
                            }

                            public void onSwipeLeft() {
                                if (recipeDocIds.size() > recipeDocIdsIndex) {
                                    recipeDocIdsIndex--;
                                    if (recipeDocIdsIndex < 0) {
                                        recipeDocIdsIndex = recipeDocIds.size() - 1;
                                    }
                                    recipeDocId = recipeDocIds.get(recipeDocIdsIndex);
                                    displayRecipeInfo(v, recipeDocId);
                                }
                            }
                        });
                    }
                });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View v = getView();
        if (v != null) {
            TextView name = v.findViewById(R.id.recipe_name);
            name.setText("recipe name");
        }
    }

    private void displayRecipeInfo(View v, String recipeID) {
        TextView name = v.findViewById(R.id.recipe_name);
        ImageView image = v.findViewById(R.id.recipe_image);
        RatingBar ratingBar = v.findViewById(R.id.main_page_rating_bar);
        DocumentReference recipeDocRef = db.collection("recipes").document(recipeID);
        recipeDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Recipe r = documentSnapshot.toObject(Recipe.class);
                name.setText(r.getRecipeName());
                Picasso.get().load(r.getImageURL()).into(image);
                int ratingCount = r.getRatingCount();
                if (ratingCount == 0) {
                    ratingBar.setRating(0);
                } else {
                    double rating = r.getRating() / ratingCount;
                    ratingBar.setRating((float) rating);
                }
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), recipeID, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
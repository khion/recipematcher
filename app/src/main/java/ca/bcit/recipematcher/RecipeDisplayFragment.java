package ca.bcit.recipematcher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


public class RecipeDisplayFragment extends Fragment {

    FirebaseFirestore db;


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
                        for (QueryDocumentSnapshot recipeDocument: task.getResult()) {
                            recipeDocIds.add(recipeDocument.getId());
                        }
                        View v = getView();
                        displayRecipeInfo(v, recipeDocIds.get(0));

                        view.setOnTouchListener(new onSwipeTouchListener(view.getContext()) {
                            String recipeDocId = "";
                            int recipeDocIdsIndex = 0;
                            public void onSwipeTop() {
                                Toast.makeText(view.getContext(), "top", Toast.LENGTH_SHORT).show();
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
                            public void onSwipeBottom() {
                                Toast.makeText(view.getContext(), "bottom", Toast.LENGTH_SHORT).show();
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
        DocumentReference recipeDocRef = db.collection("recipes").document(recipeID);
        recipeDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Recipe r = documentSnapshot.toObject(Recipe.class);
                name.setText(r.getRecipeName());
                Picasso.get().load(r.getImageURL()).into(image);
            }
        });
    }
}
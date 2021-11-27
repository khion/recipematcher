package ca.bcit.recipematcher;

import android.os.Bundle;

import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class RecipeDisplayFragment extends Fragment {

    private Recipe currentlyShowing;

    public RecipeDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_display, container, false);

        assert getArguments() != null;
        currentlyShowing = getArguments().getParcelable("currentRecipe");

        view.setOnTouchListener(new onSwipeTouchListener(view.getContext()) {
//            public void onSwipeTop() {
//                Toast.makeText(view.getContext(), "top", Toast.LENGTH_SHORT).show();
//            }
            public void onSwipeRight() {
                Toast.makeText(view.getContext(), "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(view.getContext(), "left", Toast.LENGTH_SHORT).show();
            }
//            public void onSwipeBottom() {
//                Toast.makeText(view.getContext(), "bottom", Toast.LENGTH_SHORT).show();
//            }

        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View v = getView();
        if (v != null) {
            TextView name = v.findViewById(R.id.recipe_name);
            name.setText("test");
        }
    }
}
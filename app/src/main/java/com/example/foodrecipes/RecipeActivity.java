package com.example.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ScrollingView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodrecipes.adapters.RecipeViewHolder;
import com.example.foodrecipes.model.Ingredient;
import com.example.foodrecipes.model.Recipe;

import java.util.List;

public class RecipeActivity extends BaseActivity {

    //UI Components
    private ImageView mRecipeImage;
    private TextView mRecipeTitle;
    private LinearLayout mRecipeIngredientContainer;
    private ScrollingView mScrollingView;
    private String ingredientSummary = "";
    private static final String TAG = "RecipeActivity";
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        mRecipeImage = findViewById(R.id.recipe_image);
        mRecipeTitle = findViewById(R.id.recipe_title);
        mRecipeIngredientContainer = findViewById(R.id.ingredients_container);
        mScrollingView = findViewById(R.id.parent);
        getIncomingIntent();
        setRecipeProperies();

    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("recipe")) {
            recipe = getIntent().getParcelableExtra("recipe");
            Log.d(TAG, "getIncomingIntent: " + recipe.getLabel());

        }
    }

    private void setRecipeProperies() {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(recipe.getImage())
                .into(mRecipeImage);
        mRecipeTitle.setText(recipe.getLabel());

        for (String ingredient : recipe.getIngredientLines()) {
            if (ingredient != null) {
                ingredientSummary += ingredient + "\n";
            }

        }
        TextView textView = new TextView(this);
        textView.setText(ingredientSummary);
        textView.setTextSize(15);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRecipeIngredientContainer.addView(textView);
    }
}

package com.example.foodrecipes.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrecipes.R;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView title, source;
    AppCompatImageView image;
    OnRecipeListener mOnRecipeListener;

    public RecipeViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
        super(itemView);
        title = itemView.findViewById(R.id.recipe_title);
        source = itemView.findViewById(R.id.recipe_source);
        image = itemView.findViewById(R.id.recipe_image);
        itemView.setOnClickListener(this);
        mOnRecipeListener = onRecipeListener;
    }

    @Override
    public void onClick(View view) {
        mOnRecipeListener.onRecipeClick(getAdapterPosition());
    }
}

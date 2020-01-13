package com.example.foodrecipes.adapters;

import android.app.TimePickerDialog;
import android.net.Uri;
import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodrecipes.R;
import com.example.foodrecipes.model.Recipe;
import com.example.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;
    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int TIME_OUT_TYPE = 4;
    private static final int EXHAUSTED_TYPE = 5;


    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener) {
        this.mOnRecipeListener = mOnRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener);
            case LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new LoadingViewHolder(view);
            case TIME_OUT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timed_out_list_item, parent, false);
                return new RequestTimedOutViewHolder(view);
            case EXHAUSTED_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted, parent, false);
                return new SearchExhaustedViewHolder(view);
            case CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
                return new CategoryViewHolder(view, mOnRecipeListener);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item, parent, false);
                return new RecipeViewHolder(view, mOnRecipeListener);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        if (itemViewType == RECIPE_TYPE) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(mRecipes.get(position).getImage())
                    .into(((RecipeViewHolder) holder).image);
            ((RecipeViewHolder) holder).title.setText(mRecipes.get(position).getLabel());
            ((RecipeViewHolder) holder).source.setText(mRecipes.get(position).getSource());

        } else if (itemViewType == CATEGORY_TYPE) {
            Uri path = Uri.parse("android.resource://com.example.foodrecipes/drawable/" + mRecipes.get(position).getImage());
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);
            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(path)
                    .into(((CategoryViewHolder) holder).categoryImage);
            ((CategoryViewHolder) holder).categoryTitle.setText(mRecipes.get(position).getLabel());


        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mRecipes.get(position).getSource() == "-1") {
            return CATEGORY_TYPE;
        } else if (mRecipes.get(position).getLabel().equals("LOADING...")) {
            return LOADING_TYPE;
        } else if (mRecipes.get(position).getLabel().equals("TIMED OUT...")) {
            return TIME_OUT_TYPE;
        } else if (mRecipes.get(position).getLabel().equals("EXHAUSTED...")) {
            return EXHAUSTED_TYPE;
        } else if (position == mRecipes.size() - 1 && position != 0 && !mRecipes.get(position).getLabel().equals("EXHAUSTED...")) {
            return LOADING_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    public void displayLoading() {
        if (!isLoading()) {
            Recipe recipe = new Recipe();
            recipe.setLabel("LOADING...");
            List<Recipe> loadingList = new ArrayList<>();
            loadingList.add(recipe);
            mRecipes = loadingList;
            notifyDataSetChanged();

        }

    }

    public void setQueryTimedOut() {
        hideLoading();
        hideExhausted();
        Recipe timedOutRecipe = new Recipe();
        timedOutRecipe.setLabel("TIMED OUT...");
        mRecipes.add(timedOutRecipe);
        notifyDataSetChanged();
    }

    public void setQueryExhausted() {
        hideLoading();
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setLabel("EXHAUSTED...");
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    private void hideLoading() {
        if (isLoading()) {
            for (Recipe recipe : mRecipes) {
                if (recipe.getLabel().equals("LOADING...")) {
                    mRecipes.remove(recipe);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void hideExhausted() {
        if (isExhausted()) {
            for (Recipe recipe : mRecipes) {
                if (recipe.getLabel().equals("EXHAUSTED...")) {
                    mRecipes.remove(recipe);
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean isExhausted() {
        if (mRecipes != null) {
            if (mRecipes.size() > 0) {
                if (mRecipes.get(mRecipes.size() - 1).getLabel().equals("EXHAUSTED...")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLoading() {
        if (mRecipes != null) {
            if (mRecipes.size() > 0) {
                if (mRecipes.get(mRecipes.size() - 1).getLabel().equals("LOADING...")) {
                    return true;
                }
            }
        }
        return false;
    }


    public void displaySearchCategories() {
        List<Recipe> categories = new ArrayList<>();
        for (int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++) {
            Recipe recipe = new Recipe();
            recipe.setLabel(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSource("-1");
            categories.add(recipe);
        }
        mRecipes = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null) {
            return mRecipes.size();
        }
        return 0;
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position) {
        if (mRecipes != null) {
            if (mRecipes.size() > 0) {
                return mRecipes.get(position);
            }
        }
        return null;
    }
}

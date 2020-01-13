package com.example.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodrecipes.model.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private boolean mIsViewingRecipes;
    private boolean mIsPerformingQuery;
    private boolean mDidRetrieveRecipe;
    private boolean mIsTimedOut;


    private RecipeRepository mRecipeRepository;
    public RecipeListViewModel()
    {
        mRecipeRepository = RecipeRepository.getInstance();
        mIsPerformingQuery = false;
        mDidRetrieveRecipe = false;
        mIsTimedOut = false;
    }

    public LiveData<List<Recipe>> getRecipes()
    {
        return mRecipeRepository.getRecipes();
    }

    public LiveData<Boolean> isQueryExhausted()
    {
        return mRecipeRepository.isQueryExhausted();
    }

    public void searchRecipesApi(String query, int from, int to)
    {
        mIsViewingRecipes = true;
        mRecipeRepository.searchRecipesApi(query,from,to);
    }
    public void setRetrievedRecipe(boolean retrievedRecipe)
    {
        mDidRetrieveRecipe = retrievedRecipe;
    }
    public boolean didRetrieveRecipe()
    {
        return mDidRetrieveRecipe;
    }

    public void setIsRequestTimedOut(boolean isRequestTimedOut)
    {
        mIsTimedOut = isRequestTimedOut;
    }
    public boolean isRequestTimedOut()
    {
        return mIsTimedOut;
    }

    public boolean isIsViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean mIsViewingRecipes) {
        this.mIsViewingRecipes = mIsViewingRecipes;
    }

    public void searchNextPage()
    {
        if(!mIsPerformingQuery && mIsViewingRecipes && !isQueryExhausted().getValue())
        {
            mRecipeRepository.searchNextPage();
        }
    }



    public boolean isPerformingQuery() {
        return mIsPerformingQuery;
    }

    public void setIsPerformingQuery(boolean mIsPerformingQuery) {
        this.mIsPerformingQuery = mIsPerformingQuery;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut()
    {
        return mRecipeRepository.isRecipeRequestTimedOut();
    }

    public boolean onBackPressed()
    {

        if(mIsPerformingQuery)
        {
            // cancel the query
            mRecipeRepository.cancelRequest();
            mIsPerformingQuery = false;
        }
        else if(mIsViewingRecipes)
        {
            mIsViewingRecipes = false;
            return false;
        }
        return true;
    }
}

package com.example.foodrecipes.repositories;

import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.foodrecipes.model.Recipe;
import com.example.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private static final String TAG = "RecipeRepository";
    private String mQuery;
    private int mFrom;
    private int mTo;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes = new MediatorLiveData<>();


    public RecipeRepository() {
        mRecipeApiClient = RecipeApiClient.getInstance();
        initMediators();
    }
    public static RecipeRepository getInstance() {

        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    private void initMediators(){
        LiveData<List<Recipe>> recipeListApiSource = mRecipeApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if(recipes != null){
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                }
                else{
                    // search database cache
                    doneQuery(null);
                }
            }
        });
    }

    public LiveData<Boolean> isQueryExhausted()
    {

        return mIsQueryExhausted;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut()
    {
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }

    private void doneQuery (List<Recipe> list)
    {
        if(list!=null)
        {
            if((list.size()%9)!=0)
            {
                mIsQueryExhausted.setValue(true);
            }
        }
        else
        {
           // mIsQueryExhausted.setValue(true);
        }
    }



    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public void searchRecipesApi(String query, int from, int to)
    {
        mIsQueryExhausted.setValue(false);
        mQuery=query;
        mFrom=from;
        mTo = to;
        mRecipeApiClient.searchRecipesApi(query,from,to);

    }

    public void searchNextPage()
    {
        searchRecipesApi(mQuery, mFrom+10,mTo+10 );
    }

    public void cancelRequest()
    {
        mRecipeApiClient.cancelRequest();
    }
}

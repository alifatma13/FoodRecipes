package com.example.foodrecipes.requests;

import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.foodrecipes.AppExecutors;
import com.example.foodrecipes.model.Hit;
import com.example.foodrecipes.model.Recipe;
import com.example.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeApiClient {

    public static final String TAG = "RecipeApiClient";

    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;
    private MutableLiveData<Boolean> mRecipeRequestTimeOut = new MutableLiveData<>();

    public static RecipeApiClient getInstance() {
        if (instance == null) {
            instance = new RecipeApiClient();
        }
        return instance;
    }
    public LiveData<Boolean> isRecipeRequestTimedOut()
    {
        return mRecipeRequestTimeOut;
    }


    private RecipeApiClient() {
        this.mRecipes = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public void searchRecipesApi(String query, int from, int to) {
        if(mRetrieveRecipesRunnable!=null)
        {
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query,from,to);
        final Future handler = AppExecutors.getInstance().NetworkIO().submit(mRetrieveRecipesRunnable);

        AppExecutors.getInstance().NetworkIO().schedule(new Runnable() {
            @Override
            public void run() {
                //let the user know its timed out
                mRecipeRequestTimeOut.postValue(true);
                handler.cancel(true);
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);


    }

    private class RetrieveRecipesRunnable implements Runnable {

        private String query;
        private int from;
        private int to;
        boolean cancelRequest;
        List<Hit> hit = null;
        List<Recipe> recipes = new ArrayList<>();
        int recipe_count = 0;

        public RetrieveRecipesRunnable(String query, int from, int to) {
            this.query = query;
            this.from = from;
            this.to = to;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, from, to).execute();
                if (cancelRequest == true) {
                    return;
                }
                if (response.code() == 200) {
                    recipe_count = ((com.example.foodrecipes.model.Response) response.body()).getHits().size();
                    hit = ((com.example.foodrecipes.model.Response) response.body()).getHits();

                    for (int i = 0; i < recipe_count; i++) {
                        recipes.add(hit.get(i).getRecipe());
                       // Log.d("RecipeListActivity", "onResponse: " + recipes.get(i).getLabel());
                    }
                    if (from == Constants.FROM_RECIPE_NUMBER && to == Constants.TO_RECIPE_NUMBER) {
                        mRecipes.postValue(recipes);
                    } else {
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(recipes);
                        mRecipes.postValue(currentRecipes);
                    }

                } else {

                    String error = response.errorBody().string();
                    Log.d(TAG, "run: " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }


        }

        private Call<com.example.foodrecipes.model.Response> getRecipes(String query, int from, int to) {
            return ServiceGenerator.getRecipeApi().searchRecipe(Constants.APP_ID, Constants.API_KEY, query, from, to);
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: Cancelling the search Request");
            cancelRequest = true;
        }
    }

    public void cancelRequest()
    {
        if(mRetrieveRecipesRunnable!=null){
            mRetrieveRecipesRunnable.cancelRequest();
        }

    }
}

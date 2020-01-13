package com.example.foodrecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.example.foodrecipes.model.Recipe;


import com.example.foodrecipes.util.Constants;
import com.example.foodrecipes.util.Testing;
import com.example.foodrecipes.util.VerticalSpacingItemDecorator;
import com.example.foodrecipes.viewmodels.RecipeListViewModel;


import java.util.List;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private RecipeListViewModel mRecipeListViewModel;
    private static final String TAG = "RecipeListActivity";
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);


        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        initRecyclerView();
        subscribeObservers();
        initSearchView();

        if (!mRecipeListViewModel.isIsViewingRecipes()) {
            //display search categories
            displaySearchCategories();
        }

        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void subscribeObservers() {

        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {

                if (recipes != null) {
                    if (mRecipeListViewModel.isIsViewingRecipes()) {
                        Testing.printRecipes(recipes, "Recipes test");
                        mRecipeListViewModel.setIsPerformingQuery(false);
                        mRecipeListViewModel.setRetrievedRecipe(true);
                        mAdapter.setRecipes(recipes);

                    }
                }
            }
        });

        mRecipeListViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                 if(aBoolean )
                 {
                     Log.d(TAG, "onChanged: the Query is Exhausted...");
                     mAdapter.setQueryExhausted();
                 }

            }
        });

        mRecipeListViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && !mRecipeListViewModel.didRetrieveRecipe())
                {
                    Log.d(TAG, "onChanged: Timed out..");
                    mAdapter.setQueryTimedOut();
                    mRecipeListViewModel.setIsRequestTimedOut(true);
                }
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!mRecyclerView.canScrollVertically(1))
                {
                    //search the next page
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });
    }

    private void initSearchView() {
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String foodName) {
                if (foodName != null) {
                    mAdapter.displayLoading();
                    searchRecipesApi(foodName, Constants.FROM_RECIPE_NUMBER, Constants.TO_RECIPE_NUMBER);
                    mSearchView.clearFocus();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void searchRecipesApi(String query, int from, int to) {
        mRecipeListViewModel.searchRecipesApi(query, from, to);
    }


    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this,RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        mAdapter.displayLoading();
        searchRecipesApi(category, Constants.FROM_RECIPE_NUMBER, Constants.TO_RECIPE_NUMBER);
        mSearchView.clearFocus();
    }

    private void displaySearchCategories() {
        mRecipeListViewModel.setIsViewingRecipes(false);
        mAdapter.displaySearchCategories();

    }

    @Override
    public void onBackPressed() {

        if (mRecipeListViewModel.onBackPressed()) {
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_categories)
        {
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.foodrecipes.requests;



import com.example.foodrecipes.model.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {


@GET("search")
    Call<Response> searchRecipe(
            @Query("app_id") String app_id,
            @Query("app_key") String app_key,
            @Query("q") String search_query,
            @Query("from") int from,
            @Query("to") int to
);


}

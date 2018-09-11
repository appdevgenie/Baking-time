package com.appdevgenie.bakingtime.utils;

import com.appdevgenie.bakingtime.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingApi {

    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipe();
}

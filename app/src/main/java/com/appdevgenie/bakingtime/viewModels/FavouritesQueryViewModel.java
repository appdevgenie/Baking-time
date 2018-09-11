package com.appdevgenie.bakingtime.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.appdevgenie.bakingtime.database.RecipeDatabase;
import com.appdevgenie.bakingtime.model.Recipe;

import java.util.List;

public class FavouritesQueryViewModel extends ViewModel {

    private LiveData<List<Recipe>> queryFavourites;

    public FavouritesQueryViewModel(RecipeDatabase recipeDatabase, int recipeId) {

        queryFavourites = recipeDatabase.favouritesDao().loadFavouriteByRecipeId(recipeId);
    }

    public LiveData<List<Recipe>> checkFavourites(){
        return queryFavourites;
    }
}

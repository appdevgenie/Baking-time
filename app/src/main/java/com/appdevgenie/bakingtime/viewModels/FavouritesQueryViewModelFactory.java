package com.appdevgenie.bakingtime.viewModels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.appdevgenie.bakingtime.database.RecipeDatabase;

public class FavouritesQueryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private RecipeDatabase recipeDatabase;
    private int recipeId;

    public FavouritesQueryViewModelFactory(RecipeDatabase recipeDatabase, int recipeId) {
        this.recipeDatabase = recipeDatabase;
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        //noinspection unchecked
        return (T) new FavouritesQueryViewModel(recipeDatabase, recipeId);
    }
}

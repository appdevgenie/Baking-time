package com.appdevgenie.bakingtime.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.appdevgenie.bakingtime.database.RecipeDatabase;
import com.appdevgenie.bakingtime.model.Recipe;

import java.util.List;

public class FavouritesLoadViewModel extends AndroidViewModel {

    private LiveData<List<Recipe>> favs;

    public FavouritesLoadViewModel(Application application) {
        super(application);

        RecipeDatabase recipeDatabase = RecipeDatabase.getDbInstance(this.getApplication());
        favs = recipeDatabase.favouritesDao().loadFavourites();
    }

    public LiveData<List<Recipe>> getFavs(){
        return favs;
    }
}

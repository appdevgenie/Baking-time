package com.appdevgenie.bakingtime.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.appdevgenie.bakingtime.model.Recipe;

import java.util.List;

@Dao
public interface FavouritesDao {

    @Insert
    void insertFavourite(Recipe recipe);

    @Query("DELETE FROM recipe WHERE id = :recipeId")
    void deleteFav(int recipeId);

    @Query("SELECT * FROM recipe")
    LiveData<List<Recipe>> loadFavourites();

    @Query("SELECT * FROM recipe WHERE id = :recipeId")
    LiveData<List<Recipe>> loadFavouriteByRecipeId(int recipeId);

    @Query("SELECT * FROM recipe WHERE itemId = :id")
    LiveData<Recipe> loadFavouriteById(int id);

}

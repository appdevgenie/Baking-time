package com.appdevgenie.bakingtime.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.appdevgenie.bakingtime.model.Recipe;

@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
@TypeConverters(ListConverter.class)
public abstract class RecipeDatabase extends RoomDatabase {

    private static final String TAG = RecipeDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "recipe.db";
    private static final Object LOCK = new Object();
    private static RecipeDatabase dbInstance;

    public static RecipeDatabase getDbInstance(Context context){

        if(dbInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "getDbInstance: creating new database instance");
                dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                        RecipeDatabase.class, RecipeDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return dbInstance;
    }

    public abstract FavouritesDao favouritesDao();
}

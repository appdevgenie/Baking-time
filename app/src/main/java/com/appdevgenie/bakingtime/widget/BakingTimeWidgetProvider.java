package com.appdevgenie.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.activities.MainListActivity;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.database.ListConverter;
import com.appdevgenie.bakingtime.model.Ingredient;
import com.appdevgenie.bakingtime.utils.IngredientListStringBuilder;

import java.util.List;


public class BakingTimeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        Intent intent = new Intent(context, MainListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0);
        String ingredientsString = prefs.getString(Constants.SHARED_PREFS_INGREDIENTS, null);
        String recipeName = prefs.getString(Constants.SHARED_PREFS_NAME, context.getString(R.string.recipe));
        List<Ingredient> ingredientList = ListConverter.stringToIngredientList(ingredientsString);

        StringBuilder stringBuilder = IngredientListStringBuilder.formatListToString(ingredientList, context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_time_widget_provider);
        views.setTextViewText(R.id.tvWidgetRecipeName, recipeName);
        views.setTextViewText(R.id.tvWidgetIngredients, stringBuilder);
        views.setOnClickPendingIntent(R.id.rlWidgetLayout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


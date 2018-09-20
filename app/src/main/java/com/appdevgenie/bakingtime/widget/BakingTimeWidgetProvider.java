package com.appdevgenie.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.activities.MainListActivity;
import com.appdevgenie.bakingtime.constants.Constants;


public class BakingTimeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0);
        String recipeName = prefs.getString(Constants.SHARED_PREFS_NAME, context.getString(R.string.recipe));

        Intent serviceIntent = new Intent(context, WidgetRemoteViewsService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent intentOpen = new Intent(context, MainListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentOpen, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        views.setTextViewText(R.id.tvWidgetRecipeTitle, recipeName);
        views.setRemoteAdapter(R.id.widgetListView, serviceIntent);
        views.setEmptyView(R.id.widgetListView, R.id.widgetEmptyView);
        views.setPendingIntentTemplate(R.id.widgetListView, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


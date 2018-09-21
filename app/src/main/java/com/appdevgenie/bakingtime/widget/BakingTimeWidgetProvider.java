package com.appdevgenie.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.activities.MainListActivity;
import com.appdevgenie.bakingtime.constants.Constants;


public class BakingTimeWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CLICK = "com.appdevgenie.bakingtime.widget.ACTION_CLICK";
    public static final String EXTRA_STRING = "com.appdevgenie.bakingtime.widget.EXTRA_STRING";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0);
        String recipeName = prefs.getString(Constants.SHARED_PREFS_NAME, context.getString(R.string.recipe));
        String ingredientsString = prefs.getString(Constants.SHARED_PREFS_INGREDIENTS, null);

        Intent serviceIntent = new Intent(context, WidgetRemoteViewsService.class);
        serviceIntent.putExtra(EXTRA_STRING, ingredientsString);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent clickIntent = new Intent(context, BakingTimeWidgetProvider.class);
        clickIntent.setAction(ACTION_CLICK);
        clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        views.setTextViewText(R.id.tvWidgetRecipeTitle, recipeName);
        views.setRemoteAdapter(R.id.widgetListView, serviceIntent);
        views.setEmptyView(R.id.widgetListView, R.id.widgetEmptyView);
        views.setPendingIntentTemplate(R.id.widgetListView, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, BakingTimeWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.widgetListView);
        }

        if (ACTION_CLICK.equals(intent.getAction())) {
            Intent openIntent = new Intent(context, MainListActivity.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(openIntent);
        }

        super.onReceive(context, intent);
    }
}


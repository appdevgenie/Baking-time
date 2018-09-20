package com.appdevgenie.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.database.ListConverter;
import com.appdevgenie.bakingtime.model.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListRemoteViewsFactory(getApplicationContext());
    }

    class WidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private String ingredientsString;
        private List<Ingredient> ingredientList;

        WidgetListRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0);
            ingredientsString = prefs.getString(Constants.SHARED_PREFS_INGREDIENTS, null);
            ingredientList = ListConverter.stringToIngredientList(ingredientsString);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredientList == null) {
                return 0;
            }
            return ingredientList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);

            Ingredient ingredient = ingredientList.get(position);
            DecimalFormat decimalFormat = new DecimalFormat(context.getString(R.string.measure_decimal_format));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(context.getString(R.string.dot))
                    .append(" ")
                    .append(decimalFormat.format(Double.valueOf(ingredient.getQuantity())))
                    .append(" ")
                    .append(ingredient.getMeasure())
                    .append("\t\t")
                    .append(ingredient.getIngredient());

            views.setTextViewText(R.id.tvWidgetIngredientItem, stringBuilder);

            Intent intent = new Intent();
            views.setOnClickFillInIntent(R.id.tvWidgetIngredientItem, intent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}

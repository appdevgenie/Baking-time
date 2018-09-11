package com.appdevgenie.bakingtime.utils;

import android.content.Context;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.model.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

public class IngredientListStringBuilder {

    public static StringBuilder formatListToString(List<Ingredient> ingredients, Context context) {

        DecimalFormat decimalFormat = new DecimalFormat(context.getString(R.string.measure_decimal_format));

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {

            stringBuilder
                    .append(context.getString(R.string.dot))
                    .append(" ")
                    .append(decimalFormat.format(Double.valueOf(ingredients.get(i).getQuantity())))
                    .append(" ")
                    .append(ingredients.get(i).getMeasure())
                    .append("\t\t")
                    .append(ingredients.get(i).getIngredient());

            if (i != ingredients.size() - 1) {
                stringBuilder.append("\n");
            }
        }

        return stringBuilder;
    }
}

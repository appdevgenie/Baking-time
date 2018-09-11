package com.appdevgenie.bakingtime.fragments;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.activities.RecipeDetailsActivity;
import com.appdevgenie.bakingtime.adapters.RecipeStepsListAdapter;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.database.ListConverter;
import com.appdevgenie.bakingtime.model.Ingredient;
import com.appdevgenie.bakingtime.model.Recipe;
import com.appdevgenie.bakingtime.model.Step;
import com.appdevgenie.bakingtime.utils.IngredientListStringBuilder;
import com.appdevgenie.bakingtime.utils.SnackbarUtil;
import com.appdevgenie.bakingtime.widget.BakingTimeWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeInfoFragment extends Fragment {

    @BindView(R.id.tvIngredients)
    TextView tvIngredients;

    private View view;
    private Recipe recipe;
    private Context context;
    private List<Ingredient> ingredients;

    public RecipeInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipe_info, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        if(savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                recipe = bundle.getParcelable(Constants.PARSE_SELECTED_RECIPE);
            }
        }else{
            recipe = savedInstanceState.getParcelable(Constants.SAVED_SELECTED_RECIPE);
        }

        populateIngredients();

        CardView cvInfoIngredients = view.findViewById(R.id.cvInfoIngredients);
        cvInfoIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupWidget();
            }
        });

        return view;

    }

    private void populateIngredients() {

        ingredients = recipe.getIngredients();

        tvIngredients.setText(IngredientListStringBuilder.formatListToString(ingredients, context));

        List<Step> steps = recipe.getSteps();
        RecyclerView recyclerView = view.findViewById(R.id.rvDetailsRecipeSteps);
        RecipeStepsListAdapter recipeStepsListAdapter = new RecipeStepsListAdapter(context, steps, (RecipeDetailsActivity)context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recipeStepsListAdapter);
    }

    private void setupWidget() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle extras = new Bundle();
        int widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit();
        String recipeName = recipe.getName();
        prefs.putString(Constants.SHARED_PREFS_NAME, recipeName);
        String ingredientsString = ListConverter.ingredientListToString(ingredients);
        prefs.putString(Constants.SHARED_PREFS_INGREDIENTS, ingredientsString);
        prefs.apply();

        BakingTimeWidgetProvider.updateAppWidget(context, appWidgetManager, widgetId);

        String snackText = recipeName + " " + getString(R.string.added_to_widget);
        SnackbarUtil.snackBarBuilder(getActivity().findViewById(android.R.id.content), snackText).show();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.SAVED_SELECTED_RECIPE, recipe);
    }
}

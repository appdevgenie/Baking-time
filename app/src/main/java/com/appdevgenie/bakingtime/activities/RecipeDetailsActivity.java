package com.appdevgenie.bakingtime.activities;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.adapters.RecipeStepsListAdapter;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.database.RecipeDatabase;
import com.appdevgenie.bakingtime.executors.AppExecutors;
import com.appdevgenie.bakingtime.fragments.RecipeInfoFragment;
import com.appdevgenie.bakingtime.fragments.RecipeStepFragment;
import com.appdevgenie.bakingtime.model.Recipe;
import com.appdevgenie.bakingtime.model.Step;
import com.appdevgenie.bakingtime.utils.IngredientListStringBuilder;
import com.appdevgenie.bakingtime.utils.SnackbarUtil;
import com.appdevgenie.bakingtime.viewModels.FavouritesQueryViewModel;
import com.appdevgenie.bakingtime.viewModels.FavouritesQueryViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeStepsListAdapter.StepClickedListener {

    private boolean dualPane;
    private FragmentManager fragmentManager;
    private String recipeTitle;
    private boolean isFavourite = false;
    private Recipe recipe;
    private RecipeDatabase recipeDatabase;

    @BindView(R.id.detailsCoordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    /*@BindView(R.id.tvDetailsInfo)
    TextView tvDetailsInfo;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        ButterKnife.bind(this);

        Context context = getApplicationContext();

        recipeDatabase = RecipeDatabase.getDbInstance(context);

        FrameLayout frameLayout = findViewById(R.id.recipe_instructions_container);
        if (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE) {
            dualPane = true;
        }

        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dualPane) {
                    finish();
                } else {
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                    } else {
                        finish();
                    }
                }
            }
        });

        if (savedInstanceState == null) {

            Intent intent = getIntent();

            if (intent != null && intent.hasExtra(Constants.PARSE_SELECTED_RECIPE)) {

                recipe = intent.getParcelableExtra(Constants.PARSE_SELECTED_RECIPE);
                recipeTitle = recipe.getName();

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.PARSE_SELECTED_RECIPE, recipe);
                RecipeInfoFragment recipeInfoFragment = new RecipeInfoFragment();
                recipeInfoFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .add(R.id.recipe_details_container, recipeInfoFragment)
                        .commit();

                checkIfFavourite();
            }
        } else {
            recipeTitle = savedInstanceState.getString(Constants.SAVED_RECIPE_TITLE, getString(R.string.app_name));
            isFavourite = savedInstanceState.getBoolean(Constants.SAVED_IS_FAVOURITE);
            recipe = savedInstanceState.getParcelable(Constants.SAVED_SELECTED_RECIPE);
        }

        getSupportActionBar().setTitle(recipeTitle);

    }

    private void checkIfFavourite() {
        FavouritesQueryViewModelFactory favouritesQueryViewModelFactory = new FavouritesQueryViewModelFactory(recipeDatabase, recipe.getId());
        FavouritesQueryViewModel favouritesQueryViewModel = ViewModelProviders.of(this, favouritesQueryViewModelFactory).get(FavouritesQueryViewModel.class);
        favouritesQueryViewModel.checkFavourites().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> favourites) {

                if (favourites != null) {
                    isFavourite = favourites.size() != 0;
                }
            }
        });
    }

    @Override
    public void onStepClicked(List<Step> stepsList, int position) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PARSE_STEP_ID, position);
        bundle.putBoolean(Constants.PARSE_DUAL_PANE, dualPane);
        bundle.putParcelableArrayList(Constants.PARSE_ALL_STEPS, (ArrayList<? extends Parcelable>) stepsList);
        bundle.putString(Constants.PARSE_RECIPE_TITLE, recipeTitle);

        if (!dualPane) {
            //mobile
            Intent intent = new Intent(this, RecipeStepInfoActivity.class);
            intent.putExtra(Constants.BUNDLE_STEP_EXTRA, bundle);
            startActivity(intent);

            /*fragmentManager.beginTransaction()
                    .replace(R.id.recipe_details_container, recipeStepFragment)
                    .addToBackStack(Constants.BACK_STACK_STEP_FRAGMENT)
                    .commit();*/
        } else {
            //tablet
            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            recipeStepFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_instructions_container, recipeStepFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_update_favourite:
                onFavClicked();
                invalidateOptionsMenu();
                return true;

            case R.id.menu_share:
                shareRecipeIngredients();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (isFavourite) {
            menu.getItem(0).setIcon(R.drawable.ic_fav_on);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_fav_off);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void shareRecipeIngredients() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType(getString(R.string.intent_share_type));
        StringBuilder shareBodyText = IngredientListStringBuilder.formatListToString(recipe.getIngredients(), getApplicationContext());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, recipe.getName() + "\n\n" + shareBodyText.toString());
        CharSequence shareLabel = TextUtils.concat(getString(R.string.share), " ", recipe.getName(), " ", getString(R.string.ingredients));
        startActivity(Intent.createChooser(sharingIntent, shareLabel));
    }

    private void onFavClicked() {

        String recipeTitle = recipe.getName();

        final Recipe recipeSave = new Recipe(
                recipe.getId(),
                recipe.getName(),
                recipe.getIngredients(),
                recipe.getSteps(),
                recipe.getServings(),
                recipe.getImage());

        if (!isFavourite) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    recipeDatabase.favouritesDao().insertFavourite(recipeSave);
                }
            });

            isFavourite = true;
            SnackbarUtil.snackBarBuilder(coordinatorLayout, recipeTitle + " " + getString(R.string.added_to_favourites)).show();

        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    recipeDatabase.favouritesDao().deleteFav(recipeSave.getId());
                }
            });

            isFavourite = false;
            SnackbarUtil.snackBarBuilder(coordinatorLayout, recipeTitle + " " + getString(R.string.removed_from_favourites)).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.SAVED_RECIPE_TITLE, recipeTitle);
        outState.putBoolean(Constants.SAVED_IS_FAVOURITE, isFavourite);
        outState.putParcelable(Constants.SAVED_SELECTED_RECIPE, recipe);
    }
}

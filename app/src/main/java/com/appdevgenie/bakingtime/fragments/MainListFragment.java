package com.appdevgenie.bakingtime.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.activities.RecipeDetailsActivity;
import com.appdevgenie.bakingtime.adapters.MainRecipeListAdapter;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.model.Recipe;
import com.appdevgenie.bakingtime.utils.BakingApi;
import com.appdevgenie.bakingtime.utils.EspressoIdlingResourceUtility;
import com.appdevgenie.bakingtime.utils.SnackbarUtil;
import com.appdevgenie.bakingtime.viewModels.FavouritesLoadViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainListFragment extends Fragment implements MainRecipeListAdapter.RecipeClickedListener {

    @BindView(R.id.rvMainRecipeList)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvProgress)
    TextView tvProgress;

    private MainRecipeListAdapter mainRecipeListAdapter;
    private Context context;

    public MainListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_recyclerview_list, container, false);
        ButterKnife.bind(this, view);
        setupVariables();

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                String selectedDb = bundle.getString(Constants.SELECT_DB, Constants.NETWORK_DB);
                if (TextUtils.equals(selectedDb, Constants.NETWORK_DB)) {
                    populateNetworkList();
                }
                if (TextUtils.equals(selectedDb, Constants.FAVOURITE_DB)) {
                    populateFavouritesList();
                }
            }
        }

        return view;
    }

    private void setupVariables() {

        context = getContext();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, calculateSpan());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        mainRecipeListAdapter = new MainRecipeListAdapter(context, this);
        recyclerView.setAdapter(mainRecipeListAdapter);

    }

    private int calculateSpan() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 260);
    }

    private void populateNetworkList() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BakingApi api = retrofit.create(BakingApi.class);
        Call<ArrayList<Recipe>> call = api.getRecipe();

        progressBar.setVisibility(View.VISIBLE);
        tvProgress.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                progressBar.setVisibility(View.GONE);
                tvProgress.setVisibility(View.GONE);
                ArrayList<Recipe> recipes = response.body();
                mainRecipeListAdapter.setMainAdapterData(recipes);
                EspressoIdlingResourceUtility.decrement();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {

                progressBar.setVisibility(View.GONE);
                tvProgress.setVisibility(View.GONE);
                SnackbarUtil.snackBarBuilder(getActivity().findViewById(android.R.id.content), getString(R.string.error_loading_recipes)).show();
                EspressoIdlingResourceUtility.decrement();
            }

        });
    }

    private void populateFavouritesList() {

        FavouritesLoadViewModel favouritesLoadViewModel = ViewModelProviders.of(this).get(FavouritesLoadViewModel.class);
        favouritesLoadViewModel.getFavs().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> favourites) {
                mainRecipeListAdapter.setMainAdapterData(favourites);
            }
        });
    }

    @Override
    public void onRecipeClicked(Recipe selectedRecipe) {

        Intent intent = new Intent(context, RecipeDetailsActivity.class);
        intent.putExtra(Constants.PARSE_SELECTED_RECIPE, selectedRecipe);
        startActivity(intent);
    }


}

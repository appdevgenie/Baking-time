package com.appdevgenie.bakingtime.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.fragments.RecipeStepFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepInfoActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvStepDetailNumber)
    TextView tvStepNumber;
    @BindView(R.id.ibStepDetailNext)
    ImageButton ibNextStep;
    @BindView(R.id.ibStepDetailPrevious)
    ImageButton ibPreviousStep;

    private Context context;
    private int stepID;
    private boolean dualPane;
    private ArrayList stepsArrayList;
    private String recipeTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_info);
        ButterKnife.bind(this);

        setupVariables();

        toolbar = findViewById(R.id.stepToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra(Constants.BUNDLE_STEP_EXTRA);

            if (bundle != null) {
                stepID = bundle.getInt(Constants.PARSE_STEP_ID);
                dualPane = bundle.getBoolean(Constants.PARSE_DUAL_PANE);
                stepsArrayList = bundle.getParcelableArrayList(Constants.PARSE_ALL_STEPS);
                recipeTitle = bundle.getString(Constants.PARSE_RECIPE_TITLE, getString(R.string.step));
                populateStep();
            }
        }else{
            stepID = savedInstanceState.getInt(Constants.PARSE_STEP_ID);
            stepsArrayList = savedInstanceState.getParcelableArrayList(Constants.PARSE_ALL_STEPS);
            recipeTitle = savedInstanceState.getString(Constants.SAVED_RECIPE_TITLE, getString(R.string.step));
            setupStepInfo();
        }
        getSupportActionBar().setTitle(recipeTitle);
    }

    private void setupVariables() {

        context = getApplicationContext();

        ibNextStep.setOnClickListener(this);
        ibPreviousStep.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibStepDetailPrevious:
                stepID--;
                populateStep();
                break;

            case R.id.ibStepDetailNext:
                stepID++;
                populateStep();
                break;
        }
    }

    private void populateStep() {
        setupStepInfo();

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PARSE_STEP_ID, stepID);
        bundle.putBoolean(Constants.PARSE_DUAL_PANE, dualPane);
        bundle.putParcelableArrayList(Constants.PARSE_ALL_STEPS, (ArrayList<? extends Parcelable>) stepsArrayList);

        loadFragment(bundle);
    }

    private void setupStepInfo() {
        if (stepID == 0) {
            ibPreviousStep.setVisibility(View.INVISIBLE);
            tvStepNumber.setText(R.string.introduction);
        } else if (stepID == stepsArrayList.size() - 1) {
            ibNextStep.setVisibility(View.INVISIBLE);
        } else {
            ibPreviousStep.setVisibility(View.VISIBLE);
            ibNextStep.setVisibility(View.VISIBLE);
            tvStepNumber.setText(TextUtils.concat(getString(R.string.step), " ", String.valueOf(stepID)));
        }
    }

    private void loadFragment(Bundle bundle) {

        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.recipe_step_container, recipeStepFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.step_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.PARSE_STEP_ID, stepID);
        outState.putParcelableArrayList(Constants.PARSE_ALL_STEPS, (ArrayList<? extends Parcelable>) stepsArrayList);
        outState.putString(Constants.SAVED_RECIPE_TITLE, getSupportActionBar().getTitle().toString());
    }
}

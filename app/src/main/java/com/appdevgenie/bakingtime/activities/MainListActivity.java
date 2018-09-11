package com.appdevgenie.bakingtime.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.appdevgenie.bakingtime.R;
import com.appdevgenie.bakingtime.constants.Constants;
import com.appdevgenie.bakingtime.fragments.MainListFragment;
import com.appdevgenie.bakingtime.utils.EspressoIdlingResourceUtility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainListActivity extends AppCompatActivity {

    private String selectedDb;
    @BindView(R.id.tvMainInfo)
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            selectedDb = savedInstanceState.getString(Constants.SELECT_DB);
        } else {
            selectedDb = Constants.NETWORK_DB;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parseData(selectedDb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_refresh:
                selectedDb = Constants.NETWORK_DB;
                parseData(selectedDb);
                return true;

            case R.id.menu_select_network:
                selectedDb = Constants.NETWORK_DB;
                parseData(selectedDb);
                return true;

            case R.id.menu_select_favorites:
                selectedDb = Constants.FAVOURITE_DB;
                parseData(selectedDb);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (TextUtils.equals(selectedDb, Constants.NETWORK_DB) && isNetworkConnected(this)) {
            menu.findItem(R.id.menu_refresh).setVisible(true);
        } else if (TextUtils.equals(selectedDb, Constants.NETWORK_DB) && !isNetworkConnected(this)) {
            menu.findItem(R.id.menu_refresh).setVisible(false);
        } else {
            menu.findItem(R.id.menu_refresh).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void parseData(String selDb) {

        invalidateOptionsMenu();

        if (TextUtils.equals(selDb, Constants.NETWORK_DB)) {
            EspressoIdlingResourceUtility.getIdlingResource();
            EspressoIdlingResourceUtility.increment();
            tvInfo.setText(R.string.network_recipes);
        }else{
            tvInfo.setText(R.string.favourite_recipes);
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SELECT_DB, selDb);
        MainListFragment mainListFragment = new MainListFragment();
        mainListFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_list_container, mainListFragment)
                .commit();
    }

    private boolean isNetworkConnected(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.SELECT_DB, selectedDb);
    }
}

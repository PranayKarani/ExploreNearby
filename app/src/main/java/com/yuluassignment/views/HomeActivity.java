package com.yuluassignment.views;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.yuluassignment.R;
import com.yuluassignment.databinding.ActivityHomeBinding;
import com.yuluassignment.misc.Settings;
import com.yuluassignment.misc.SharedPrefs;
import com.yuluassignment.viewmodels.PlacesViewModel;

public class HomeActivity extends AppCompatActivity {

    final String sp_offline_only  = "offline";
    final String sp_open_map_view = "mapView";

    private ActivityHomeBinding b;
    private PlacesViewModel     viewModel;

    private PlacesListFragment listFragment;
    private MapViewFragment    mapViewFragment;

    private boolean showingMapView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateStatusBar(android.R.color.white);
        b = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setSupportActionBar(b.toolbar);
        b.toolbar.setTitle(null);
        getSupportActionBar().setTitle("Search a place");
        loadSettings();

        viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);

        listFragment = new PlacesListFragment();
        mapViewFragment = new MapViewFragment();

        if (Settings.mapView) {
            showMap();
        } else {
            showList();
        }

    }

    public void toggleView(View view) {

        if (showingMapView) {
            showList();
        } else {
            showMap();
        }

    }

    private void showMap() {
        showingMapView = true;
        showFragment(mapViewFragment);
        b.viewToggleBtn.setImageDrawable(getResources().getDrawable(R.drawable.list_icon));
    }

    private void showList() {
        showingMapView = false;
        showFragment(listFragment);
        b.viewToggleBtn.setImageDrawable(getResources().getDrawable(R.drawable.map_icon));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem   mSearch     = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("what do you want to search for?");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard(b.getRoot());
                viewModel.getPlacesFor(query);
                b.viewToggleBtn.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menu.findItem(R.id.offline).setChecked(Settings.offline);
        menu.findItem(R.id.map_view).setChecked(Settings.mapView);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_data:
                viewModel.clearData();
                return true;
            case R.id.offline:
                Settings.offline = !Settings.offline;
                item.setChecked(Settings.offline);
                SharedPrefs.writeData(sp_offline_only, Settings.offline);
                return true;
            case R.id.map_view:
                Settings.mapView = !Settings.mapView;
                item.setChecked(Settings.mapView);
                SharedPrefs.writeData(sp_open_map_view, Settings.mapView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void loadSettings() {
        Settings.offline = SharedPrefs.readData(sp_offline_only, true);
        Settings.mapView = SharedPrefs.readData(sp_open_map_view, false);
    }

    private void updateStatusBar(int color) {

        getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

}

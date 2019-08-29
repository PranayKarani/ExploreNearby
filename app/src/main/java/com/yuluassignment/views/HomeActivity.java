package com.yuluassignment.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.Task;
import com.yuluassignment.C;
import com.yuluassignment.R;
import com.yuluassignment.databinding.ActivityHomeBinding;
import com.yuluassignment.entities.SimpleLocation;
import com.yuluassignment.misc.Prefs;
import com.yuluassignment.misc.SharedPrefs;
import com.yuluassignment.viewmodels.PlacesViewModel;

import java.util.HashSet;
import java.util.Set;

import static com.yuluassignment.C.*;

public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 12;
    private static final int REQUEST_CHECK_SETTINGS          = 43;

    private ActivityHomeBinding b;
    private PlacesViewModel     viewModel;

    private Set<SearchCloseListener>   searchCloseListeners;

    static boolean showingMapView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateStatusBar(android.R.color.white);

        searchCloseListeners = new HashSet<>();
        loadSettings();

        b = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setSupportActionBar(b.toolbar);
        try {
            getSupportActionBar().setTitle("Explore nearby");
        } catch (NullPointerException e) {
            b.toolbar.setTitle(null);
        }

        viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);

        PlacesListFragment listFragment    = new PlacesListFragment();
        MapViewFragment    mapViewFragment = new MapViewFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container, listFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.map_fragment_container, mapViewFragment).commit();

        searchCloseListeners.add(listFragment);
        searchCloseListeners.add(mapViewFragment);

        if (Prefs.mapView) {
            showMap();
        } else {
            showList();
        }

        // check this if no location found
        checkForLocationPersmission();

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
        b.listFragmentContainer.setVisibility(View.GONE);
        b.mapFragmentContainer.setVisibility(View.VISIBLE);
        b.viewToggleBtn.setImageDrawable(getResources().getDrawable(R.drawable.list_icon));
    }

    private void showList() {
        showingMapView = false;
        b.listFragmentContainer.setVisibility(View.VISIBLE);
        b.mapFragmentContainer.setVisibility(View.GONE);
        b.viewToggleBtn.setImageDrawable(getResources().getDrawable(R.drawable.map_icon));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                for (SearchCloseListener sl : searchCloseListeners) {
                    sl.onSearchClose();
                }
                return true;
            }
        });
        SearchView sv = (SearchView) searchItem.getActionView();
        sv.setQueryHint("");
        sv.findViewById(androidx.appcompat.R.id.search_plate).setBackgroundColor(Color.TRANSPARENT);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard(b.getRoot());
                viewModel.getPlacesFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menu.findItem(R.id.offline).setChecked(Prefs.offline);
        menu.findItem(R.id.map_view).setChecked(Prefs.mapView);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_data:
                viewModel.clearData();
                return true;
            case R.id.offline:
                Prefs.offline = !Prefs.offline;
                item.setChecked(Prefs.offline);
                SharedPrefs.put(C.sp_offline_only, Prefs.offline);
                return true;
            case R.id.map_view:
                Prefs.mapView = !Prefs.mapView;
                item.setChecked(Prefs.mapView);
                SharedPrefs.put(sp_open_map_view, Prefs.mapView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadSettings() {
        Prefs.offline = SharedPrefs.get(sp_offline_only, false);
        Prefs.mapView = SharedPrefs.get(sp_open_map_view, false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                showAlert(false, "Location permission is required to detect nearby places.", (dialog, which) -> {

                    checkForLocationPersmission();

                });

            } else {
                checkForLocationSettings();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getUserLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Will show results in Bengaluru", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void checkForLocationPersmission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        } else {

            checkForLocationSettings();

        }

    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * Checks for location setting. If on, call getUserLocation else shows a turn on location dialog
     */
    private void checkForLocationSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(getLocationRequest());
        Task<LocationSettingsResponse>  task    = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            getUserLocation();
        });
        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException ignore) {
                }
            }
        });

    }

    /**
     * Gets user location and informs listeners.
     * If location could not be accessed get the last saved location
     */
    private void getUserLocation() {

        final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(location -> {

            if (location != null) {
                setSimpleLocation(SimpleLocation.fromLocation(location));
            } else {
                client.requestLocationUpdates(getLocationRequest(), new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            setSimpleLocation(SimpleLocation.fromLocation(locationResult.getLastLocation()));
                        } else {

                            SimpleLocation lastLocation = checkForLastSavedLocation();
                            if (lastLocation != null) {
                                setSimpleLocation(lastLocation);
                            }

                        }
                    }
                }, null);

            }

        }).addOnFailureListener(e -> {
            Log.e(C.TAG, e.getMessage());
        });
    }

    private SimpleLocation checkForLastSavedLocation() {

        float lat = SharedPrefs.get(sp_last_lat);
        float lng = SharedPrefs.get(sp_last_long);

        if (lat != -1 && lng != -1) {
            return new SimpleLocation(lat, lng);
        } else {
            return null;
        }

    }

    // saves location and set viewmodel data
    private void setSimpleLocation(SimpleLocation location) {
        SharedPrefs.put(sp_last_lat, location.lat);
        SharedPrefs.put(sp_last_long, location.lng);
        viewModel.setLocation(location);
    }

    void showToggleButton(boolean show) {
        b.viewToggleBtn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void showAlert(final boolean cancelable, final String message, final DialogInterface.OnClickListener clickListener) {

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", clickListener)
                .setCancelable(cancelable)
                .create()
                .show();

    }

    public interface SearchCloseListener {
        void onSearchClose();
    }



}

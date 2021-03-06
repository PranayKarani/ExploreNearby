package com.yuluassignment.views;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yuluassignment.C;
import com.yuluassignment.R;
import com.yuluassignment.entities.Place;
import com.yuluassignment.entities.SimpleLocation;
import com.yuluassignment.viewmodels.PlacesViewModel;

import java.text.DecimalFormat;
import java.util.List;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, HomeActivity.SearchCloseListener {

    private GoogleMap          map;
    private PlacesViewModel    viewModel;
    private SupportMapFragment mapFragment;
    private SimpleLocation     location;
    private BitmapDescriptor   userPinIcon, placePinIcon;
    private Handler handler = new Handler();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, null);

        userPinIcon = getMarkerIconFromDrawable(R.drawable.user_pin);
        placePinIcon = getMarkerIconFromDrawable(R.drawable.place_pin);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // observe search for places and mark them on map upon receiving them
        viewModel.getPlacesData().observe(this, places -> {

            markPlaces(places);
            if (!HomeActivity.showingMapView) {
                return;
            }
            if (places.isEmpty()) {
                Toast.makeText(getContext(), "No places found :(", Toast.LENGTH_SHORT).show();
            }

        });

        // observe location data and locate on map upon getting it
        viewModel.getLocationData().observe(this, simpleLocation -> {
            location = simpleLocation;
            locateUser(simpleLocation);
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final DecimalFormat decimalFormat = new DecimalFormat("#.### km");
        Log.i(C.TAG, "map ready");
        map = googleMap;
        map.setOnMarkerClickListener(marker -> {

            // show place details dialog upon selecting marker, and also hide activitie's toggle view button
            Place place = (Place) marker.getTag();
            if (place != null) {
                ((HomeActivity) getActivity()).showToggleButton(false);
                String details = "Name: " + place.name + "\n\n" +
                        "Category: " + place.categoryName + "\n\n" +
                        "Full Address: " + place.fullAddress + "\n\n" +
                        "Distance: " + decimalFormat.format(place.distance / 1000);
                handler.postDelayed(() -> ((HomeActivity) getActivity()).showAlert(true, details, (dialog, which) -> dialog.dismiss()), 500);
            }
            return false;
        });
        // show toggle button if select somewhere on the map
        map.setOnMapClickListener(latLng -> {
            ((HomeActivity) getActivity()).showToggleButton(true);
        });
        locateUser(location);
    }

    @Override
    public void onSearchClose() {
        cleanupMap();
    }

    /**
     * adds a marker and moves camera to given location
     *
     * @param location
     */
    private void locateUser(SimpleLocation location) {

        if (location != null) {
            LatLng ll = new LatLng(location.lat, location.lng);
            map.addMarker(new MarkerOptions().position(ll).icon(userPinIcon));
            map.moveCamera(CameraUpdateFactory.newLatLng(ll));
        }
    }

    /**
     * Add markers on map for places
     *
     * @param places
     */
    private void markPlaces(List<Place> places) {

        if (map != null) {
            cleanupMap();

            // if location is not set i.e. camera might be looking somewhere else
            // move it to point at the first place
            if (location == null && !places.isEmpty()) {
                moveCameraOnPlace(places.get(0));
            }

            for (Place place : places) {

                LatLng ll = new LatLng(place.lat, place.lng);
                map.addMarker(new MarkerOptions().position(ll).icon(placePinIcon)).setTag(place);

            }

        }

    }

    private void moveCameraOnPlace(Place place) {
        LatLng ll = new LatLng(place.lat, place.lng);
        map.moveCamera(CameraUpdateFactory.newLatLng(ll));
    }

    /**
     * removes all markers except user location
     */
    private void cleanupMap() {
        if (map != null) {
            map.clear();
            locateUser(location);
        }
        ((HomeActivity) getActivity()).showToggleButton(true);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(int drawableId) {

        Drawable drawable = getResources().getDrawable(drawableId);

        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}

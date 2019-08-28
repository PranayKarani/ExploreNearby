package com.yuluassignment.views;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback, HomeActivity.LocationFetchListener, HomeActivity.SearchCloseListener {

    private GoogleMap          mMap;
    private PlacesViewModel    viewModel;
    private SupportMapFragment mapFragment;
    private SimpleLocation     location;

    public MapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        viewModel.getPlacesData().observe(this, places -> {

            if (places.isEmpty()) {
                Toast.makeText(getContext(), "No places found :(", Toast.LENGTH_SHORT).show();
            } else {
                markPlaces(places);
            }

        });
        if (location != null) {
            if (mMap == null) {
                locationFetched(location);
            } else {
                locateUser(location);
            }
        }
    }

    private void markPlaces(List<Place> places) {

        if (mMap != null) {
            cleanupMap();

            for (Place place : places) {

                addMarker(place.lat, place.lng);

            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, null);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(C.TAG, "ready map");
        mMap = googleMap;
        locateUser(location);
    }

    private void locateUser(SimpleLocation location) {

        if (location != null) {
            LatLng           ll   = new LatLng(location.lat, location.lng);
            BitmapDescriptor icon = getMarkerIconFromDrawable(R.drawable.user_pin);
            mMap.addMarker(new MarkerOptions().position(ll).icon(icon));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        }
    }

    private void addMarker(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(ll));
    }

    @Override
    public void locationFetched(SimpleLocation location) {
        this.location = location;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(C.TAG, "stopped");
    }

    @Override
    public void onSearchClose() {
        cleanupMap();
    }

    private void cleanupMap() {
        if (mMap != null) {
            mMap.clear();
            locateUser(location);
        }
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

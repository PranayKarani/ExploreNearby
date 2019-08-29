package com.yuluassignment.views;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.yuluassignment.R;
import com.yuluassignment.adapters.PlaceListAdapter;
import com.yuluassignment.databinding.FragmentPlacesListBinding;
import com.yuluassignment.viewmodels.PlacesViewModel;

import java.text.DecimalFormat;

public class PlacesListFragment extends Fragment implements HomeActivity.SearchCloseListener {

    private FragmentPlacesListBinding b;
    private PlacesViewModel           viewModel;
    private PlaceListAdapter          adapter;

    public PlacesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);

        final DecimalFormat decimalFormat = new DecimalFormat("#.### km");

        // setup list adapter
        adapter = new PlaceListAdapter(getContext());
        // listen for place item selection
        adapter.setPlaceSelectionListener(place -> {

            String details =
                    "Name: " + place.name + "\n\n" +
                            "Category: " + place.categoryName + "\n\n" +
                            "Full Address: " + place.fullAddress + "\n\n" +
                            "Distance: " + decimalFormat.format(place.distance / 1000);
            ((HomeActivity) getActivity()).showAlert(true, details, (dialog, which) -> dialog.dismiss());

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_places_list, container, false);

        b.placeList.setAdapter(adapter);

        // observe places data and update adapter upon results
        viewModel.getPlacesData().observe(this, places -> {

            adapter.setPlaces(places);

            if (places.isEmpty()) {
                if (!HomeActivity.showingMapView) {
                    Toast.makeText(getContext(), "No places found :(", Toast.LENGTH_SHORT).show();
                }
                b.img.setVisibility(View.VISIBLE);
            } else {
                b.img.setVisibility(View.GONE);
            }

        });

        return b.getRoot();
    }

    @Override
    public void onSearchClose() {
        adapter.setPlaces(null);
        b.img.setVisibility(View.VISIBLE);
    }
}

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

public class PlacesListFragment extends Fragment {

    private PlacesViewModel  viewModel;
    private PlaceListAdapter adapter;
    public PlacesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        adapter = new PlaceListAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPlacesListBinding b = DataBindingUtil.inflate(inflater, R.layout.fragment_places_list, container, false);

        b.placeList.setAdapter(adapter);
        viewModel.getPlacesData().observe(this, places -> {

            if (places.isEmpty()) {
                Toast.makeText(getContext(), "No places found :(", Toast.LENGTH_SHORT).show();
            }
            adapter.setPlaces(places);

        });

        return b.getRoot();
    }

}

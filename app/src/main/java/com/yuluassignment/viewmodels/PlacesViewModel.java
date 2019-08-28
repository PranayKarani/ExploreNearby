package com.yuluassignment.viewmodels;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yuluassignment.C;
import com.yuluassignment.entities.Place;
import com.yuluassignment.repos.PlacesRepo;

import java.util.List;

public class PlacesViewModel extends ViewModel {

    private MutableLiveData<List<Place>> placesData = new MutableLiveData<>();

    public void getPlacesFor(String query) {

        PlacesRepo.get().getPlacesFor(query, places -> {

            placesData.postValue(places);
            for (Place place : places) {
                Log.i(C.TAG, place.toString());
            }

        });

    }

    public void clearData() {

        PlacesRepo.get().clearLocalData();

    }

    public LiveData<List<Place>> getPlacesData() {
        return placesData;
    }


}

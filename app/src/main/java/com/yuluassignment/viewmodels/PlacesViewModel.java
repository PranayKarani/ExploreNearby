package com.yuluassignment.viewmodels;

import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.yuluassignment.C;
import com.yuluassignment.MyApp;
import com.yuluassignment.entities.Place;
import com.yuluassignment.entities.SimpleLocation;
import com.yuluassignment.repos.PlacesRepo;

import java.util.List;

public class PlacesViewModel extends ViewModel {

    private MutableLiveData<List<Place>>    placesData   = new MutableLiveData<>();
    private MutableLiveData<SimpleLocation> locationData = new MutableLiveData<>();

    public void getPlacesFor(String query) {

        Toast.makeText(MyApp.get(), "Searching...", Toast.LENGTH_SHORT).show();
        PlacesRepo.get().getPlacesFor(query, locationData.getValue(), places -> {

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

    public void setLocation(SimpleLocation location) {
        this.locationData.setValue(location);
    }

    public LiveData<SimpleLocation> getLocationData() {
        return locationData;
    }

}

package com.yuluassignment.views;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.yuluassignment.C;
import com.yuluassignment.R;
import com.yuluassignment.entities.Place;
import com.yuluassignment.repos.PlacesRepo;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        PlacesRepo.get().getPlacesFor("coffee", places -> {

            for (Place place : places) {
                Log.i(C.TAG, place.toString());
            }

        });

    }

}

package com.yuluassignment.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.yuluassignment.R;
import com.yuluassignment.databinding.ActivityHomeBinding;
import com.yuluassignment.viewmodels.PlacesViewModel;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding b;
    private PlacesViewModel     viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlacesFor("flipkart");


    }

}

package com.yuluassignment.views;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.yuluassignment.R;
import com.yuluassignment.databinding.ActivityHomeBinding;
import com.yuluassignment.misc.Settings;
import com.yuluassignment.misc.SharedPrefs;
import com.yuluassignment.viewmodels.PlacesViewModel;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding b;
    public  PlacesViewModel     viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_home);

        loadSettings();

        viewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        viewModel.getPlacesFor("flipkart");

        PlacesListFragment listFragment = new PlacesListFragment();
        loadFragment(listFragment);

    }

    private void loadFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }

    private void loadSettings() {
        Settings.offline_only = SharedPrefs.readData("offline_only", false);
    }



}

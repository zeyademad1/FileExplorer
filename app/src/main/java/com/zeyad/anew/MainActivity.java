package com.zeyad.anew;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.zeyad.anew.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NavController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> binding.drawer.openDrawer(GravityCompat.START));
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navigationHost.getId());
        if (navHostFragment != null) {
            controller = navHostFragment.getNavController();
        }


        NavigationUI.setupWithNavController(binding.navigationView, controller);

        controller.addOnDestinationChangedListener((navController, navDestination, bundle)
                -> binding.toolbar.setTitle("File Explorer"));
    }
}
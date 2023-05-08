package ch.epfl.culturequest.ui.quiz;


import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.ActivityNavigationBinding;
import ch.epfl.culturequest.utils.AndroidUtils;

public class QuizActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Sets up the bottom navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        BottomNavigationView navView = binding.navView;
        NavigationUI.setupWithNavController(navView, navController);

        // Disables default grey tint on icons
        navView.setItemIconTintList(null);
    }

}

package ch.epfl.culturequest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.databinding.ActivityNavigationBinding;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.utils.AndroidUtils;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Authenticator.checkIfUserIsLoggedIn(this);

        // Create the notification channels on NavigationActivity creation
        PushNotification.createNotificationChannels(this);

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Sets up the bottom navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        BottomNavigationView navView = binding.navView;
        NavigationUI.setupWithNavController(navView, navController);
        // Listener used to ensure that the correct fragment is displayed even after an intent with redirection
        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_scan:
                    navController.navigate(R.id.navigation_scan);
                    break;
                case R.id.navigation_home:
                    navController.navigate(R.id.navigation_home);
                    break;
                case R.id.navigation_profile:
                    navController.navigate(R.id.navigation_profile);
                    break;
                case R.id.navigation_leaderboard:
                    navController.navigate(R.id.navigation_leaderboard);
                    break;
                case R.id.navigation_map:
                    navController.navigate(R.id.navigation_map);
                    break;
            }
            return true;
        });

        // Disables default grey tint on icons
        navView.setItemIconTintList(null);

        // allows to redirect to the correct fragment after an intent with redirection
        String redirect = getIntent().getStringExtra("redirect");
        if (Objects.equals(redirect, "profile")) {
            navController.navigate(R.id.navigation_profile);
        }
        if (Objects.equals(redirect, "home")) {
            navController.navigate(R.id.navigation_home);
        }
    }
}
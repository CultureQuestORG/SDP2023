package ch.epfl.culturequest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.databinding.ActivityNavigationBinding;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.utils.AndroidUtils;

public class NavigationActivity extends AppCompatActivity {

    private ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Disables default grey tint on icons
        navView.setItemIconTintList(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            TournamentManagerApi.handleTournaments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
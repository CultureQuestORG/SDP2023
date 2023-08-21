package ch.epfl.culturequest.ui.settings;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.ActivityNotificationSettingsBinding;
import ch.epfl.culturequest.service.SettingsService;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;


/**
 * Activity that allows the user to change his profile picture and username
 */
public class NotificationSettingsActivity extends AppCompatActivity {
    private Profile activeProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.removeStatusBar(getWindow());
        ActivityNotificationSettingsBinding binding = ActivityNotificationSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //appear from right
        getWindow().setEnterTransition(new Slide(Gravity.END));

        activeProfile = Profile.getActiveProfile();

        // if the user is not logged in, we can't display the settings so we finish the activity
        if (activeProfile == null) {
            finish();
            return;
        }

        SwitchMaterial notificationsAllSwitch = findViewById(R.id.setting0);
        SwitchMaterial notificationsNewSwitch = findViewById(R.id.setting1);
        SwitchMaterial notificationsTournamentSwitch = findViewById(R.id.setting2);
        SwitchMaterial notificationsLikeSwitch = findViewById(R.id.setting3);

        boolean notificationsNew = SettingsService.getSettings(this, "notificationsNew", true);
        boolean notificationsTournament = SettingsService.getSettings(this, "notificationsTournament", true);
        boolean notificationsLike = SettingsService.getSettings(this, "notificationsLike", true);
        boolean notificationsAll = notificationsLike || notificationsNew || notificationsTournament;

        notificationsAllSwitch.setChecked(notificationsAll);
        notificationsNewSwitch.setChecked(notificationsNew);
        notificationsTournamentSwitch.setChecked(notificationsTournament);
        notificationsLikeSwitch.setChecked(notificationsLike);

        notificationsAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                notificationsNewSwitch.setChecked(true);
                notificationsNewSwitch.setEnabled(true);
                notificationsTournamentSwitch.setChecked(true);
                notificationsTournamentSwitch.setEnabled(true);
                notificationsLikeSwitch.setChecked(true);
                notificationsLikeSwitch.setEnabled(true);
            } else {
                notificationsNewSwitch.setChecked(false);
                notificationsNewSwitch.setEnabled(false);
                notificationsTournamentSwitch.setChecked(false);
                notificationsTournamentSwitch.setEnabled(false);
                notificationsLikeSwitch.setChecked(false);
                notificationsLikeSwitch.setEnabled(false);
            }
        });

        notificationsNewSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsService.saveSettings(this, "notificationsNew", isChecked);
        });

        notificationsTournamentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsService.saveSettings(this, "notificationsTournament", isChecked);
        });

        notificationsLikeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsService.saveSettings(this, "notificationsLike", isChecked);
        });
    }

    /**
     * Returns to the profile fragment
     */
    public void goBack(View view) {
        super.onBackPressed();
    }
}
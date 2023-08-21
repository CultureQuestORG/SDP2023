package ch.epfl.culturequest.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.ActivitySecuritySettingsBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.CustomSnackbar;


/**
 * Activity that allows the user to change his profile picture and username
 */
public class SecuritySettingsActivity extends AppCompatActivity {
    private Profile activeProfile;

    private TextView username;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        AndroidUtils.removeStatusBar(getWindow());
        ActivitySecuritySettingsBinding binding = ActivitySecuritySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //appear from right
        getWindow().setEnterTransition(new Slide(Gravity.END));

        //handle cancel
        Button logoutButton = binding.logOut;
        logoutButton.setOnClickListener(v -> {
            onBackPressed();
        });

        activeProfile = Profile.getActiveProfile();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // if the user is not logged in, we can't display the settings so we finish the activity
        if (activeProfile == null) {
            finish();
            return;
        }

        // handle the update profile button
        Button updateProfileButton = binding.updateProfile;
        username = binding.username;
        username.setText(activeProfile.getEmail());
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                username.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProfileButton.setEnabled(!s.toString().equals(activeProfile.getEmail()) && !binding.password.getText().toString().equals("") && false);
                binding.password.setError("You can't change your email for now");
            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.password.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProfileButton.setEnabled(false);
                binding.password.setError("You can't change your password for now");
            }
        });

        updateProfileButton.setOnClickListener(v -> {
            user.updateEmail(username.getText().toString())
                    .addOnCompleteListener(task -> {
                        updateProfileButton.setEnabled(false);
                        if (task.isSuccessful()) {
                            activeProfile.setEmail(username.getText().toString());
                            CustomSnackbar.showCustomSnackbar("Profile updated", R.drawable.account, binding.getRoot(), (Void) -> null);
                        } else {
                            username.setError(task.getException().getMessage());
                        }
                    });
        });


        if(user.isAnonymous() || !user.getProviderData().get(1).getProviderId().equals("password")){
            username.setEnabled(false);
            username.setAlpha(0.5f);
            binding.password.setEnabled(false);
            binding.password.setAlpha(0.5f);
        }

    }

    /**
     * Returns to the profile fragment
     */
    public void goBack(View view) {
        super.onBackPressed();
    }


}
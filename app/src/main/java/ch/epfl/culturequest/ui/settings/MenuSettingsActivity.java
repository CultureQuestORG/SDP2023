package ch.epfl.culturequest.ui.settings;

import static ch.epfl.culturequest.utils.AndroidUtils.hasConnection;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.utils.AndroidUtils;
import ch.epfl.culturequest.utils.CustomSnackbar;

public class MenuSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_settings_activity);
        AndroidUtils.removeStatusBar(getWindow());

        findViewById(R.id.back_button).setOnClickListener((view) -> onBackPressed());
        ConstraintLayout accountView = findViewById(R.id.account_settings);
        accountView.setOnClickListener((view) -> {
            //Open account settings activity
            Intent intent = new Intent(this, UserSettingsActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        accountView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.focus, null));
            } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.transparent, null));
            }
            return false;
        });

        ConstraintLayout securityView = findViewById(R.id.security_setting);
        securityView.setOnClickListener((view) -> {
            //Open profile settings activity
            Intent intent = new Intent(this, SecuritySettingsActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        securityView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.focus, null));
            } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.transparent, null));
            }
            return false;
        });

        ConstraintLayout notificationView = findViewById(R.id.notification_settings);
        notificationView.setOnClickListener((view) -> {
            //Open profile settings activity
            Intent intent = new Intent(this, NotificationSettingsActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        notificationView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.focus, null));
            } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.transparent, null));
            }
            return false;
        });

        ConstraintLayout legalView = findViewById(R.id.legal_settings);
        legalView.setOnClickListener((view) -> {
            //Open profile settings activity
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://culturequestorg.github.io/SDP2023/"));
            startActivity(i);
        });
        legalView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.focus, null));
            } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                view.setBackgroundTintList(getResources().getColorStateList(R.color.transparent, null));
            }
            return false;
        });

        Button logoutButton = findViewById(R.id.log_out2);
        logoutButton.setOnClickListener(v -> {
            Context context = v.getContext();
            if (hasConnection(context)) Authenticator.signOut(this);
            else {
                View rootView = v.getRootView();
                CustomSnackbar.showCustomSnackbar("Cannot log out. You are not connected to the internet", R.drawable.unknown_error, rootView, (Void) -> null);
            }
        });

    }
}
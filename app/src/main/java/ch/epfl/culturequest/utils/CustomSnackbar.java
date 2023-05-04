package ch.epfl.culturequest.utils;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;

import ch.epfl.culturequest.R;

public class CustomSnackbar {

    public static TextView currentSnackbarText;
    public static void showCustomSnackbar(String message, int imageResourceName, View rootView) {

        // Inflate custom Snackbar layout
        LayoutInflater inflater = LayoutInflater.from(rootView.getContext());
        View customView = inflater.inflate(R.layout.custom_snackbar, null);

        // Set message and customize colors
        TextView snackbarText = customView.findViewById(R.id.snackbar_text);
        snackbarText.setText(message);
        currentSnackbarText = snackbarText;
        CardView snackbarContainer = customView.findViewById(R.id.custom_snackbar_container);

        // set the background color of the snackbar to #555555
        snackbarContainer.setCardBackgroundColor(Color.parseColor("#555555"));

        // load the image
        ImageView logo = customView.findViewById(R.id.snackbar_image);

        // Set the image logo as a png from the drawable folder
        logo.setImageResource(imageResourceName);

        // Animate the logo
        Animation rotateAnimation = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.smooth_rotation);
        logo.startAnimation(rotateAnimation);

        // Create and show the Snackbar
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 200); // Remove the default padding
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT); // Make the background transparent
        snackbarLayout.addView(customView, 0);

        // Custom enter and exit animations
        snackbarLayout.setAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.snackbar_enter));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                snackbarLayout.setAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.snackbar_exit));
                super.onDismissed(transientBottomBar, event);
            }
        });

        snackbar.show();
    }
}

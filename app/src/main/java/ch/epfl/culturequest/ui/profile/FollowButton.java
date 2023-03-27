package ch.epfl.culturequest.ui.profile;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.culturequest.R;

public final class FollowButton {
    private final RelativeLayout button;
    private final Context context;
    private final TextView text;

    public FollowButton(RelativeLayout button) {
        this.button = button;
        this.text = button.findViewById(R.id.profileFollowText);
        this.context = button.getContext();
    }

    public void setFollowed(boolean followed) {
            text.setText(!followed ? context.getResources().getString(R.string.followTextButton) : context.getResources().getString(R.string.unfollowTextButton));
            button.setBackgroundResource(!followed ? R.drawable.button_rounded_corner : R.drawable.button_rounded_corner_negative);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        button.setOnClickListener(listener);
    }
}

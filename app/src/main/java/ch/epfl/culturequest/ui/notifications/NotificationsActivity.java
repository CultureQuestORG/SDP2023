package ch.epfl.culturequest.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardFragment;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardRecycleViewAdapter;
import ch.epfl.culturequest.ui.leaderboard.LeaderboardViewModel;
import ch.epfl.culturequest.utils.AndroidUtils;

public class NotificationsActivity extends AppCompatActivity {

    private NotificationsViewModel notificationsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        AndroidUtils.removeStatusBar(getWindow());

        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        // define the RecyclerView's adapter
        RecyclerView recyclerView = findViewById(R.id.notifications_recycler_view);
        NotificationsRecycleViewAdapter globalAdapter = new NotificationsRecycleViewAdapter(notificationsViewModel);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(globalAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        notificationsViewModel.getNotificationTexts().observe(this, pushNotifications -> {
            if(pushNotifications.size() == 0) {
                findViewById(R.id.no_notification_text).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.no_notification_text).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Returns to the home fragment
     */
    public void goBack(View view) {
        super.onBackPressed();
    }
}
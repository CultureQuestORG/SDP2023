package ch.epfl.culturequest.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.CompetitionNotification;
import ch.epfl.culturequest.notifications.FollowNotification;
import ch.epfl.culturequest.notifications.LikeNotification;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.notifications.ScanNotification;
import ch.epfl.culturequest.notifications.SightseeingNotification;
import ch.epfl.culturequest.social.Profile;

public class NotificationsRecycleViewAdapter extends RecyclerView.Adapter<NotificationsRecycleViewAdapter.NotificationViewHolder> {

    private List<PushNotification> notificationTexts;
    private final String UID;

    public NotificationsRecycleViewAdapter(NotificationsViewModel notificationsViewModel) {
        notificationsViewModel.getNotificationTexts().observeForever(notificationTexts -> {
            this.notificationTexts = notificationTexts;
            System.out.println("Notifications: " + notificationTexts.stream().map(PushNotification::getText).collect(Collectors.toList()));
            notifyItemRangeChanged(0, getItemCount());
        });
        UID = Profile.getActiveProfile().getUid();
    }

    @NonNull
    @Override
    public NotificationsRecycleViewAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsRecycleViewAdapter.NotificationViewHolder holder, int position) {
        holder.getNotificationText().setText(notificationTexts.get(position).getText());
        setIconNotification(holder.getNotificationIcon(), notificationTexts.get(position).getChannelId());
        holder.getDeleteButton().setOnClickListener(view -> {
            Database.deleteNotification(UID, notificationTexts.get(position));
            notificationTexts.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return notificationTexts.size();
    }

    private void setIconNotification(ImageView icon, String channel) {
        switch (channel) {
            case ScanNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.scan_icon_unsel);
                break;
            case LikeNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.like_full);
                break;
            case FollowNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.profile_icon_unsel);
                break;
            case CompetitionNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.planner);
                break;
            case SightseeingNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.planner);
                break;
            default:
                icon.setImageResource(R.drawable.notification);
                break;
        }
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ImageView deleteButton;
        private final TextView notificationText;
        private final ImageView notificationIcon;

        public NotificationViewHolder(View parent) {
            super(parent);
            deleteButton = parent.findViewById(R.id.delete_button);
            notificationText = parent.findViewById(R.id.notification_text);
            notificationIcon = parent.findViewById(R.id.notification_icon_text);
        }

        public ImageView getDeleteButton() {
            return deleteButton;
        }

        public TextView getNotificationText() {
            return notificationText;
        }

        public ImageView getNotificationIcon() {
            return notificationIcon;
        }
    }
}

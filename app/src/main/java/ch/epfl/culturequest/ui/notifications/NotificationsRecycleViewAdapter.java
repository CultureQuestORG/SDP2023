package ch.epfl.culturequest.ui.notifications;

import android.app.PendingIntent;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ch.epfl.culturequest.NavigationActivity;
import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.notifications.TournamentNotification;
import ch.epfl.culturequest.notifications.FollowNotification;
import ch.epfl.culturequest.notifications.LikeNotification;
import ch.epfl.culturequest.notifications.PushNotification;
import ch.epfl.culturequest.notifications.ScanNotification;
import ch.epfl.culturequest.notifications.SightseeingNotification;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

public class NotificationsRecycleViewAdapter extends RecyclerView.Adapter<NotificationsRecycleViewAdapter.NotificationViewHolder> {

    private List<PushNotification> notificationTexts = List.of();

    public NotificationsRecycleViewAdapter(NotificationsViewModel notificationsViewModel) {
        notificationsViewModel.getNotificationTexts().observeForever(notificationTexts -> {
            this.notificationTexts = notificationTexts;
            notifyItemRangeChanged(0, getItemCount());
        });
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
        setIconNotification(holder.getNotificationIcon(), notificationTexts.get(position).getChannelId(), notificationTexts.get(position).getSenderId());

        holder.getDeleteButton().setOnClickListener(view -> {
            Database.deleteNotification(Profile.getActiveProfile().getUid(), notificationTexts.get(position));
            notificationTexts.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, getItemCount());
        });

        holder.itemView.setOnClickListener(view -> {
            System.out.println(notificationTexts.get(position).getChannelId());
            try {
                notificationTexts.get(position).selectPendingIntent(holder.itemView.getContext(), notificationTexts.get(position).getChannelId()).send();
            } catch (PendingIntent.CanceledException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationTexts.size();
    }

    private void setIconNotification(ImageView icon, String channel, String uid) {
        switch (channel) {
            case ScanNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.scan_icon_unsel);
                break;
            case LikeNotification.CHANNEL_ID:
                icon.setImageResource(R.drawable.like_full);
                break;
            case FollowNotification.CHANNEL_ID:
                Database.getProfile(uid).thenAccept(profile -> {
                    Picasso.get()
                            .load(profile.getProfilePicture())
                            .placeholder(R.drawable.profile_icon_unsel)
                            .into(icon);
                });
                break;
            case TournamentNotification.CHANNEL_ID:
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

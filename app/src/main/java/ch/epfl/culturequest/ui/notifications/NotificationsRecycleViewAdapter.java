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

import ch.epfl.culturequest.R;

public class NotificationsRecycleViewAdapter  extends RecyclerView.Adapter<NotificationsRecycleViewAdapter.NotificationViewHolder>{

    private List<String> notificationTexts;

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
        holder.getNotificationText().setText(notificationTexts.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationTexts.size();
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

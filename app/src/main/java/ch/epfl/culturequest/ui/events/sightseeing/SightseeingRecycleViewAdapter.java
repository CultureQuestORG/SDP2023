package ch.epfl.culturequest.ui.events.sightseeing;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.authentication.Authenticator;
import ch.epfl.culturequest.backend.map_collection.OTMLocation;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.SightseeingEvent;
import ch.epfl.culturequest.ui.events.EventsViewModel;
import ch.epfl.culturequest.ui.profile.DisplayUserProfileActivity;

public class SightseeingRecycleViewAdapter extends RecyclerView.Adapter<SightseeingRecycleViewAdapter.SightseeingViewHolder> {

    private List<SightseeingEvent> sightseeingEvents = List.of();

    public SightseeingRecycleViewAdapter(EventsViewModel eventsViewModel) {
        eventsViewModel.getSightseeingEvents().observeForever(sightseeingEvents -> {
            this.sightseeingEvents = sightseeingEvents;
            notifyItemRangeChanged(0, getItemCount());
        });
    }

    @NonNull
    @Override
    public SightseeingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sightseeing, parent, false);
        return new SightseeingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SightseeingViewHolder holder, int position) {
        holder.getSightseeingName().setText(sightseeingEvents.get(position).getOwner().getName() + "'s sightseeing");
        List<OTMLocation> locations = sightseeingEvents.get(position).getLocations();
        for (OTMLocation location : locations) {
            TextView locationView = new TextView(holder.getLocationsList().getContext());
            locationView.setText(location.getName());
            locationView.setTextColor(holder.itemView.getContext().getColor(R.color.black));
            locationView.setPadding(10, 0, 0, 0);
            holder.getLocationsList().addView(locationView);
        }

        List<Profile> participants = sightseeingEvents.get(position).getInvited();
        for (Profile participant : participants) {
            TextView participantView = new TextView(holder.getParticipantsList().getContext());
            participantView.setText(participant.getName());
            participantView.setTextColor(holder.itemView.getContext().getColor(R.color.black));
            participantView.setPadding(10, 0, 0, 0);
            participantView.setOnClickListener(view -> {
                Intent intent = new Intent(holder.itemView.getContext(), DisplayUserProfileActivity.class);
                intent.putExtra("uid", participant.getUid());
                holder.itemView.getContext().startActivity(intent);
            });
            holder.getParticipantsList().addView(participantView);
        }

        holder.sightseeingName.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), DisplayUserProfileActivity.class);
            intent.putExtra("uid", sightseeingEvents.get(position).getOwner().getUid());
            holder.itemView.getContext().startActivity(intent);
        });

        if(sightseeingEvents.get(position).getOwner().getUid().equals(Authenticator.getCurrentUser().getUid())) {
            holder.getDeleteButton().setVisibility(View.VISIBLE);
            holder.getDeleteButton().setOnClickListener(view -> {
                Database.deleteSightseeingEvent(Authenticator.getCurrentUser().getUid(), sightseeingEvents.get(position).getEventId()).whenComplete((aVoid, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        sightseeingEvents.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                });
            });
        }

    }

    @Override
    public int getItemCount() {
        return sightseeingEvents.size();
    }

    public static class SightseeingViewHolder extends RecyclerView.ViewHolder {

        private final TextView sightseeingName;
        private final LinearLayout locationsList;
        private final LinearLayout participantsList;
        private final ImageView deleteButton;

        public SightseeingViewHolder(@NonNull View itemView) {
            super(itemView);

            sightseeingName = itemView.findViewById(R.id.sightseeing_title);
            locationsList = itemView.findViewById(R.id.locations_list);
            participantsList = itemView.findViewById(R.id.invited_list);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public TextView getSightseeingName() {
            return sightseeingName;
        }

        public LinearLayout getLocationsList() {
            return locationsList;
        }

        public LinearLayout getParticipantsList() {
            return participantsList;
        }

        public ImageView getDeleteButton() {
            return deleteButton;
        }
    }
}

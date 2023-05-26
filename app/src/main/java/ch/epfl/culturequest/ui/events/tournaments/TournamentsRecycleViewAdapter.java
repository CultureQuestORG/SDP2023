package ch.epfl.culturequest.ui.events.tournaments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.ui.events.EventsViewModel;

public class TournamentsRecycleViewAdapter extends RecyclerView.Adapter<TournamentsRecycleViewAdapter.TournamentViewHolder> {

    private List<Tournament> tournamentsEvents = List.of();

    public TournamentsRecycleViewAdapter(EventsViewModel eventsViewModel) {
        eventsViewModel.getTournamentsEvents().observeForever(sightseeingEvents -> {
            this.tournamentsEvents = sightseeingEvents;
            notifyItemRangeChanged(0, getItemCount());
        });
    }

    @NonNull
    @Override
    public TournamentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tournament, parent, false);
        return new TournamentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentViewHolder holder, int position) {
        holder.getTournamentName().setText("Quiz of the week");
        holder.getTournamentStatus().setText("Status: In progress");

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), TournamentActivity.class);
            intent.putExtra("tournament_id", tournamentsEvents.get(position).getTournamentId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tournamentsEvents.size();
    }

    public static class TournamentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tournamentName;
        private final TextView tournamentStatus;

        public TournamentViewHolder(@NonNull View itemView) {
            super(itemView);

            tournamentName = itemView.findViewById(R.id.tournament_title);
            tournamentStatus = itemView.findViewById(R.id.tournament_outcome);
        }

        public TextView getTournamentName() {
            return tournamentName;
        }

        public TextView getTournamentStatus() {
            return tournamentStatus;
        }
    }
}

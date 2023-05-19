package ch.epfl.culturequest.ui.events.tournaments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.tournament.quiz.Quiz;
import ch.epfl.culturequest.ui.events.EventsViewModel;

public class QuizzesRecycleViewAdapter extends RecyclerView.Adapter<QuizzesRecycleViewAdapter.QuizzViewHolder> {

    private List<Quiz> quizzes;
    private final FragmentManager fragmentManager;

    public QuizzesRecycleViewAdapter(TournamentViewModel eventsViewModel, FragmentManager fragmentManager) {
        eventsViewModel.getQuizzes().observeForever(quizzes -> {
            this.quizzes = quizzes;
            notifyItemRangeChanged(0, getItemCount());
        });
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public QuizzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quizz, parent, false);
        return new QuizzViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizzViewHolder holder, int position) {
        holder.getQuizzTitle().setText(quizzes.get(position).getArtName());
        holder.getQuizzStatus().setText("Not started yet");
        setAvailable(false, holder);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    private void setAvailable(boolean available, QuizzViewHolder holder) {
        if (!available) {
            holder.quizzTitleMark.setAlpha(0.7f);
            holder.quizzTitle.setAlpha(0.7f);
            holder.quizzStatus.setAlpha(0.7f);
            holder.quizzNext.setAlpha(0.7f);
            holder.itemView.setOnClickListener(v -> {
                QuizzUnavailableDialog quizzUnavailableDialog = new QuizzUnavailableDialog();
                quizzUnavailableDialog.show(fragmentManager, "QuizzUnavailableDialog");
            });
        }
    }

    public static class QuizzViewHolder extends RecyclerView.ViewHolder {

        private final TextView quizzTitleMark;
        private final TextView quizzTitle;
        private final TextView quizzStatus;
        private final ImageView quizzNext;

        public QuizzViewHolder(@NonNull View itemView) {
            super(itemView);

            quizzTitleMark = itemView.findViewById(R.id.quizz_title_mark);
            quizzTitle = itemView.findViewById(R.id.quizz_title);
            quizzStatus = itemView.findViewById(R.id.quizz_status);
            quizzNext = itemView.findViewById(R.id.quizz_icon_next);
        }

        public TextView getQuizzTitleMark() {
            return quizzTitleMark;
        }

        public TextView getQuizzTitle() {
            return quizzTitle;
        }

        public TextView getQuizzStatus() {
            return quizzStatus;
        }

        public ImageView getQuizzNext() {
            return quizzNext;
        }
    }
}

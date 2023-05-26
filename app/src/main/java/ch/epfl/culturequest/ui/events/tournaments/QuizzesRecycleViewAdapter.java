package ch.epfl.culturequest.ui.events.tournaments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.ui.quiz.QuizActivity;

public class QuizzesRecycleViewAdapter extends RecyclerView.Adapter<QuizzesRecycleViewAdapter.QuizzViewHolder> {

    private Map<String, ArtQuiz> quizzes;
    private String tournament;
    private final FragmentManager fragmentManager;

    public QuizzesRecycleViewAdapter(TournamentViewModel eventsViewModel, FragmentManager fragmentManager) {
        eventsViewModel.getQuizzes().observeForever(quizzes -> {
            this.quizzes = quizzes;
            notifyItemRangeChanged(0, getItemCount());
        });
        eventsViewModel.getTournament().observeForever(tournament -> {
            this.tournament = tournament;
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
        holder.getQuizzTitle().setText(quizzes.keySet().toArray()[position].toString());

        Profile.getActiveProfile().retrievePosts().whenComplete((posts, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                boolean alreadyPosted = posts.stream().anyMatch(post -> post.getArtworkName().equals(quizzes.keySet().toArray()[position].toString()));
                if (!alreadyPosted ) {
                    setAvailable(false, holder, quizzes.keySet().toArray()[position].toString(), null);
                }
            }
        });

        Database.getScoreQuiz(tournament, quizzes.get(quizzes.keySet().toArray()[position].toString()).getArtName(), Profile.getActiveProfile().getUid()).whenComplete((score, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            } else {
                if (score != null) {
                    holder.getQuizzStatus().setText("Score: " + score);
                    setAvailable(false, holder, quizzes.keySet().toArray()[position].toString(), "You have already completed this quiz");
                } else {
                    holder.getQuizzStatus().setText("Not started yet");
                    setAvailable(true, holder, quizzes.keySet().toArray()[position].toString(), null);
                }
            }
        });

        holder.getQuizzStatus().setText("Not started yet");
        setAvailable(true, holder, quizzes.keySet().toArray()[position].toString(), null);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    private void setAvailable(boolean available, QuizzViewHolder holder, String artName, String message) {
        if (!available) {
            holder.quizzTitleMark.setAlpha(0.7f);
            holder.quizzTitle.setAlpha(0.7f);
            holder.quizzStatus.setAlpha(0.7f);
            holder.quizzNext.setAlpha(0.7f);
            holder.itemView.setOnClickListener(v -> {
                QuizzUnavailableDialog quizzUnavailableDialog;
                if(message != null) quizzUnavailableDialog= new QuizzUnavailableDialog(message);
                else quizzUnavailableDialog = new QuizzUnavailableDialog();
                quizzUnavailableDialog.show(fragmentManager, "QuizzUnavailableDialog");
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), QuizActivity.class);
                intent.putExtra("artName", artName);
                holder.itemView.getContext().startActivity(intent);
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

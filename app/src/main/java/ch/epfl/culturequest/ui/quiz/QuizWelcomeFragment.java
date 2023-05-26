package ch.epfl.culturequest.ui.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.databinding.FragmentQuizWelcomeBinding;

public class QuizWelcomeFragment extends Fragment {

    FragmentQuizWelcomeBinding binding;
    QuizViewModel quizViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizWelcomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // get the uid from the bundle
        if (getArguments() == null) {
            return root;

        }

        String uid = getArguments().getString("uid");
        // String tournament = getArguments().getString("tournament");
        Tournament tournament = TournamentManagerApi.getTournamentFromSharedPref();
        String artName = getArguments().getString("artName");

        if ( uid == null || artName == null) {
            throw new RuntimeException("Null argument");
        }

        quizViewModel = QuizViewModel.getQuiz(uid, tournament.getTournamentId(), artName);

        binding.titleTextView.setText("The " + artName + " Quiz");
        binding.blackTextView.setText(artName);


        quizViewModel.getImage().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(binding.imageView));

        binding.startButton.setOnClickListener(a-> startQuiz());

        return root;
    }

    public QuizQuestionFragment startQuiz() {
        return quizViewModel.startQuiz();
    }
}

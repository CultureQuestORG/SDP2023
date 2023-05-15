package ch.epfl.culturequest.ui.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentQuizWelcomeBinding;

public class QuizWelcomeFragment extends Fragment {

    FragmentQuizWelcomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizWelcomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // get the uid from the bundle
        if (getArguments() == null) {
            return root;

        }

        String uid = getArguments().getString("uid");
        String tournament = getArguments().getString("tournament");
        String artName = getArguments().getString("artName");

        QuizViewModel quizViewModel = QuizViewModel.getQuiz(uid, tournament, artName);

        binding.titleTextView.setText(tournament);
        binding.blackTextView.setText(artName);




        root.findViewById(R.id.nextButton).setOnClickListener(a-> {
            quizViewModel.startQuiz();
        });

        return root;
    }
}

package ch.epfl.culturequest.ui.quiz;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.epfl.culturequest.databinding.FragmentQuizVictoryBinding;

public class QuizVictoryFragment extends Fragment {

    FragmentQuizVictoryBinding binding;

    private static final String pointsFormat = "You earned %d points";


    @SuppressLint("DefaultLocale")
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        binding = FragmentQuizVictoryBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        assert getArguments() != null;
        int points = getArguments().getInt("score");
        binding.pointsEarned.setText(String.format(pointsFormat, points));


        binding.btnBackToTournament.setOnClickListener(a-> {
            //handle back to tournament
        });


        return root;
    }



}


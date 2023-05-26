package ch.epfl.culturequest.ui.quiz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.epfl.culturequest.databinding.FragmentQuizVictoryBinding;
import ch.epfl.culturequest.ui.events.tournaments.TournamentActivity;

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
            Intent intent = new Intent(getActivity(), TournamentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        });


        return root;
    }



}


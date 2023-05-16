package ch.epfl.culturequest.ui.quiz;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.epfl.culturequest.databinding.FragmentQuizGameOverBinding;
import ch.epfl.culturequest.databinding.FragmentQuizInterBinding;

public class QuizGameOverFragment extends Fragment {

    FragmentQuizGameOverBinding binding;


    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        binding = FragmentQuizGameOverBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        binding.btnBackToTournament.setOnClickListener(a-> {
            //handle back to tournament
        });

        return root;
    }



}

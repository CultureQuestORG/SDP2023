package ch.epfl.culturequest.ui.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentQuizQuestionBinding;
import ch.epfl.culturequest.social.quiz.Question;

public class QuizQuestionFragment extends Fragment {
    FragmentQuizQuestionBinding binding;
    QuizViewModel quizViewModel;

    Question question;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String uid = getArguments().getString("uid");
        String tournament = getArguments().getString("tournament");
        String artName = getArguments().getString("artName");
        int questionNumber = getArguments().getInt("questionNumber");

        quizViewModel = QuizViewModel.getQuiz(uid, tournament, artName);

        question = quizViewModel.getQuestion(questionNumber);

        binding.questionTextView.setText(question.getQuestion());

        List<String> possibilities = question.getPossibilities();

        binding.answer1RadioButton.setText(possibilities.get(0));
        binding.answer2RadioButton.setText(possibilities.get(1));
        binding.answer3RadioButton.setText(possibilities.get(2));
        binding.answer4RadioButton.setText(possibilities.get(3));


        root.findViewById(R.id.nextButton).setOnClickListener(a-> {
            // check that at least one answer has been selected
            if (binding.answer1RadioButton.isChecked() || binding.answer2RadioButton.isChecked() || binding.answer3RadioButton.isChecked() || binding.answer4RadioButton.isChecked()) {


            }
            else{

            }

        });



        return root;

    }

    public boolean checkAnswer(){
        String selectedAnswer = "";
        if (binding.answer1RadioButton.isChecked()) {
            selectedAnswer = binding.answer1RadioButton.getText().toString();
        }
        else if (binding.answer2RadioButton.isChecked()) {
            selectedAnswer = binding.answer2RadioButton.getText().toString();
        }
        else if (binding.answer3RadioButton.isChecked()) {
            selectedAnswer = binding.answer3RadioButton.getText().toString();
        }
        else if (binding.answer4RadioButton.isChecked()) {
            selectedAnswer = binding.answer4RadioButton.getText().toString();
        }

        return question.isCorrect(selectedAnswer);


    }


}

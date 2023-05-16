package ch.epfl.culturequest.ui.quiz;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.FragmentQuizQuestionBinding;
import ch.epfl.culturequest.social.quiz.Question;

public class QuizQuestionFragment extends Fragment {
    FragmentQuizQuestionBinding binding;
    QuizViewModel quizViewModel;

    List<RadioButton> possibleAnswers=new ArrayList<>();

    String question;

    List<String> possibilities;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() == null) {
            throw new RuntimeException("QuizQuestionFragment needs arguments");
        }
        String uid = getArguments().getString("uid");
        String tournament = getArguments().getString("tournament");
        String artName = getArguments().getString("artName");
        int questionNumber = getArguments().getInt("questionNumber");
        question = getArguments().getString("question");
        possibilities = getArguments().getStringArrayList("possibleAnswers");


        quizViewModel = QuizViewModel.getQuiz(uid, tournament, artName);

        binding.questionTextView.setText(question);

        binding.progressBar.setProgress(100*questionNumber/ Objects.requireNonNull(quizViewModel.getQuiz().getValue()).getQuestions().size());


       possibleAnswers.add(binding.answer1RadioButton);
       possibleAnswers.add(binding.answer2RadioButton);
       possibleAnswers.add(binding.answer3RadioButton);
       possibleAnswers.add(binding.answer4RadioButton);

         for (int i = 0; i < possibilities.size(); i++)
              possibleAnswers.get(i).setText(possibilities.get(i));




        root.findViewById(R.id.nextButton).setOnClickListener(a-> {
            // check that at least one answer has been selected
            if (!possibleAnswers.stream().reduce(false, (acc, possibleAnswer) -> acc || possibleAnswer.isChecked(), (acc1, acc2) -> acc1 || acc2)){
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage("Please select an answer")
                        .setPositiveButton("OK", (dialog1, which) -> {
                            dialog1.dismiss();
                        })
                        .create();
                dialog.show();
                return;
            }
            quizViewModel.answerQuestion(questionNumber, possibleAnswers.stream().filter(RadioButton::isChecked).findFirst().get().getText().toString());

        });



        return root;

    }

}

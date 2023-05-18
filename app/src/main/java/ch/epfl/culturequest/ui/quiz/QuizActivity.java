package ch.epfl.culturequest.ui.quiz;



import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.databinding.ActivityQuizBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.tournament.quiz.Question;
import ch.epfl.culturequest.tournament.quiz.Quiz;
import ch.epfl.culturequest.utils.AndroidUtils;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());
        // fetch the tournament and artname from the intent
        String tournament = getIntent().getStringExtra("tournament");
        String artName = getIntent().getStringExtra("artName");
        String uid;
        if (Profile.getActiveProfile() != null) {
            uid = Profile.getActiveProfile().getUid();
        } else {
            uid = "1234";
        }

        binding = ActivityQuizBinding.inflate(getLayoutInflater());



        QuizViewModel.addQuiz(tournament, artName,this, uid);


        setContentView(binding.getRoot());


    }



    public void welcome(){
        Bundle bundle = basicBundle();
        QuizWelcomeFragment fragment = new QuizWelcomeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
    }



    public void goToQuestion(int questionNumber,Question question) {
        Bundle bundle = basicBundle();
        bundle.putInt("questionNumber", questionNumber);
        bundle.putStringArrayList("possibleAnswers", question.getPossibilities());
        bundle.putString("question", question.getQuestion());
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
    }

    public void interQuestion(int score){
        Bundle bundle = basicBundle();
        bundle.putInt("score", score);
        QuizInterFragment fragment = new QuizInterFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
    }

    public void FailQuiz(){
        QuizGameOverFragment fragment = new QuizGameOverFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
    }


    private Bundle basicBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("uid", "1234");
        bundle.putString("tournament", "tournament1");
        bundle.putString("artName", "La Joconde");
        return bundle;
    }


    public void endQuiz(int score) {
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        QuizVictoryFragment fragment = new QuizVictoryFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();

    }
}

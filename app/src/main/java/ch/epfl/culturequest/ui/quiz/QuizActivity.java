package ch.epfl.culturequest.ui.quiz;



import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.databinding.ActivityQuizBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.AndroidUtils;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;

    private String uid;
    private Tournament tournament;
    private String artName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());
        // fetch the tournament and artname from the intent
        tournament = TournamentManagerApi.getTournamentFromSharedPref();
        //tournament = getIntent().getStringExtra("tournament");
        artName = getIntent().getStringExtra("artName");
        if (Profile.getActiveProfile() != null) {
            uid = Profile.getActiveProfile().getUid();
        } else {
            uid = "1234";
        }

        if (artName == null) {
            throw new RuntimeException("Null argument");
        }

        binding = ActivityQuizBinding.inflate(getLayoutInflater());



        QuizViewModel.addQuiz(artName,this, uid);


        setContentView(binding.getRoot());


    }



    public QuizWelcomeFragment welcome(){
        Bundle bundle = basicBundle();
        QuizWelcomeFragment fragment = new QuizWelcomeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
        return fragment;
    }



    public QuizQuestionFragment goToQuestion(int questionNumber, QuizQuestion question) {
        Bundle bundle = basicBundle();
        bundle.putInt("questionNumber", questionNumber);
        bundle.putStringArrayList("possibleAnswers", new ArrayList<>(question.getPossibleAnswers()));
        bundle.putString("question", question.getQuestionContent());
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
        return fragment;
    }

    public QuizInterFragment interQuestion(int score){
        Bundle bundle = basicBundle();
        bundle.putInt("score", score);
        QuizInterFragment fragment = new QuizInterFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
        return fragment;
    }

    public QuizGameOverFragment FailQuiz(){
        QuizGameOverFragment fragment = new QuizGameOverFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
        return fragment;
    }


    private Bundle basicBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        // bundle.putString("tournament", tournament);
        bundle.putString("artName", artName);
        return bundle;
    }


    public QuizVictoryFragment endQuiz(int score) {
        Bundle bundle = new Bundle();
        bundle.putInt("score", score);
        QuizVictoryFragment fragment = new QuizVictoryFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
        return fragment;

    }
}

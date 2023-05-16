package ch.epfl.culturequest.ui.quiz;



import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;


import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.ActivityQuizBinding;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.social.quiz.Question;
import ch.epfl.culturequest.social.quiz.Quiz;
import ch.epfl.culturequest.utils.AndroidUtils;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To make the status bar transparent
        AndroidUtils.removeStatusBar(getWindow());



        binding = ActivityQuizBinding.inflate(getLayoutInflater());

        ArrayList<Question> questions = new ArrayList<>();
        // create 5 questions about "La Joconde"
        ArrayList<String> answers1 = new ArrayList<>();
        answers1.add("Leonardo da Vinci");
        answers1.add("Pablo Picasso");
        answers1.add("Vincent Van Gogh");
        answers1.add("Claude Monet");
        questions.add(new Question("Who painted the famous artwork known as \"La Joconde\"?",answers1, "Leonardo da Vinci"));
        ArrayList<String> answers2 = new ArrayList<>();
        answers2.add("15th century");
        answers2.add("16th century");
        answers2.add("17th century");
        answers2.add("18th century");
        questions.add(new Question("Which century was \"La Joconde\" created in?",answers2, "16th century"));
        ArrayList<String> answers3 = new ArrayList<>();
        answers3.add("Mona Lisa");
        answers3.add("The Last Supper");
        answers3.add("The Creation of Adam");
        answers3.add("The Starry Night");
        questions.add(new Question("What is another name for the painting \"La Joconde\"?",answers3, "Mona Lisa"));
        ArrayList<String> answers4 = new ArrayList<>();
        answers4.add("The Louvre");
        answers4.add("The Metropolitan Museum of Art");
        answers4.add("The National Gallery");
        answers4.add("The Vatican Museums");
        questions.add(new Question("Where is \"La Joconde\" located?",answers4, "The Louvre"));
        ArrayList<String> answers5 = new ArrayList<>();
        answers5.add("Oil painting");
        answers5.add("Watercolor painting");
        answers5.add("Acrylic painting");
        answers5.add("Tempera painting");
        questions.add(new Question("What technique did Leonardo da Vinci use in the creation of \"La Joconde\"?",answers5, "Oil painting"));

        Quiz quiz = new Quiz("La Joconde", questions,"tournament1");


        QuizViewModel.addQuiz(quiz, this,"1234");
        //send data to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("uid", "1234");
        bundle.putString("tournament", "tournament1");
        bundle.putString("artName", "La Joconde");
        QuizWelcomeFragment fragment = new QuizWelcomeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
        setContentView(binding.getRoot());


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

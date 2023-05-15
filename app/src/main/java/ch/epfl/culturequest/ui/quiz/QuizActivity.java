package ch.epfl.culturequest.ui.quiz;



import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

import ch.epfl.culturequest.R;
import ch.epfl.culturequest.databinding.ActivityQuizBinding;
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

        List<Question> questions = new ArrayList<>();
        // create 5 questions about "La Joconde"
        questions.add(new Question("Who painted the famous artwork known as \"La Joconde\"?",List.of("Leonardo da Vinci", "Pablo Picasso", "Vincent Van Gogh", "Claude Monet"), "Leonardo da Vinci"));
        questions.add(new Question("Which century was \"La Joconde\" created in?",List.of("15th century", "16th century", "17th century", "18th century"), "16th century"));
        questions.add(new Question("What is another name for the painting \"La Joconde\"?",List.of("Mona Lisa", "The Last Supper", "The Creation of Adam", "The Starry Night"), "Mona Lisa"));
        questions.add(new Question("Where is \"La Joconde\" located?",List.of("The Louvre", "The Metropolitan Museum of Art", "The National Gallery", "The Vatican Museums"), "The Louvre"));
        questions.add(new Question("What technique did Leonardo da Vinci use in the creation of \"La Joconde\"?",List.of("Oil painting", "Watercolor painting", "Acrylic painting", "Tempera painting"), "Oil painting"));
        Quiz quiz = new Quiz("La Joconde", questions,"1234",0,"tournament1",100,0,false,false);


        QuizViewModel.addQuiz(quiz, this);
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



    public void startQuiz() {
        Bundle bundle = new Bundle();
        bundle.putString("uid", "1234");
        bundle.putString("tournament", "tournament1");
        bundle.putString("artName", "La Joconde");
        bundle.putInt("questionNumber", 0);
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
    }

    public void FinishQuestion(boolean success) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", "1234");
        bundle.putString("tournament", "tournament1");
        bundle.putString("artName", "La Joconde");

        bundle.putBoolean("success", success);
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_quiz, fragment)
                .commit();
    }


}

package ch.epfl.culturequest.ui.quiz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Objects;

import ch.epfl.culturequest.social.quiz.Question;
import ch.epfl.culturequest.social.quiz.Quiz;
import kotlin.Triple;


public class QuizViewModel extends ViewModel {


    public static HashMap<Triple<String, String, String>, QuizViewModel> quizHashMap = new HashMap<>();


    private final MutableLiveData<Quiz> quiz = new MutableLiveData<>();

    private final MutableLiveData<QuizActivity> quizActivity = new MutableLiveData<>();


    public QuizViewModel(Quiz quiz, QuizActivity quizActivity) {
        this.quiz.setValue(quiz);
        this.quizActivity.setValue(quizActivity);

    }
    public void setQuiz(Quiz quiz) {
        this.quiz.setValue(quiz);
    }

    public MutableLiveData<Quiz> getQuiz() {
        return quiz;
    }

    public void startQuiz() {
        System.out.println("start quiz");
        System.out.println(quizActivity.getValue());
        Objects.requireNonNull(quizActivity.getValue()).startQuiz();
    }



    public static void addQuiz(Quiz quiz,QuizActivity activity) {
        Triple<String, String, String> key = new Triple<>(quiz.getUid(), quiz.getTournament(), quiz.getArtName());
        quizHashMap.put(key, new QuizViewModel(quiz, activity));
    }

    public static QuizViewModel getQuiz(String uid, String tournament, String artName) {
        Triple<String, String, String> key = new Triple<>(uid, tournament, artName);
        return quizHashMap.get(key);
    }

    public Question getQuestion(int questionNumber) {
        return Objects.requireNonNull(quiz.getValue()).getQuestions().get(questionNumber);
    }


}

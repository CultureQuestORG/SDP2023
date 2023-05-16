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

    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<Integer> nextScore = new MutableLiveData<>();

    private final MutableLiveData<Integer> questionNumber = new MutableLiveData<>();



    public QuizViewModel(Quiz quiz, QuizActivity quizActivity) {
        this.quiz.setValue(quiz);
        this.quizActivity.setValue(quizActivity);
        score.setValue(0);
        nextScore.setValue(100);


    }
    public void setQuiz(Quiz quiz) {
        this.quiz.setValue(quiz);
    }

    public MutableLiveData<Quiz> getQuiz() {
        return quiz;
    }

    public void startQuiz() {
        Objects.requireNonNull(quizActivity.getValue()).goToQuestion(0, Objects.requireNonNull(quiz.getValue()).getQuestions().get(0));
    }

    public void answerQuestion(int questionNumber, String answer) {
        boolean correct = Objects.requireNonNull(quiz.getValue()).getQuestions().get(questionNumber).isCorrect(answer);
        if (!correct) {
            Objects.requireNonNull(quizActivity.getValue()).FailQuiz();
            return;
        }

        score.setValue(nextScore.getValue());
        this.questionNumber.setValue(questionNumber + 1);
        if (this.questionNumber.getValue() == Objects.requireNonNull(quiz.getValue()).getQuestions().size()) {
            Objects.requireNonNull(quizActivity.getValue()).endQuiz(score.getValue());
            return;
        }

        Objects.requireNonNull(quizActivity.getValue()).interQuestion(nextScore.getValue());


    }

    public void nextQuestion(int nextScore) {
        this.nextScore.setValue(nextScore);
        Objects.requireNonNull(quizActivity.getValue()).goToQuestion(questionNumber.getValue(), getQuestion(questionNumber.getValue()));

    }

    public void finishQuiz(int score) {
        Objects.requireNonNull(quizActivity.getValue()).endQuiz(score);
    }



    public static void addQuiz(Quiz quiz,QuizActivity activity,String uid) {
        Triple<String, String, String> key = new Triple<>(uid, quiz.getTournament(), quiz.getArtName());
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

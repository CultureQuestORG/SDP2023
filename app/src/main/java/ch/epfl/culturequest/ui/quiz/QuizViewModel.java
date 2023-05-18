package ch.epfl.culturequest.ui.quiz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Objects;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.tournament.quiz.Question;
import ch.epfl.culturequest.tournament.quiz.Quiz;
import kotlin.Triple;


public class QuizViewModel extends ViewModel {


    public static HashMap<Triple<String, String, String>, QuizViewModel> quizHashMap = new HashMap<>();


    private final MutableLiveData<Quiz> quiz = new MutableLiveData<>();

    private final MutableLiveData<QuizActivity> quizActivity = new MutableLiveData<>();

    private final MutableLiveData<String> uid = new MutableLiveData<>();

    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<Integer> nextScore = new MutableLiveData<>();

    private final MutableLiveData<Integer> questionNumber = new MutableLiveData<>();



    public QuizViewModel(Quiz quiz, QuizActivity quizActivity, String uid) {
        this.quiz.setValue(quiz);
        this.quizActivity.setValue(quizActivity);
        score.setValue(0);
        nextScore.setValue(100);
        this.uid.setValue(uid);


    }
    public void setQuiz(Quiz quiz) {
        this.quiz.setValue(quiz);
    }

    public MutableLiveData<Quiz> getQuiz() {
        return quiz;
    }

    public void startQuiz() {
        Objects.requireNonNull(quizActivity.getValue()).goToQuestion(0, Objects.requireNonNull(quiz.getValue()).getQuestions().get(0));
        Database.startQuiz(Objects.requireNonNull(quiz.getValue()).getTournament(), Objects.requireNonNull(quiz.getValue()).getArtName(), uid.getValue());
    }

    public void answerQuestion(int questionNumber, int answer) {
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
        Database.setScoreQuiz(Objects.requireNonNull(quiz.getValue()).getTournament(), Objects.requireNonNull(quiz.getValue()).getArtName(), uid.getValue(), score);
    }



    public static void addQuiz(String tournament,String artName,QuizActivity activity,String uid) {
        Objects.requireNonNull(Database.getQuiz(tournament, artName)).thenAccept(quiz -> {
            Triple<String, String, String> key = new Triple<>(uid, tournament, artName);
            quizHashMap.put(key, new QuizViewModel(quiz, activity,uid));
            activity.welcome();
        });
    }

    public static QuizViewModel getQuiz(String uid, String tournament, String artName) {
        Triple<String, String, String> key = new Triple<>(uid, tournament, artName);
        return quizHashMap.get(key);
    }

    public Question getQuestion(int questionNumber) {
        return Objects.requireNonNull(quiz.getValue()).getQuestions().get(questionNumber);
    }

    public MutableLiveData<Integer> getScore() {
        return score;
    }

    public MutableLiveData<Integer> getNextScore() {
        return nextScore;
    }





}

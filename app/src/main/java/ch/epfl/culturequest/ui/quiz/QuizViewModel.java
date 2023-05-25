package ch.epfl.culturequest.ui.quiz;

import androidx.fragment.app.Fragment;
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

    private final MutableLiveData<String> image = new MutableLiveData<>();

    public MutableLiveData<String> getImage() {
        return image;
    }

    public QuizViewModel(Quiz quiz, QuizActivity quizActivity, String uid) {
        this.quiz.postValue(quiz);
        this.quizActivity.postValue(quizActivity);
        score.postValue(0);
        nextScore.postValue(100);
        this.uid.postValue(uid);
        questionNumber.postValue(0);
        Database.getImageForArt(quiz.getArtName()).thenAccept(image::postValue);



    }
    public void setQuiz(Quiz quiz) {
        this.quiz.postValue(quiz);
    }

    public MutableLiveData<Quiz> getQuiz() {
        return quiz;
    }

    public QuizQuestionFragment startQuiz() {
        Database.startQuiz(Objects.requireNonNull(quiz.getValue()).getTournament(), Objects.requireNonNull(quiz.getValue()).getArtName(), uid.getValue());
        return Objects.requireNonNull(quizActivity.getValue()).goToQuestion(0, Objects.requireNonNull(quiz.getValue()).getQuestions().get(0));
    }

    public Fragment answerQuestion(int questionNumber, int answer) {
        boolean correct = Objects.requireNonNull(quiz.getValue()).getQuestions().get(questionNumber).isCorrect(answer);
        if (!correct) {
            return Objects.requireNonNull(quizActivity.getValue()).FailQuiz();

        }

        score.postValue(nextScore.getValue());
        this.questionNumber.postValue(questionNumber + 1);
        if (questionNumber + 1 == Objects.requireNonNull(quiz.getValue()).getQuestions().size()) {
            return Objects.requireNonNull(quizActivity.getValue()).endQuiz(nextScore.getValue());

        }

        return Objects.requireNonNull(quizActivity.getValue()).interQuestion(nextScore.getValue());


    }

    public QuizQuestionFragment nextQuestion(int nextScore) {
        this.nextScore.postValue(nextScore);
        return Objects.requireNonNull(quizActivity.getValue()).goToQuestion(questionNumber.getValue(), getQuestion(questionNumber.getValue()));
    }

    public QuizVictoryFragment finishQuiz(int score) {
        Database.setScoreQuiz(Objects.requireNonNull(quiz.getValue()).getTournament(), Objects.requireNonNull(quiz.getValue()).getArtName(), uid.getValue(), score);
        return Objects.requireNonNull(quizActivity.getValue()).endQuiz(score);
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

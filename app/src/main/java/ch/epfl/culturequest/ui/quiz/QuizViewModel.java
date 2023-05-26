package ch.epfl.culturequest.ui.quiz;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Objects;

import ch.epfl.culturequest.backend.tournament.apis.TournamentManagerApi;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.ArtQuiz;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.QuizQuestion;
import ch.epfl.culturequest.backend.tournament.tournamentobjects.Tournament;
import ch.epfl.culturequest.database.Database;
import kotlin.Triple;


public class QuizViewModel extends ViewModel {


    public static HashMap<Triple<String, String, String>, QuizViewModel> quizHashMap = new HashMap<>();


    private final MutableLiveData<ArtQuiz> quiz = new MutableLiveData<>();

    private final MutableLiveData<QuizActivity> quizActivity = new MutableLiveData<>();

    private final MutableLiveData<String> uid = new MutableLiveData<>();

    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<Integer> nextScore = new MutableLiveData<>();

    private final MutableLiveData<Integer> questionNumber = new MutableLiveData<>();

    private final MutableLiveData<String> tournament = new MutableLiveData<>();

    private final MutableLiveData<String> image = new MutableLiveData<>();


    public QuizViewModel(ArtQuiz quiz, QuizActivity quizActivity, String uid, String tournament) {
        this.quiz.postValue(quiz);
        this.quizActivity.postValue(quizActivity);
        score.postValue(0);
        nextScore.postValue(100);
        this.uid.postValue(uid);
        questionNumber.postValue(0);
        this.tournament.postValue(tournament);
        Database.getImageForArt(quiz.getArtName()).thenAccept(image::postValue);
    }
    public void setQuiz(ArtQuiz quiz) {
        this.quiz.postValue(quiz);
    }

    public MutableLiveData<ArtQuiz> getQuiz() {
        return quiz;
    }

    public QuizQuestionFragment startQuiz() {
        Database.startQuiz(tournament.getValue(), Objects.requireNonNull(quiz.getValue()).getArtName(), uid.getValue());
        return Objects.requireNonNull(quizActivity.getValue()).goToQuestion(0, Objects.requireNonNull(quiz.getValue()).getQuestions().get(0));
    }

    public Fragment answerQuestion(int questionNumber, int answer) {
        boolean correct = quiz.getValue().getQuestions().get(questionNumber).getCorrectAnswerIndex() == answer;
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
        Database.setScoreQuiz(tournament.getValue(), Objects.requireNonNull(quiz.getValue()).getArtName(), uid.getValue(), score);
        return Objects.requireNonNull(quizActivity.getValue()).endQuiz(score);
    }



    public static void addQuiz(String artName,QuizActivity activity,String uid) {
        Tournament tournament1 = TournamentManagerApi.getTournamentFromSharedPref();
        Triple<String, String, String> key = new Triple<>(uid, tournament1.getTournamentId(), artName);
        quizHashMap.put(key, new QuizViewModel(tournament1.getArtQuizzes().get(artName), activity,uid,tournament1.getTournamentId()));
        activity.welcome();
    }

    public static QuizViewModel getQuiz(String uid, String tournament, String artName) {
        Triple<String, String, String> key = new Triple<>(uid, tournament, artName);
        return quizHashMap.get(key);
    }

    public QuizQuestion getQuestion(int questionNumber) {
        return Objects.requireNonNull(quiz.getValue()).getQuestions().get(questionNumber);
    }

    public MutableLiveData<Integer> getScore() {
        return score;
    }

    public MutableLiveData<Integer> getNextScore() {
        return nextScore;
    }

    public MutableLiveData<String> getImage() {
        return image;
    }


}

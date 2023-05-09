package ch.epfl.culturequest.ui.quiz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import ch.epfl.culturequest.social.quizz.Question;
import ch.epfl.culturequest.social.quizz.Quiz;

public class QuizViewModel extends ViewModel {


    private final MutableLiveData<Quiz> quiz = new MutableLiveData<>();


    public QuizViewModel(String uid,String tournament, String artWork) {

    }
    public void setQuiz(Quiz quiz) {
        this.quiz.setValue(quiz);
    }

    public MutableLiveData<Quiz> getQuiz() {
        return quiz;
    }


}

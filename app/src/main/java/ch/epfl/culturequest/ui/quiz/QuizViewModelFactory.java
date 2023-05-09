package ch.epfl.culturequest.ui.quiz;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class QuizViewModelFactory implements ViewModelProvider.Factory {
private final String uid;
private final String tournament;
private final String artWork;


public QuizViewModelFactory(String uid, String tournament,String artWork) {
        this.uid = uid;
        this.tournament = tournament;
        this.artWork = artWork;
        }

@NonNull
@Override
public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QuizViewModel.class)) {
        return (T) new QuizViewModel(uid,tournament,artWork);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
        }
        }

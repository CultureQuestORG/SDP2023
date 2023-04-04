package ch.epfl.culturequest.utils;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

public class AutoCompleteTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @Test
    public void autoCompleteIsCorrect() {
        List<String> dictionnary = List.of("Albert","Alberto","Albertin","Al","alI","Luca","Ugo","Hugo","Thomas","John","Jack");
        List<String> result = autoCompletion.top5matches("Al", dictionnary);
        assertEquals(result, List.of("Al","alI","Albert","Alberto","Albertin"));


    }
}

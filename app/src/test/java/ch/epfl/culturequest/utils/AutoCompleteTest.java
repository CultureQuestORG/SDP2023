package ch.epfl.culturequest.utils;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Set;

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
        Set<String> dictionary = Set.of("Albert","Alberto","Albertan","Al","alI","Luca","Ugo","Hugo","Thomas","John","Jack");
        List<String> result = AutoComplete.topNMatches("Al", dictionary,5);
        assertEquals(result, List.of("Al","alI","Albert","Alberto","Albertan"));
    }
}

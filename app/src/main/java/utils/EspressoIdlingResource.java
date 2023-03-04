package utils;

import androidx.test.espresso.idling.CountingIdlingResource;

public class EspressoIdlingResource {
    public static final String RESOURCE = "GLOBAL";
    public static final CountingIdlingResource countingIdlingResource = new CountingIdlingResource(RESOURCE);

    public static void increment() {
        countingIdlingResource.increment();
    }

    public static void decrement() {
        if(!countingIdlingResource.isIdleNow()) {
            countingIdlingResource.decrement();
        }
    }

}

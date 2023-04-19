package ch.epfl.culturequest.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import androidx.activity.ComponentActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.culturequest.database.Database;

@RunWith(AndroidJUnit4.class)
public class AuthenticatorTest {
    private Authenticator authenticator;


    @Before
    public void setup() {
        // Set up the database to run on the local emulator of Firebase
        Database.setEmulatorOn();

        // clear the database before starting the following tests
        Database.clearDatabase();

        ComponentActivity mockActivity = mock(ComponentActivity.class);
        authenticator = new Authenticator(mockActivity, true);
    }


    @Test
    public void authenticatorSignInMatchesCurrentUser() throws InterruptedException {
        authenticator.signIn();
        Thread.sleep(2000);
        assertEquals(authenticator.getUser(), FirebaseAuth.getInstance().getCurrentUser());
    }

    @Test
    public void signOutMatchesCurrentUser() throws InterruptedException {
        authenticator.signIn();
        Thread.sleep(2000);
        authenticator.signOut();
        Thread.sleep(2000);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());

    }

    @After
    public void tearDown() {
        // clear the database after running the tests
        Database.clearDatabase();
    }
}

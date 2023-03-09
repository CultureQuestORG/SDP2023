package ch.epfl.culturequest.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import androidx.activity.ComponentActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AuthenticatorTest {
    private Authenticator authenticator;


    @Before
    public void setup(){
        ComponentActivity mockActivity = mock(ComponentActivity.class);
        authenticator = new Authenticator(mockActivity, true);
    }


    @Test
    public void authenticatorSignInMatchesCurrentUser() throws InterruptedException {
        authenticator.signIn();
        Thread.sleep(4000);
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




}

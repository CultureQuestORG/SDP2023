package ch.epfl.culturequest.social;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.database.MockDatabase;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {

    private Profile profile;
    private FirebaseUser user;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String defaultUriString = "res/drawable/logo_compact.png";


    @Before
    public void setup() throws InterruptedException {
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword("test@gmail.com", "abcdefg")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) user = mAuth.getCurrentUser();
                });

        Thread.sleep(5000);
        if (user != null) {
            profile = new Profile("joker", defaultUriString);
        } else System.exit(0);

    }

    @Test
    public void profileHasCorrectUID() {
        assertThat(profile.getUid(), is(user.getUid()));
    }

    @Test
    public void profileHasCorrectUsername() {
        assertThat(profile.getUsername(), is("joker"));
    }

    @Test
    public void profileHasCorrectEmail() {
        assertThat(profile.getEmail(), is(user.getEmail()));
    }

    @Test
    public void nameIsTheSame() {
        assertThat(profile.getName(), is(user.getDisplayName()));
    }

    @Test
    public void profileHasCorrectPhoneNum() {
        assertThat(profile.getPhoneNumber(), is(user.getPhoneNumber()));
    }

    @Test
    public void profilePicIsCorrect() {
        assertThat(profile.getProfilePicture(), is(defaultUriString));
    }

    @Test
    public void setProfilePicWorks() {
        String newPic = "res/drawable/logo_plain.png";
        profile.setProfilePicture(newPic);
        assertThat(profile.getProfilePicture(), is(newPic));
    }

    @Test
    public void setPhoneNumberWorks() {
        String newPhone = "123456789";
        profile.setPhoneNumber(newPhone);
        assertThat(profile.getPhoneNumber(), is(newPhone));
    }

    @Test
    public void setEmailWorks() {
        String newEmail = "john.doe@gmail.com";
        profile.setEmail(newEmail);
        assertThat(profile.getEmail(), is(newEmail));
    }

    @Test
    public void setUsernameWorks() {
        String newUsername = "johnny";
        profile.setUsername(newUsername);
        assertThat(profile.getUsername(), is(newUsername));
    }

    @Test
    public void setUidWorks() {
        String newUid = "123456789";
        profile.setUid(newUid);
        assertThat(profile.getUid(), is(newUid));
    }

    @Test
    public void setNameWorks() {
        String newName = "John Doe Jr.";
        profile.setName(newName);
        assertThat(profile.getName(), is(newName));
    }

    @Test
    public void setScoreWorks() {
        int newScore = 100;
        profile.setScore(newScore);
        assertThat(profile.getScore(), is(newScore));
    }

    @Test
    public void emptyConstructorWorks() {
        Profile emptyProfile = new Profile();
        assertThat(emptyProfile.getUid(), is(""));
        assertThat(emptyProfile.getName(), is(""));
        assertThat(emptyProfile.getUsername(), is(""));
        assertThat(emptyProfile.getEmail(), is(""));
        assertThat(emptyProfile.getPhoneNumber(), is(""));
        assertThat(emptyProfile.getProfilePicture(), is(""));
        //users images should be an empty list
        assertThat(emptyProfile.getPosts().size(), is(0));
        assertThat(emptyProfile.getScore(), is(0));

    }

    @Test
    public void setImageWorks() {
        Database.init(new MockDatabase());
        Image image = new Image("imageTest","this is an image",defaultUriString, 12345,"myUid");
        Database.setImage(image);
        HashMap<String, Boolean> images = new HashMap<>();
        images.put(image.getUid(), true);
        profile.addObserver(((o, arg) -> {
            assertThat(profile.getPosts().size(), is(images.size()));
            assertThat(profile.getPosts().get(0).getUid(), is(image.getUid()));
        }));
        profile.setImages(images);
    }

    @Test
    public void addingPostIncrementsAllPostsSize() {
        Post post = new Post("def", "123",
                "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",
                "Piece of Art", new Date(), 0, new ArrayList<>());

        profile.addPost(post);
        assertEquals(1, profile.getPosts().size());
    }

    @Test
    public void postsAreSortedByDate() {
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        List<Post> posts = IntStream.range(1, 10).mapToObj(i -> {
            try {
                return new Post(String.valueOf(i), "123",
                        "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",
                        "Piece of Art", DateFor.parse("0" + i + "/01/2000"), 0, new ArrayList<>());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        profile.setPosts(posts);
        Collections.reverse(posts);
        assertEquals(posts, profile.getPosts());
    }

    @Test
    public void retrievingPostsWithLimitAndOffsetReturnsLatestPosts(){
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        List<Post> posts = IntStream.range(1, 10).mapToObj(i -> {
            try {
                return new Post(String.valueOf(i), "123",
                        "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",
                        "Piece of Art", DateFor.parse("0" + i + "/01/2000"), 0, new ArrayList<>());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        profile.setPosts(posts);
        Collections.reverse(posts);
        assertEquals(posts.subList(0,3), profile.getPosts(3,0));
        assertEquals(posts.subList(3,7), profile.getPosts(4,3));
    }

    @Test
    public void givingInvalidLimitsAndOffsetThrowsException(){
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        List<Post> posts = IntStream.range(1, 10).mapToObj(i -> {
            try {
                return new Post(String.valueOf(i), "123",
                        "https://firebasestorage.googleapis.com/v0/b/culturequest.appspot.com/o/0000598561_OG.jpeg?alt=media&token=503f241d-cebf-4050-8897-4cbb7595e0b8",
                        "Piece of Art", DateFor.parse("0" + i + "/01/2000"), 0, new ArrayList<>());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        profile.setPosts(posts);
        assertThrows(IllegalArgumentException.class, () -> {
            profile.getPosts(1, 11);//offset is larger than the total number of posts;
        });

        assertThrows(IllegalArgumentException.class, () ->{
            profile.getPosts(-1, 0);
        });
        assertThrows(IllegalArgumentException.class, () ->{
            profile.getPosts(1, -1);
        });
    }

    @Test
    public void noPostsGivesEmptyListWithListAndOffset(){
        assertTrue(profile.getPosts(1,0).isEmpty());
    }

    @Test
    public void toStringWorks() {
        assertThat(profile.toString(), is("Profile: \n" +
                "uid: " + profile.getUid() + "\n" +
                "name: " + profile.getName() + "\n" +
                "username: " + profile.getUsername() + "\n" +
                "email: " + profile.getEmail() + "\n" +
                "phoneNumber: " + profile.getPhoneNumber() + "\n" +
                "profilePicture: " + profile.getProfilePicture() + "\n" +
                        "pictures: " + profile.getPosts() + "\n" +
                "score: " + profile.getScore() + "\n"));
    }


}

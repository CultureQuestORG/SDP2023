package ch.epfl.culturequest.ui.leaderboard;

import static java.util.stream.Collectors.toList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import ch.epfl.culturequest.database.Database;
import ch.epfl.culturequest.social.Profile;
import ch.epfl.culturequest.utils.EspressoIdlingResource;

public class LeaderboardViewModel extends ViewModel {
    private final MutableLiveData<String> currentUsername;
    private final MutableLiveData<String> currentUserProfilePictureUri;
    private final MutableLiveData<String> currentUserScore;
    private final MutableLiveData<String> currentUserRank;
    private final MutableLiveData<String> currentUserRankFriends;
    private final MutableLiveData<List<String>> topNUserNames;
    private final MutableLiveData<List<String>> topNUserNamesFriends;
    private final MutableLiveData<List<String>> topNUserScores;
    private final MutableLiveData<List<String>> topNUserScoresFriends;
    private final MutableLiveData<List<String>> topNUserRanksFriends;
    private final MutableLiveData<List<String>> topNUserProfilePicturesUri;
    private final MutableLiveData<List<String>> topNUserProfilePicturesUriFriends;
    private final MutableLiveData<List<String>> topNUserRanks;
    private final int N = 10;




    public LeaderboardViewModel(String currentUserUid) {
        currentUsername = new MutableLiveData<>();
        currentUserProfilePictureUri = new MutableLiveData<>();
        currentUserScore = new MutableLiveData<>();
        currentUserRank = new MutableLiveData<>();
        topNUserNames = new MutableLiveData<>();
        topNUserScores = new MutableLiveData<>();
        topNUserScoresFriends = new MutableLiveData<>();
        topNUserProfilePicturesUri = new MutableLiveData<>();
        topNUserRanks = new MutableLiveData<>();



        topNUserRanksFriends = new MutableLiveData<>();
        topNUserNamesFriends = new MutableLiveData<>();
        topNUserProfilePicturesUriFriends = new MutableLiveData<>();
        currentUserRankFriends = new MutableLiveData<>();



        // EspressoIdlingResource is used to wait for the database to finish loading before
        // the tests are run
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();
        EspressoIdlingResource.increment();


        // retrieve the current user's information to be displayed in the leaderboard
        Database.getProfile(currentUserUid).whenComplete((p, e) -> {
            currentUsername.setValue(p.getUsername());
            currentUserProfilePictureUri.setValue(p.getProfilePicture());
            currentUserScore.setValue(p.getScore().toString());
            EspressoIdlingResource.decrement();
        });


        Database.getRank(currentUserUid).whenComplete((rank, e) -> {
            currentUserRank.setValue(rank.toString());
            EspressoIdlingResource.decrement();
        });


        Database.getRankFriends(currentUserUid).whenComplete((rank, e) -> {
            currentUserRankFriends.setValue(rank.toString());
            EspressoIdlingResource.decrement();
        });

        // retrieve the top N users' information to be displayed in the leaderboard
        Database.getTopNProfiles(N).whenComplete((topN, e) -> {
            // reverse the list so that the top user is at the top of the leaderboard
            topNUserNames.setValue(topN.stream().map(Profile::getUsername).collect(toList()));
            topNUserScores.setValue(topN.stream().map(p -> p.getScore().toString()).collect(toList()));
            topNUserProfilePicturesUri.setValue(topN.stream().map(Profile::getProfilePicture).collect(toList()));
            // create array of string int from 1 to N corresponding to the ranks of the top N users
            String[] ranks = new String[N];
            for (int i = 0; i < N; i++) {
                ranks[i] = Integer.toString(i + 1);
            }
            topNUserRanks.setValue(List.of(ranks));
            EspressoIdlingResource.decrement();
        });

        Database.getTopNFriendsProfiles(N).whenComplete((topN, e) -> {
            // reverse the list so that the top user is at the top of the leaderboard
            topNUserNamesFriends.setValue(topN.stream().map(Profile::getUsername).collect(toList()));
            topNUserScoresFriends.setValue(topN.stream().map(p -> p.getScore().toString()).collect(toList()));
            topNUserProfilePicturesUriFriends.setValue(topN.stream().map(Profile::getProfilePicture).collect(toList()));

            String[] ranks = new String[N];
            for (int i = 0; i < N; i++) {
                ranks[i] = Integer.toString(i + 1);
            }
            topNUserRanksFriends.setValue(List.of(ranks));

            EspressoIdlingResource.decrement();
        });
    }

    public LiveData<String> getCurrentUsername() {
        return currentUsername;
    }

    public LiveData<String> getCurrentUserProfilePictureUri() {
        return currentUserProfilePictureUri;
    }

    public LiveData<String> getCurrentUserScore() {
        return currentUserScore;
    }

    public LiveData<String> getCurrentUserRank() {
        return currentUserRank;
    }

    public LiveData<List<String>> getTopNUserNames() {
        return topNUserNames;
    }

    public LiveData<List<String>> getTopNUserScores() {
        return topNUserScores;
    }

    public LiveData<List<String>> getTopNUserProfilePicturesUri() {
        return topNUserProfilePicturesUri;
    }

    public LiveData<List<String>> getTopNUserRanks() {
        return topNUserRanks;
    }

    public LiveData<List<String>> getTopNUserScoresFriends() {
        return topNUserScoresFriends;
    }

    public LiveData<List<String>> getTopNUserRanksFriends() {
        return topNUserRanksFriends;
    }

    public LiveData<List<String>> getTopNUserNamesFriends() {
        return topNUserNamesFriends;
    }

    public LiveData<List<String>> getTopNUserProfilePicturesUriFriends() {
        return topNUserProfilePicturesUriFriends;
    }

    public LiveData<String> getCurrentUserRankFriends() {
        return currentUserRankFriends;
    }


}
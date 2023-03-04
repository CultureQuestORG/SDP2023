package ch.epfl.culturequest;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebAPIActivity extends AppCompatActivity {

    public static String BASE_URL = "https://www.boredapi.com/api/";

    private BoredAPI setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(BoredAPI.class);
    }
    private SharedPreferences getDatabase() {
        return getPreferences(Context.MODE_PRIVATE);
    }
    private void addActivityToDatabase(String activity) {
        SharedPreferences database = getDatabase();
        SharedPreferences.Editor editor = database.edit();
        editor.putString(activity, activity);
        editor.apply();
    }
    private String getRandomActivityFromDatabase() {
        SharedPreferences database = getDatabase();
        Set<String> allActivities = database.getAll().keySet();
        List<String> activityList = new ArrayList<>(allActivities);
        Random random = new Random();
        int randomIndex = random.nextInt(activityList.size());
        return activityList.get(randomIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bored_api);
    }


    private void displayCachedResponseOrError() {
        TextView textView = findViewById(R.id.mainTextView);
        if (getDatabase().getAll().size() > 0) {
            String randomActivity = getRandomActivityFromDatabase();
            textView.setText(randomActivity + " (Cached)");
        }
        else{
            textView.setText("Couldn't fetch activity");
        }
    }

    public void fetchAndDisplayActivity(View view) {
        BoredAPI boredAPI = setupRetrofit();
        boredAPI.getActivity().enqueue(new Callback<BoredActivity>() {
            @Override
            public void onResponse(Call<BoredActivity> call, Response<BoredActivity> response) {
                if (response.isSuccessful()) {
                    BoredActivity boredActivity = response.body();
                    addActivityToDatabase(boredActivity.activity);
                    TextView textView = findViewById(R.id.mainTextView);
                    textView.setText(boredActivity.activity);
                }
                else{
                    displayCachedResponseOrError();
                }
            }

            @Override
            public void onFailure(Call<BoredActivity> call, Throwable t) {
                displayCachedResponseOrError();
            }
        });
    }
}
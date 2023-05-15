package ch.epfl.culturequest.backend.notifications_api;

import com.google.gson.JsonObject;

import ch.epfl.culturequest.BuildConfig;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    String BASE_URL = "https://fcm.googleapis.com";
    String SERVER_KEY = BuildConfig.FIREBASE_MESSAGING_KEY;
    String CONTENT_TYPE = "application/json";

    @Headers({"Authorization: key=" + SERVER_KEY, "Content-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<JsonObject> sendNotification(@Body JsonObject payload);
}

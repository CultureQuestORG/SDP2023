package ch.epfl.culturequest;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BoredAPI {
    @GET("activity")
    Call<BoredActivity> getActivity();
}

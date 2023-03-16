package ch.epfl.culturequest.backend.artprocessing.apis;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class responsible for the recognition of the scanned art
 * Uses an unofficial Google Lens API and parses the HTTP response (as HTML) to retrieve the name of the art and additional information
 */


public class RecognitionApi {
    private final String baseGoogleLensAPIURL="https://lens.google.com/uploadbyurl?url=";

    /** Returns an art recognition object (as a future) given the URL of the image associated to the scanned piece of art */
    public CompletableFuture<ArtRecognition> getArtName(String imageURL) {

        String apiEndpoint = baseGoogleLensAPIURL + imageURL;

        return getArtFromParsing(getApiResponse(apiEndpoint));

    }

    private CompletableFuture<String> getApiResponse(String apiEndpoint){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiEndpoint)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/110.0")
                .header("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Cookie","CONSENT=YES+1000")
                .build();

        CompletableFuture<String> f = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                f.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    f.completeExceptionally(new IOException("Unexpected code " + response));
                }

                f.complete(response.body().string());
            }
        });

        return f;
    }

    private CompletableFuture<ArtRecognition> getArtFromParsing(CompletableFuture<String> responseBody){
        Pattern patternArt = Pattern.compile("(?<=\\[\")([a-zàâçéèêëîïôûùüÿñæœ -]*)(?=\",\"((?:monument|peinture|painting|sculpture|photo|historical|lieu)[a-zàâçéèêëîïôûùüÿñæœ -]*)\")", Pattern.CASE_INSENSITIVE);

        return responseBody.thenApply((String s) -> {

            Matcher matcherArt = patternArt.matcher(s);
            matcherArt.find();
            String artName = matcherArt.group(0);
            String additionalInfo = matcherArt.group(2);
            return new ArtRecognition(artName, additionalInfo);

        });
    }
}

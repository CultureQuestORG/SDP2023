package ch.epfl.culturequest.backend.artprocessing.apis;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is responsible for getting the description of an art piece from Wikipedia.
 * It uses a specific Wikipedia HTTP endpoint to access the art's page, retrieves its HTML, and then parses it to get the all the necessary information.
 * For now, it mainly supports paintings and sculptures and isn't very efficient for architecture / monuments.
 */

public class WikipediaDescriptionApi {

    public static String wikipediaBaseUrl = "https://en.wikipedia.org/wiki/Special:Search?search=";

    /** Returns an art description object (as a future) given a recognized piece of art (represented by ArtRecognition) */
    public CompletableFuture<BasicArtDescription> getArtDescription(ArtRecognition recognizedArt) {

        return getWikipediaPageHtml(recognizedArt)
                .thenApply(pageHtml -> {

                    String artName = recognizedArt.getArtName();
                    String artSummary = getArtSummary(pageHtml);
                    BasicArtDescription.ArtType artType = getArtType(recognizedArt);

                    if(artType == BasicArtDescription.ArtType.PAINTING || artType == BasicArtDescription.ArtType.SCULPTURE){

                        String parsedLocation = parseLocation(pageHtml);

                        String museumName = getMuseumFromLocation(parsedLocation);
                        String city = getCityFromLocation(parsedLocation);
                        String country = getCountryFromLocation(parsedLocation);
                        String year = getYear(pageHtml);
                        String artist = getArtist(pageHtml);

                        return new BasicArtDescription(artName, artist, artSummary, artType, year, city, country, museumName);
                    }

                    return new BasicArtDescription(artName, null, artSummary, artType, null, null, null, null);

                });
    }

    private CompletableFuture<String> getWikipediaPageHtml(ArtRecognition recognizedArt) {

        String wikipediaSearchUrl = wikipediaBaseUrl + recognizedArt.getArtName();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(wikipediaSearchUrl)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/110.0")
                .header("Accept-Language", "en-US,en;q=0.5")
                .build();

        CompletableFuture<String> pageHtmlFuture = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                pageHtmlFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()){
                    pageHtmlFuture.completeExceptionally(new IOException("Unexpected code " + response));
                }

                pageHtmlFuture.complete(response.body().string());
            }});

        return pageHtmlFuture;

    }
    private String getArtSummary(String pageHtml) {

        return shortenSummary(cleanHtml(getSummaryFromParsing(pageHtml)));
    }

    private String getSummaryFromParsing(String pageHTML) {

        String summaryRegex = "(<p>.*?<\\/p>){2}";
        Pattern pattern = Pattern.compile(summaryRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pageHTML);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private String cleanHtml(String html) {

        if (html == null) {
            return null;
        }

        String textFromHtml = Jsoup.parse(html).text();
        // Cleans the string by removing everything inside brackets, parentheses (as well as inner parentheses). Also removes double spaces and space before dot and comma.
        return textFromHtml.
                replaceAll("(\\([^)]*?){2}(\\)[^\\(]*?){2}","")         // removes everything inside 2 inner parentheses

                .replaceAll("\\[.*?]", "")        // removes everything inside brackets

                .replaceAll("\\(.*?\\)", "")        // removes everything inside parentheses

                .replaceAll("(?<=.)  (?=.)", " ")        // removes double spaces

                .replaceAll("(?<=.) (?=\\.)", "")        // removes space before dot

                .replaceAll("(?<=.) (?=,)", "");        // removes space before comma

    }

    private String shortenSummary(String summary) {

        if (summary == null) {
            return null;
        }

        // returns the first 4 sentences of the summary (separated by a dot)
        String[] sentences = summary.split("\\.(?![0-9])");
        if (sentences.length > 3) {
            return String.join(".", sentences[0], sentences[1], sentences[2]) + ".";
        }

        return summary;
    }

    private BasicArtDescription.ArtType getArtType(ArtRecognition recognizedArt) {

        String additionalInfo = recognizedArt.getAdditionalInfo();
        String firstWord = additionalInfo.split(" ")[0].toUpperCase();

        if (firstWord.equals("SCULPTURE")) {
            return BasicArtDescription.ArtType.SCULPTURE;
        } else if (firstWord.equals("PAINTING")) {
            return BasicArtDescription.ArtType.PAINTING;
        } else if (firstWord.equals("MONUMENT") || additionalInfo.equals("Cultural landmark") || additionalInfo.equals("Historic Landmark")) {
            return BasicArtDescription.ArtType.ARCHITECTURE;
        } else {
            return BasicArtDescription.ArtType.OTHER;
        }
    }

    private String parseLocation(String pageHtml){

        String locationRegex = "(?<=Location<\\/th>).*?<\\/td>";
        Pattern pattern = Pattern.compile(locationRegex);
        Matcher matcher = pattern.matcher(pageHtml);
        if (matcher.find()) {
            String locationHtml = matcher.group(0);
            String cleanedLocationText = cleanHtml(locationHtml);
            return cleanedLocationText;
        }

        return null;

    }

    private String getMuseumFromLocation(String parsedLocation){

        if (parsedLocation == null){
            return null;
        }

        return parsedLocation.split(",")[0];
    }

    private String getCityFromLocation(String parsedLocation){

        if (parsedLocation == null){
            return null;
        }

        String[] locationParts = parsedLocation.split(",");
        if (locationParts.length > 1){
            // removes the space at the beginning of the city name
            return locationParts[1].substring(1);
        }

        return null;
    }

    private String getCountryFromLocation(String parsedLocation) {

        if (parsedLocation == null) {
            return null;
        }

        String[] locationParts = parsedLocation.split(",");
        if (locationParts.length > 2) {
            return locationParts[2].substring(1);
        }

        return null;
    }

    private String getYear(String pageHtml){

        String yearRegex = "(?<=Year<\\/th>).*?<\\/td>";

        Pattern pattern = Pattern.compile(yearRegex);
        Matcher matcher = pattern.matcher(pageHtml);
        if (matcher.find()) {
            String yearHtml = matcher.group(0);
            String cleanedYearText = cleanHtml(yearHtml);

            // extracts all the numbers from the text and returns the last one

            String numberRegex = "\\d+";
            Pattern numberPattern = Pattern.compile(numberRegex);
            Matcher numberMatcher = numberPattern.matcher(cleanedYearText);
            String year = null;
            while (numberMatcher.find()){
                year = numberMatcher.group(0);
            }

            return year;
        }

        return null;
    }

    public String getArtist(String pageHtml){
        String artistRegex = "(?<=Artist<\\/th>).*?<\\/td>";

        Pattern pattern = Pattern.compile(artistRegex);
        Matcher matcher = pattern.matcher(pageHtml);
        if (matcher.find()) {
            String artistHtml = matcher.group(0);
            String cleanedArtistText = cleanHtml(artistHtml);
            return cleanedArtistText;
        }

        return null;
    }
}
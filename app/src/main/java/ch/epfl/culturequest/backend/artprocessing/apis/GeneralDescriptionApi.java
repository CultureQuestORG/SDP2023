package ch.epfl.culturequest.backend.artprocessing.apis;

import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.BuildConfig;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

/**
 * This class combines the results of the WikipediaDescriptionApi and the OpenAIDescriptionApi to get a complete description of the scanned art
 * If the art type is painting or sculpture, the WikipediaDescriptionApi is enough
 * If the art type is architecture or monument, the OpenAIDescriptionApi is used to get the missing data (artist, year, city, country)
 */

public class GeneralDescriptionApi {

    public static OpenAiService service = new OpenAiService(BuildConfig.OPEN_AI_API_KEY);

    public CompletableFuture<BasicArtDescription> getArtDescription(ArtRecognition recognizedArt){

        return new WikipediaDescriptionApi().getArtDescription(recognizedArt)
                .thenCompose(basicArtDescription -> {
                    BasicArtDescription.ArtType artType = basicArtDescription.getType();

                    OpenAIDescriptionApi openAIDescriptionApi = new OpenAIDescriptionApi(service);

                    CompletableFuture<Integer> score = openAIDescriptionApi.getScore(recognizedArt);

                    if (artType == BasicArtDescription.ArtType.PAINTING || artType == BasicArtDescription.ArtType.SCULPTURE) {
                        return score.thenApply(s -> {
                            basicArtDescription.setScore(s);
                            return basicArtDescription;
                        });
                    }
                    else {
                        // If the art type is architecture or monument, we use the OpenAI API to get the missing data (artist, year, city, country)

                        CompletableFuture<ArrayList<String>> missingData = openAIDescriptionApi.getMissingData(recognizedArt);
                        return missingData.thenCombine(score, (data, s) -> {

                            String artist = data.get(0);
                            String year = data.get(1);
                            String city = data.get(2);
                            String country = data.get(3);

                            basicArtDescription.setArtist(artist);
                            basicArtDescription.setYear(year);
                            basicArtDescription.setCity(city);
                            basicArtDescription.setCountry(country);
                            basicArtDescription.setScore(s);

                            return basicArtDescription;
                        });
                    }
                }
        );
    }


}

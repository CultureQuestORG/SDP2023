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
                    if (artType == BasicArtDescription.ArtType.PAINTING || artType == BasicArtDescription.ArtType.SCULPTURE) {
                        return CompletableFuture.completedFuture(basicArtDescription);
                    }
                    else {
                        // If the art type is architecture or monument, we use the OpenAI API to get the missing data (artist, year, city, country)

                        CompletableFuture<ArrayList<String>> missingData = new OpenAIDescriptionApi(service).getMissingData(recognizedArt);
                        return missingData.thenApply(d -> {
                            String artist = d.get(0);
                            String year = d.get(1);
                            String city = d.get(2);
                            String country = d.get(3);
                            return new BasicArtDescription(basicArtDescription.getName(), artist, basicArtDescription.getSummary(), artType, year, city, country, null);
                        });
                    }

                }
        );
    }


}

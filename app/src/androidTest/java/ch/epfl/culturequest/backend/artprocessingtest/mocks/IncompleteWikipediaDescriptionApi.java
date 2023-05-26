package ch.epfl.culturequest.backend.artprocessingtest.mocks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import ch.epfl.culturequest.backend.artprocessing.apis.WikipediaDescriptionApi;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.ArtRecognition;
import ch.epfl.culturequest.backend.artprocessing.processingobjects.BasicArtDescription;

// Mock that would provide incomplete basic art descriptions
public class IncompleteWikipediaDescriptionApi extends WikipediaDescriptionApi {

    private ArrayList<String> fieldsToBeNull;

    public IncompleteWikipediaDescriptionApi(String wikipediaBaseUrl) {
        super(wikipediaBaseUrl);
    }


    public void indicateFieldsToBeNull(ArrayList<String> fieldsToBeNull){

        this.fieldsToBeNull = fieldsToBeNull;
    }

    @Override
    public CompletableFuture<BasicArtDescription> getArtDescription(ArtRecognition artRecognition) {

        BasicArtDescription incompleteDescription = new BasicArtDescription();

        // Iterate over all the fields of the BasicArtDescription class
        // If the field is in the list of fields to be null, set it to null
        // Otherwise, set it to a random value

        Random random = new Random();
        Field[] fields = BasicArtDescription.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // You need to do this to access private fields
            if (fieldsToBeNull != null && fieldsToBeNull.contains(field.getName())) {
                try {
                    field.set(incompleteDescription, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {

                    if (field.getType() == String.class) {
                        field.set(incompleteDescription, "RandomValue" + random.nextInt(1000));
                    }

                    else if (field.getType() == Integer.class) {
                        field.set(incompleteDescription, random.nextInt());
                    }

                    else if(field.getType() == BasicArtDescription.ArtType.class) {
                        field.set(incompleteDescription, BasicArtDescription.ArtType.PAINTING);
                    }

                    else if(field.getType() == Boolean.class) {
                        field.set(incompleteDescription, false);
                    }

                    else {
                        field.set(incompleteDescription, null);
                    }
                }

                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return CompletableFuture.completedFuture(incompleteDescription);

    }
}

package ch.epfl.culturequest.backend.map_collection;

/**
 * A simple (de)serializer to transfer information of a location from one intent to another
 */
public class OTMLocationSerializer {

    public static String serialize(OTMLocation location) {
        //we dont care about the kinds for sightseeing
        OTMLatLng coordinates = location.getCoordinates();
        return location.getName()+"|"+coordinates.getLon()+"|"+coordinates.getLat()+"|"+location.getKindsList();
    }

    public static OTMLocation deserialize(String serialized) {
        String[] elements = serialized.split("\\|");
        String name = elements[0];
        OTMLatLng coord = new OTMLatLng(Double.parseDouble(elements[1]), Double.parseDouble(elements[2]));
        String kinds = elements[3];
        return new OTMLocation(name, coord, kinds);
    }
}



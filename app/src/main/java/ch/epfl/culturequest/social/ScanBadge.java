package ch.epfl.culturequest.social;

import androidx.annotation.NonNull;

import ch.epfl.culturequest.R;

public class ScanBadge {
    public interface Badge {

        int getBadge();

        static Badge identifyPlace(String s) {
            Country country = Country.fromString(s);
            if (country != Country.OTHER) {
                return country;
            }
            City city = City.fromString(s);
            if (city != City.OTHER) {
                return city;
            }
            Museum museum = Museum.fromString(s);
            if (museum != Museum.OTHER) {
                return museum;
            }
            return Country.OTHER;
        }
    }


    /**
     * Represents the country of the badge
     */
    public enum Country implements Badge {
        //List of supported countries
        AUSTRALIA, EGYPT, FRANCE, INDIA, ITALY, MEXICO, SWITZERLAND, USA, OTHER;

        /**
         * Returns the drawable resource id of the badge corresponding to the country
         *
         * @return (int) the resId of the drawable resource id of the badge corresponding to the country
         */
        public int getBadge() {
            switch (this) {
                case AUSTRALIA:
                    return R.drawable.australia;
                case EGYPT:
                    return R.drawable.egypt;
                case FRANCE:
                    return R.drawable.france;
                case INDIA:
                    return R.drawable.india;
                case ITALY:
                    return R.drawable.italia;
                case MEXICO:
                    return R.drawable.mexico;
                case SWITZERLAND:
                    return R.drawable.swiss;
                case USA:
                    return R.drawable.usa;
                default:
                    return R.drawable.placeholder_country;
            }
        }

        /**
         * Returns the Country corresponding to the string
         *
         * @param s (String) the string to convert
         * @return (Country) the Country corresponding to the string
         */
        public static Country fromString(String s) {
            switch (s.toUpperCase()) {
                case "AUSTRALIA":
                case "AUSTRALIE":
                case "COMMONWEALTH OF AUSTRALIA":
                    return AUSTRALIA;
                case "EGYPT":
                    return EGYPT;
                case "FRANCE":
                case "REPUBLIC OF FRANCE":
                    return FRANCE;
                case "INDIA":
                    return INDIA;
                case "ITALY":
                    return ITALY;
                case "MEXICO":
                    return MEXICO;
                case "SWITZERLAND":
                    return SWITZERLAND;
                case "USA":
                case "UNITED STATES":
                case "UNITED STATES OF AMERICA":
                    return USA;
                default:
                    return OTHER;
            }
        }

        public static String toUniqueString(String country){
            switch (country.toUpperCase()) {
                case "AUSTRALIA":
                case "AUSTRALIE":
                case "COMMONWEALTH OF AUSTRALIA":
                    return "Australia";
                case "EGYPT":
                    return "Egypt";
                case "FRANCE":
                case "REPUBLIC OF FRANCE":
                    return "France";
                case "INDIA":
                    return "India";
                case "ITALY":
                    return "Italy";
                case "MEXICO":
                    return "Mexico";
                case "SWITZERLAND":
                    return "Switzerland";
                case "USA":
                case "UNITED STATES":
                case "UNITED STATES OF AMERICA":
                    return "USA";
                default:
                    return country;
            }
        }
    }

    /**
     * Represents the city of the badge
     */
    public enum City implements Badge {
        //List of supported cities
        BARCELONA, BERLIN, GENEVA, LAUSANNE, LONDON, MADRID, MUNICH, NEW_YORK, PARIS, ROME, WASHINGTON_DC, OTHER;

        /**
         * Returns the drawable resource id of the badge corresponding to the city
         *
         * @return (int) the resId of the drawable resource id of the badge corresponding to the city
         */
        public int getBadge() {
            switch (this) {
                case BARCELONA:
                    return R.drawable.barcelona;
                case BERLIN:
                    return R.drawable.berlin;
                case GENEVA:
                    return R.drawable.geneva;
                case LAUSANNE:
                    return R.drawable.lausanne;
                case LONDON:
                    return R.drawable.london;
                case MADRID:
                    return R.drawable.madrid;
                case MUNICH:
                    return R.drawable.munich;
                case NEW_YORK:
                    return R.drawable.new_york;
                case PARIS:
                    return R.drawable.paris;
                case ROME:
                    return R.drawable.roma;
                case WASHINGTON_DC:
                    return R.drawable.washington;
                default:
                    return R.drawable.placeholder_city;
            }
        }

        /**
         * Returns the City corresponding to the string
         *
         * @param s (String) the string to convert
         * @return (City) the City corresponding to the string
         */
        public static City fromString(String s) {
            switch (s.toUpperCase()) {
                case "BARCELONA":
                    return BARCELONA;
                case "BERLIN":
                    return BERLIN;
                case "GENEVA":
                case "GENÈVE":
                    return GENEVA;
                case "LAUSANNE":
                    return LAUSANNE;
                case "LONDON":
                    return LONDON;
                case "MADRID":
                    return MADRID;
                case "MUNICH":
                case "MÜNCHEN":
                    return MUNICH;
                case "NEW YORK":
                    return NEW_YORK;
                case "PARIS":
                    return PARIS;
                case "ROME":
                case "ROMA":
                    return ROME;
                case "WASHINGTON, D.C.":
                case "WASHINGTON, DC":
                case "WASHINGTON":
                    return WASHINGTON_DC;
                default:
                    return OTHER;
            }
        }

        public static String toUniqueString(String city) {
            switch (city.toUpperCase()) {
                case "BARCELONA":
                    return "Barcelona";
                case "BERLIN":
                    return "Berlin";
                case "GENEVA":
                case "GENÈVE":
                    return "Geneva";
                case "LAUSANNE":
                    return "Lausanne";
                case "LONDON":
                    return "London";
                case "MADRID":
                    return "Madrid";
                case "MUNICH":
                case "MÜNCHEN":
                    return "Munich";
                case "NEW YORK":
                    return "New York";
                case "PARIS":
                    return "Paris";
                case "ROME":
                case "ROMA":
                    return "Rome";
                case "WASHINGTON, D.C.":
                case "WASHINGTON, DC":
                case "WASHINGTON":
                    return "Washington";
                default:
                    return city;
            }
        }
    }

    /**
     * Represents the museum of the badge
     */
    public enum Museum implements Badge {
        //List of supported museums
        BRITISH_MUSEUM, GUGGENHEIM, LOUVRE, MET, MOMA, VATICAN, OTHER;

        /**
         * Returns the drawable resource id of the badge corresponding to the museum
         *
         * @return (int) the resId of the drawable resource id of the badge corresponding to the museum
         */
        public int getBadge() {
            switch (this) {
                case BRITISH_MUSEUM:
                    return R.drawable.british_museum;
                case GUGGENHEIM:
                    return R.drawable.guggenheim;
                case LOUVRE:
                    return R.drawable.louvre;
                case MET:
                    return R.drawable.met;
                case MOMA:
                    return R.drawable.moma;
                case VATICAN:
                    return R.drawable.vatican;
                default:
                    return R.drawable.placeholder_museum;
            }
        }

        /**
         * Returns the Museum corresponding to the string
         *
         * @param s (String) the string to convert
         * @return (Museum) the Museum corresponding to the string
         */
        public static Museum fromString(String s) {
            switch (s.toUpperCase()) {
                case "BRITISH MUSEUM":
                case "BRITISH MUSEUM, LONDON":
                    return BRITISH_MUSEUM;
                case "GUGGENHEIM MUSEUM":
                case "GUGGENHEIM":
                    return GUGGENHEIM;
                case "LOUVRE":
                case "MUSÉE DU LOUVRE":
                    return LOUVRE;
                case "METROPOLITAN MUSEUM OF ART":
                case "METROPOLITAN MUSEUM":
                case "MET":
                    return MET;
                case "MUSEUM OF MODERN ART":
                case "MOMA":
                    return MOMA;
                case "VATICAN MUSEUMS":
                case "VATICAN":
                case "MUSEI VATICANI":
                    return VATICAN;
                default:
                    return OTHER;
            }
        }

        public static String toUniqueString(String museum){
            switch (museum.toUpperCase()){
                case "BRITISH MUSEUM":
                case "BRITISH MUSEUM, LONDON":
                    return "British Museum";
                case "GUGGENHEIM MUSEUM":
                case "GUGGENHEIM":
                    return "Guggenheim Museum";
                case "LOUVRE":
                case "MUSÉE DU LOUVRE":
                    return "Musée du Louvre";
                case "METROPOLITAN MUSEUM OF ART":
                case "METROPOLITAN MUSEUM":
                case "MET":
                    return "Metropolitan Museum of Art";
                case "MUSEUM OF MODERN ART":
                case "MOMA":
                    return "Museum of Modern Art";
                case "VATICAN MUSEUMS":
                case "VATICAN":
                case "MUSEI VATICANI":
                    return "Vatican Museums";
                default:
                    return museum;
            }
        }
    }
}

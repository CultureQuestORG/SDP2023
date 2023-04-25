package ch.epfl.culturequest.social;

import ch.epfl.culturequest.R;

public class ScanBadge {
    /**
     * Represents the country of the badge
     */
    public enum Country {
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
            switch (s) {
                case "Australia":
                case "Australie":
                case "australie":
                case "Commonwealth of Australia":
                    return AUSTRALIA;
                case "Egypt":
                    return EGYPT;
                case "France":
                case "france":
                case "FRANCE":
                case "Republic of France":
                    return FRANCE;
                case "India":
                    return INDIA;
                case "Italy":
                    return ITALY;
                case "Mexico":
                    return MEXICO;
                case "Switzerland":
                    return SWITZERLAND;
                case "USA":
                case "United States":
                case "United States of America":
                    return USA;
                default:
                    return OTHER;
            }
        }
    }

    /**
     * Represents the city of the badge
     */
    public enum City {
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
            switch (s) {
                case "Barcelona":
                    return BARCELONA;
                case "Berlin":
                    return BERLIN;
                case "Geneva":
                case "Genève":
                    return GENEVA;
                case "Lausanne":
                    return LAUSANNE;
                case "London":
                    return LONDON;
                case "Madrid":
                    return MADRID;
                case "Munich":
                case "München":
                    return MUNICH;
                case "New York":
                    return NEW_YORK;
                case "Paris":
                    return PARIS;
                case "Rome":
                case "Roma":
                    return ROME;
                case "Washington, D.C.":
                case "Washington, DC":
                case "Washington":
                    return WASHINGTON_DC;
                default:
                    return OTHER;
            }
        }
    }

    /**
     * Represents the museum of the badge
     */
    public enum Museum {
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
            switch (s) {
                case "British Museum":
                case "British Museum, London":
                    return BRITISH_MUSEUM;
                case "Guggenheim":
                case "Guggenheim Museum":
                    return GUGGENHEIM;
                case "Louvre":
                case "Musée du Louvre":
                    return LOUVRE;
                case "Metropolitan Museum of Art":
                case "Metropolitan Museum":
                case "Met":
                case "MET":
                    return MET;
                case "Museum of Modern Art":
                case "MoMA":
                case "MOMA":
                    return MOMA;
                case "Vatican Museums":
                case "Vatican":
                case "Musei Vaticani":
                    return VATICAN;
                default:
                    return OTHER;
            }
        }
    }
}

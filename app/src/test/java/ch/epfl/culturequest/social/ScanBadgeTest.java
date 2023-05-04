package ch.epfl.culturequest.social;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.culturequest.R;

public class ScanBadgeTest {

    @Test
    public void getCountryBadgeWorks() {
        assertThat(ScanBadge.Country.AUSTRALIA.getBadge(), is(R.drawable.australia));
        assertThat(ScanBadge.Country.EGYPT.getBadge(), is(R.drawable.egypt));
        assertThat(ScanBadge.Country.FRANCE.getBadge(), is(R.drawable.france));
        assertThat(ScanBadge.Country.ITALY.getBadge(), is(R.drawable.italia));
        assertThat(ScanBadge.Country.INDIA.getBadge(), is(R.drawable.india));
        assertThat(ScanBadge.Country.MEXICO.getBadge(), is(R.drawable.mexico));
        assertThat(ScanBadge.Country.SWITZERLAND.getBadge(), is(R.drawable.swiss));
        assertThat(ScanBadge.Country.USA.getBadge(), is(R.drawable.usa));
        assertThat(ScanBadge.Country.OTHER.getBadge(), is(R.drawable.placeholder_country));
    }

    @Test
    public void fromCountryStringWorks() {
        assertThat(ScanBadge.Country.fromString("Australia"), is(ScanBadge.Country.AUSTRALIA));
        assertThat(ScanBadge.Country.fromString("Australie"), is(ScanBadge.Country.AUSTRALIA));
        assertThat(ScanBadge.Country.fromString("australie"), is(ScanBadge.Country.AUSTRALIA));
        assertThat(ScanBadge.Country.fromString("Commonwealth of Australia"), is(ScanBadge.Country.AUSTRALIA));
        assertThat(ScanBadge.Country.fromString("Egypt"), is(ScanBadge.Country.EGYPT));
        assertThat(ScanBadge.Country.fromString("France"), is(ScanBadge.Country.FRANCE));
        assertThat(ScanBadge.Country.fromString("france"), is(ScanBadge.Country.FRANCE));
        assertThat(ScanBadge.Country.fromString("FRANCE"), is(ScanBadge.Country.FRANCE));
        assertThat(ScanBadge.Country.fromString("Republic of France"), is(ScanBadge.Country.FRANCE));
        assertThat(ScanBadge.Country.fromString("Italy"), is(ScanBadge.Country.ITALY));
        assertThat(ScanBadge.Country.fromString("India"), is(ScanBadge.Country.INDIA));
        assertThat(ScanBadge.Country.fromString("Mexico"), is(ScanBadge.Country.MEXICO));
        assertThat(ScanBadge.Country.fromString("Switzerland"), is(ScanBadge.Country.SWITZERLAND));
        assertThat(ScanBadge.Country.fromString("USA"), is(ScanBadge.Country.USA));
        assertThat(ScanBadge.Country.fromString("United States"), is(ScanBadge.Country.USA));
        assertThat(ScanBadge.Country.fromString("United States of America"), is(ScanBadge.Country.USA));
        assertThat(ScanBadge.Country.fromString("Other"), is(ScanBadge.Country.OTHER));
    }

    @Test
    public void getCityBadgeWorks() {
        assertThat(ScanBadge.City.BARCELONA.getBadge(), is(R.drawable.barcelona));
        assertThat(ScanBadge.City.BERLIN.getBadge(), is(R.drawable.berlin));
        assertThat(ScanBadge.City.GENEVA.getBadge(), is(R.drawable.geneva));
        assertThat(ScanBadge.City.LAUSANNE.getBadge(), is(R.drawable.lausanne));
        assertThat(ScanBadge.City.LONDON.getBadge(), is(R.drawable.london));
        assertThat(ScanBadge.City.MADRID.getBadge(), is(R.drawable.madrid));
        assertThat(ScanBadge.City.MUNICH.getBadge(), is(R.drawable.munich));
        assertThat(ScanBadge.City.NEW_YORK.getBadge(), is(R.drawable.new_york));
        assertThat(ScanBadge.City.PARIS.getBadge(), is(R.drawable.paris));
        assertThat(ScanBadge.City.ROME.getBadge(), is(R.drawable.roma));
        assertThat(ScanBadge.City.WASHINGTON_DC.getBadge(), is(R.drawable.washington));
        assertThat(ScanBadge.City.OTHER.getBadge(), is(R.drawable.placeholder_city));
    }

    @Test
    public void fromCityStringWorks() {
        assertThat(ScanBadge.City.fromString("Barcelona"), is(ScanBadge.City.BARCELONA));
        assertThat(ScanBadge.City.fromString("Berlin"), is(ScanBadge.City.BERLIN));
        assertThat(ScanBadge.City.fromString("Geneva"), is(ScanBadge.City.GENEVA));
        assertThat(ScanBadge.City.fromString("Genève"), is(ScanBadge.City.GENEVA));
        assertThat(ScanBadge.City.fromString("Lausanne"), is(ScanBadge.City.LAUSANNE));
        assertThat(ScanBadge.City.fromString("London"), is(ScanBadge.City.LONDON));
        assertThat(ScanBadge.City.fromString("Madrid"), is(ScanBadge.City.MADRID));
        assertThat(ScanBadge.City.fromString("Munich"), is(ScanBadge.City.MUNICH));
        assertThat(ScanBadge.City.fromString("München"), is(ScanBadge.City.MUNICH));
        assertThat(ScanBadge.City.fromString("New York"), is(ScanBadge.City.NEW_YORK));
        assertThat(ScanBadge.City.fromString("Paris"), is(ScanBadge.City.PARIS));
        assertThat(ScanBadge.City.fromString("Rome"), is(ScanBadge.City.ROME));
        assertThat(ScanBadge.City.fromString("Roma"), is(ScanBadge.City.ROME));
        assertThat(ScanBadge.City.fromString("Washington, D.C."), is(ScanBadge.City.WASHINGTON_DC));
        assertThat(ScanBadge.City.fromString("Washington, DC"), is(ScanBadge.City.WASHINGTON_DC));
        assertThat(ScanBadge.City.fromString("Washington"), is(ScanBadge.City.WASHINGTON_DC));
        assertThat(ScanBadge.City.fromString("Milan"), is(ScanBadge.City.OTHER));
    }

    @Test
    public void getMuseumBadgeWorks() {
        assertThat(ScanBadge.Museum.BRITISH_MUSEUM.getBadge(), is(R.drawable.british_museum));
        assertThat(ScanBadge.Museum.GUGGENHEIM.getBadge(), is(R.drawable.guggenheim));
        assertThat(ScanBadge.Museum.LOUVRE.getBadge(), is(R.drawable.louvre));
        assertThat(ScanBadge.Museum.MET.getBadge(), is(R.drawable.met));
        assertThat(ScanBadge.Museum.MOMA.getBadge(), is(R.drawable.moma));
        assertThat(ScanBadge.Museum.VATICAN.getBadge(), is(R.drawable.vatican));
        assertThat(ScanBadge.Museum.OTHER.getBadge(), is(R.drawable.placeholder_museum));
    }

    @Test
    public void fromMuseumStringWorks() {
        assertThat(ScanBadge.Museum.fromString("British Museum"), is(ScanBadge.Museum.BRITISH_MUSEUM));
        assertThat(ScanBadge.Museum.fromString("British Museum, London"), is(ScanBadge.Museum.BRITISH_MUSEUM));
        assertThat(ScanBadge.Museum.fromString("Guggenheim"), is(ScanBadge.Museum.GUGGENHEIM));
        assertThat(ScanBadge.Museum.fromString("Guggenheim Museum"), is(ScanBadge.Museum.GUGGENHEIM));
        assertThat(ScanBadge.Museum.fromString("Louvre"), is(ScanBadge.Museum.LOUVRE));
        assertThat(ScanBadge.Museum.fromString("Musée du Louvre"), is(ScanBadge.Museum.LOUVRE));
        assertThat(ScanBadge.Museum.fromString("Metropolitan Museum of Art"), is(ScanBadge.Museum.MET));
        assertThat(ScanBadge.Museum.fromString("Metropolitan Museum"), is(ScanBadge.Museum.MET));
        assertThat(ScanBadge.Museum.fromString("Met"), is(ScanBadge.Museum.MET));
        assertThat(ScanBadge.Museum.fromString("MET"), is(ScanBadge.Museum.MET));
        assertThat(ScanBadge.Museum.fromString("Museum of Modern Art"), is(ScanBadge.Museum.MOMA));
        assertThat(ScanBadge.Museum.fromString("MoMA"), is(ScanBadge.Museum.MOMA));
        assertThat(ScanBadge.Museum.fromString("MOMA"), is(ScanBadge.Museum.MOMA));
        assertThat(ScanBadge.Museum.fromString("Vatican"), is(ScanBadge.Museum.VATICAN));
        assertThat(ScanBadge.Museum.fromString("Musei Vaticani"), is(ScanBadge.Museum.VATICAN));
        assertThat(ScanBadge.Museum.fromString("Vatican Museums"), is(ScanBadge.Museum.VATICAN));
        assertThat(ScanBadge.Museum.fromString("National Gallery"), is(ScanBadge.Museum.OTHER));
    }

    @Test
    public void identifyPlaceWorks(){
        // test for a country
        assertThat(ScanBadge.Badge.identifyPlace("France"), is(ScanBadge.Country.FRANCE));
        assertThat(ScanBadge.Badge.identifyPlace("paris"), is(ScanBadge.City.PARIS));
        assertThat(ScanBadge.Badge.identifyPlace("Louvre"), is(ScanBadge.Museum.LOUVRE));

    }

}
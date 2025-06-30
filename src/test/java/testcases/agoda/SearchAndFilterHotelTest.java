package testcases.agoda;

import data.models.agoda.Hotel;
import data.models.agoda.PriceFilter;
import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.agoda.HomePage;
import pages.agoda.SearchResultsPage;
import testcases.TestBase;
import testdata.AgodaTestData;
import utils.HotelFilters;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class SearchAndFilterHotelTest extends TestBase {

    SoftAssert softAssert;
    HomePage homePage;
    SearchResultsPage searchResultsPage;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    PriceFilter defaulPriceFilter;
    PriceFilter actualPriceFilter;
    List<Hotel> hotels;
    List<Hotel> filteredHotels;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL(); // Navigate to https://www.agoda.com/
        softAssert = new SoftAssert();
        homePage = new HomePage();
        searchResultsPage = new SearchResultsPage();
    }

    @Test(dataProvider = "dataByMethod", groups = {"smoke", "regression"}, description = "Search and filter hotel successfully")
    public void searchAndFilterHotel(AgodaTestData data) {

        checkInDate = LocalDate.now().with(TemporalAdjusters.next(data.getCheckIn()));
        checkOutDate = checkInDate.plusDays(data.getCheckOut());

        /* Search the hotel with the following information:
            - Place: Da Nang
            - Date: 3 days from next Friday
            - Number of people: Family Travelers -> 2 rooms and 4 adults
        */
        homePage.searchHotel(data.getLocation(),
                checkInDate,
                checkOutDate,
                data.getOccupancy());

        defaulPriceFilter = searchResultsPage.getPriceFilterValues(); // get default price filter to reset later

        // Search result is displayed correctly with first 5 hotels(destination).
        hotels = searchResultsPage.getHotels(data.getResultCount());
        filteredHotels = new HotelFilters(hotels)
                .filterByDestination(data.getLocation())
                .get();
        softAssert.assertEquals(hotels, filteredHotels);

        /* Filter the hotels with the following info:
            - Price: 500000-1000000VND
            - Star:3
         */
        searchResultsPage.filterPrice(data.getPriceFilter());
        searchResultsPage.filterStar(data.getRating());

        // The price and star filtered is highlighted
        actualPriceFilter = searchResultsPage.getPriceFilterValues();
        softAssert.assertEquals(actualPriceFilter, data.getPriceFilter());
        softAssert.assertTrue(searchResultsPage.isStarRatingSelected(data.getRating()));

        // Search Result is displayed correctly with first 5 hotels(destination, price, star).
        hotels = searchResultsPage.getHotels(data.getResultCount());
        filteredHotels = new HotelFilters(hotels)
                .filterByDestination(data.getLocation())
                .filterByRating(data.getRating())
                .filterByPriceRange(data.getPriceFilter())
                .get();
        softAssert.assertEquals(hotels, filteredHotels);

        // Remove price filter
        searchResultsPage.filterPrice(defaulPriceFilter);

        // The price slice is reset
        actualPriceFilter = searchResultsPage.getPriceFilterValues();
        softAssert.assertEquals(actualPriceFilter, defaulPriceFilter);

        softAssert.assertAll();
    }
}

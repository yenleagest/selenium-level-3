package testcases.agoda;

import data.models.agoda.Hotel;
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

public class SearchAndSortHotelTest extends TestBase {

    SoftAssert softAssert;
    HomePage homePage;
    SearchResultsPage searchResultsPage;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    List<Hotel> hotels;
    List<Hotel> filteredHotels;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();  // Navigate to https://www.agoda.com/
        softAssert = new SoftAssert();
        homePage = new HomePage();
        searchResultsPage = new SearchResultsPage();
    }

    @Test(dataProvider = "dataByMethod", groups = {"smoke", "regression"}, description = "Search and sort hotel successfully")
    public void searchAndSortHotel(AgodaTestData data) {

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

        // Search result is displayed correctly with first 5 hotels(destination).
        hotels = searchResultsPage.getHotels(data.getResultCount());
        filteredHotels = new HotelFilters(hotels)
                .filterByDestination(data.getLocation())
                .get();
        softAssert.assertEquals(hotels, filteredHotels);

        // Sort hotels by lowest prices.
        searchResultsPage.sortResultsBy(data.getSortBy());

        // 5 first hotels are sorted with the right order and the hotel destination is still correct.
        hotels = searchResultsPage.getHotels(data.getResultCount());
        filteredHotels = new HotelFilters(hotels)
                .filterByDestination(data.getLocation())
                .sortByPrice()
                .get();
        softAssert.assertEquals(hotels, filteredHotels);

        softAssert.assertAll();
    }
}

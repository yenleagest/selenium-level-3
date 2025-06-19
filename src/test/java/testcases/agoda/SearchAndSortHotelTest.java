package testcases.agoda;

import actions.HomePageActions;
import data.enums.SortBy;
import data.models.CardContainer;
import data.models.Occupancy;
import drivers.DriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.agoda.HomePage;
import pages.agoda.SearchResultsPage;
import testcases.TestBase;
import testdata.AgodaTestData;

import java.util.List;

@Slf4j
public class SearchAndSortHotelTest extends TestBase {

    SoftAssert softAssert;
    HomePage homePage;
    SearchResultsPage searchResultsPage;
    HomePageActions homePageActions;
    Occupancy occupancy;
    String location;
    int resultCount;
    List<CardContainer> hotels;
    List<CardContainer> filteredHotels;
    List<Integer> prices;
    List<Integer> sortedPrices;
    List<String> destinations;
    List<String> filteredDestinations;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();  // Navigate to https://www.agoda.com/
        softAssert = new SoftAssert();
        homePage = new HomePage();
        homePageActions = new HomePageActions(homePage);
        searchResultsPage = new SearchResultsPage();
    }

    @Test(dataProvider = "dataByMethod", groups = {"smoke", "regression"}, description = "Search and sort hotel successfully")
    public void searchAndSortHotel(AgodaTestData data) {
        location = data.getLocation();
        resultCount = data.getResultCount();

        /* Search the hotel with the following information:
            - Place: Da Nang
            - Date: 3 days from next Friday
            - Number of people: Family Travelers -> 2 rooms and 4 adults
        */
        homePage.searchHotel(location,
                data.getWeekday(),
                data.getDuration(),
                data.getOccupancy());

        // Search result is displayed correctly with first 5 hotels(destination).
        hotels = searchResultsPage.getHotels(resultCount);
        filteredHotels = CardContainer.filteredHotels(hotels, location, false, 0);
        softAssert.assertEquals(hotels, filteredHotels, "Not all results contain '%s': %s".formatted(location, hotels));

        // Sort hotels by lowest prices.
        searchResultsPage.sortResultsBy(SortBy.fromString(data.getSortBy()));

        // 5 first hotels are sorted with the right order and the hotel destination is still correct.
        hotels = searchResultsPage.getHotels(resultCount);
        filteredHotels = CardContainer.filteredHotels(hotels, location, true, 0);
        softAssert.assertEquals(hotels, filteredHotels, "Either results are not sorted by lowest price or not all results contain '%s' : %s".formatted(location, hotels));

        softAssert.assertAll();
    }
}

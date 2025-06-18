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
import utils.CollectionUtils;
import utils.DateUtils.WeekOffset;
import utils.DateUtils.Weekday;

import java.util.HashMap;
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
    public void searchAndSortHotel(HashMap<String, String> data) {
        occupancy = new Occupancy(
                Integer.parseInt(data.get("rooms")),
                Integer.parseInt(data.get("adults")),
                Integer.parseInt(data.get("children")));
        location = data.get("location");
        resultCount = Integer.parseInt(data.get("resultCount"));

        /* Search the hotel with the following information:
            - Place: Da Nang
            - Date: 3 days from next Friday
            - Number of people: Family Travelers -> 2 rooms and 4 adults
        */
        homePage.searchHotel(
                location,
                WeekOffset.fromString(data.get("weekOffset")),
                Weekday.fromString(data.get("weekday")),
                Integer.parseInt(data.get("duration")),
                occupancy);

        // Search result is displayed correctly with first 5 hotels(destination).
        hotels = searchResultsPage.getHotels(resultCount);
        destinations = CollectionUtils.getDestinations(hotels);
        filteredDestinations = CollectionUtils.filterByKeyword(destinations, location);
        softAssert.assertEquals(destinations, filteredDestinations, "Not all results contain '%s': %s".formatted(location, destinations));

        // Sort hotels by lowest prices.
        searchResultsPage.sortResultsBy(SortBy.fromString(data.get("sortBy")));

        // 5 first hotels are sorted with the right order.
        hotels = searchResultsPage.getHotels(resultCount);
        prices = CollectionUtils.getPrices(hotels);
        sortedPrices = CollectionUtils.sortAscending(prices);
        softAssert.assertEquals(prices, sortedPrices, "Results are not sorted by lowest price first: %s".formatted(prices));

        // The hotel destination is still correct.
        destinations = CollectionUtils.getDestinations(hotels);
        filteredDestinations = CollectionUtils.filterByKeyword(destinations, location);
        softAssert.assertEquals(destinations, filteredDestinations, "Not all results contain '%s': %s".formatted(location, destinations));

        softAssert.assertAll();
    }
}

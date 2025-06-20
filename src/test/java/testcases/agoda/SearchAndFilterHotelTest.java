package testcases.agoda;

import data.models.CardContainer;
import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.agoda.HomePage;
import pages.agoda.SearchResultsPage;
import testcases.TestBase;
import testdata.AgodaTestData;

import java.util.Collections;
import java.util.List;

public class SearchAndFilterHotelTest extends TestBase {

    SoftAssert softAssert;
    HomePage homePage;
    SearchResultsPage searchResultsPage;
    String location;
    int rating;
    int resultCount;
    int minPrice;
    int maxPrice;
    List<CardContainer> hotels;
    List<CardContainer> filteredHotels;
    List<Integer> defaultPriceFilter;
    List<Integer> actualPriceFilter;
    List<Integer> inputPriceFilter;
    List<String> destinations;
    List<String> filteredDestinations;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL(); // Navigate to https://www.agoda.com/
        softAssert = new SoftAssert();
        homePage = new HomePage();
        searchResultsPage = new SearchResultsPage();
    }

    @Test(dataProvider = "dataByMethod", groups = {"smoke", "regression"}, description = "Search and filter hotel successfully")
    public void searchAndFilterHotel(AgodaTestData data) {
        location = data.getLocation();
        resultCount = data.getResultCount();
        rating = data.getRating();
        minPrice = data.getMinPrice();
        maxPrice = data.getMaxPrice();
        inputPriceFilter = List.of(minPrice, maxPrice);

        /* Search the hotel with the following information:
            - Place: Da Nang
            - Date: 3 days from next Friday
            - Number of people: Family Travelers -> 2 rooms and 4 adults
        */
        homePage.searchHotel(location,
                data.getWeekday(),
                data.getDuration(),
                data.getOccupancy());

        defaultPriceFilter = searchResultsPage.getPriceFilterValues(); // get default price filter to reset later

        // Search result is displayed correctly with first 5 hotels(destination).
        hotels = searchResultsPage.getHotels(resultCount);
        filteredHotels = CardContainer.filteredHotels(hotels, location, false, 0);
        softAssert.assertEquals(destinations, filteredDestinations, "Not all results contain '%s': %s".formatted(location, hotels));

        /* Filter the hotels with the following info:
            - Price: 500000-1000000VND
            - Star:3
         */
        searchResultsPage.filterPrice(minPrice, maxPrice);
        searchResultsPage.filterStar(rating);

        // The price and star filtered is highlighted
        actualPriceFilter = searchResultsPage.getPriceFilterValues();
        softAssert.assertEquals(actualPriceFilter, inputPriceFilter, "Price filter does not match\n - Expected: %s\n - Actual: %s".formatted(inputPriceFilter, actualPriceFilter));
        softAssert.assertTrue(searchResultsPage.isStarRatingSelected(rating), "Star rating %s is not selected".formatted(rating));

        // Search Result is displayed correctly with first 5 hotels(destination, price, star).
        hotels = searchResultsPage.getHotels(resultCount);
        filteredHotels = CardContainer.filteredHotels(hotels, location, false, rating, minPrice, maxPrice);
        softAssert.assertEquals(hotels, filteredHotels, "Not all results match condition: %s".formatted(hotels));

        // Remove price filter
        searchResultsPage.filterPrice(Collections.min(defaultPriceFilter), Collections.max(defaultPriceFilter));

        // The price slice is reset
        actualPriceFilter = searchResultsPage.getPriceFilterValues();
        softAssert.assertEquals(actualPriceFilter, defaultPriceFilter, "Price filter is not reset: %s".formatted(actualPriceFilter));

        softAssert.assertAll();
    }
}


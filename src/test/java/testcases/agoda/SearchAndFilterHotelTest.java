package testcases.agoda;

import actions.HomePageActions;
import data.models.CardContainer;
import data.models.Occupancy;
import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.agoda.HomePage;
import pages.agoda.SearchResultsPage;
import testcases.TestBase;
import utils.CollectionUtils;
import utils.CurrencyConverter;
import utils.DateUtils.WeekOffset;
import utils.DateUtils.Weekday;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SearchAndFilterHotelTest extends TestBase {

    SoftAssert softAssert;
    HomePage homePage;
    SearchResultsPage searchResultsPage;
    HomePageActions homePageActions;
    Occupancy occupancy;
    String location;
    String currencySymbol;
    int rating;
    int resultCount;
    int minPrice;
    int maxPrice;
    List<CardContainer> hotels;
    List<Integer> defaultPriceRange;
    List<Integer> actualPriceFilter;
    List<Integer> priceFilter;
    List<Integer> prices;
    List<Integer> sortedPrices;
    List<String> destinations;
    List<String> filteredDestinations;
    List<Integer> ratings;
    List<Integer> filteredRatings;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();
        softAssert = new SoftAssert();
        homePage = new HomePage();
        homePageActions = new HomePageActions(homePage);
        searchResultsPage = new SearchResultsPage();
    }

    @Test(dataProvider = "dataByMethod", groups = {"smoke", "regression"}, description = "Search and filter hotel successfully")
    public void searchAndFilterHotel(HashMap<String, String> data) {
        // remove cast
        occupancy = new Occupancy(Integer.parseInt(data.get("rooms")), Integer.parseInt(data.get("adults")), Integer.parseInt(data.get("children")));
        location = data.get("location");
        resultCount = Integer.parseInt(data.get("resultCount"));
        rating = Integer.parseInt(data.get("starRating"));

        /* Search the hotel with the following information:
            - Place: Da Nang
            - Date: 3 days from next Friday
            - Number of people: Family Travelers -> 2 rooms and 4 adults
        */
        homePage.searchHotel(location,
                WeekOffset.fromString(data.get("weekOffset")),
                Weekday.fromString(data.get("weekday")),
                Integer.parseInt(data.get("duration")),
                occupancy);

        defaultPriceRange = searchResultsPage.getPriceFilterValues(); // get default price filter to reset later

        // Search result is displayed correctly with first 5 hotels(destination).
        hotels = searchResultsPage.getHotels(resultCount);
        destinations = CollectionUtils.getDestinations(hotels);
        filteredDestinations = CollectionUtils.filterByKeyword(destinations, location);
        softAssert.assertEquals(destinations, filteredDestinations, "Not all results contain '%s': %s".formatted(location, destinations));

        // agoda detect server location and automatically set the currency
        currencySymbol = searchResultsPage.getCurrencySymbol();
        minPrice = CurrencyConverter.convert(Integer.parseInt(data.get("minPrice")), currencySymbol);
        maxPrice = CurrencyConverter.convert(Integer.parseInt(data.get("maxPrice")), currencySymbol);
        priceFilter = List.of(minPrice, maxPrice);

        /* Filter the hotels with the following info:
            - Price: 500000-1000000VND
            - Star:3
         */
        searchResultsPage.filterPrice(minPrice, maxPrice);
        searchResultsPage.filterStar(rating);

        // The price and star filtered is highlighted
        actualPriceFilter = searchResultsPage.getPriceFilterValues();
        softAssert.assertEquals(actualPriceFilter, priceFilter, "Price filter does not match\n - Expected: %s\n - Actual: %s".formatted(priceFilter, actualPriceFilter));
        softAssert.assertTrue(searchResultsPage.isStarRatingSelected(rating), "Star rating %s is not selected".formatted(rating));

        // Search Result is displayed correctly with first 5 hotels(destination, price, star).
        hotels = searchResultsPage.getHotels(resultCount);
        prices = CollectionUtils.getPrices(hotels);
        sortedPrices = CollectionUtils.filterByRange(prices, minPrice, maxPrice);
        softAssert.assertEquals(prices, sortedPrices, "Not all results contain prices in the range %s - %s:\n%s".formatted(minPrice, maxPrice, prices));

        destinations = CollectionUtils.getDestinations(hotels);
        filteredDestinations = CollectionUtils.filterByKeyword(destinations, location);
        softAssert.assertEquals(destinations, filteredDestinations, "Not all results contain '%s':\n%s".formatted(location, destinations));

        ratings = CollectionUtils.getRatings(hotels);
        filteredRatings = CollectionUtils.filterByKeyword(ratings, String.valueOf(rating));
        softAssert.assertEquals(ratings, filteredRatings, "Not all results have '%s' star rating:\n%s".formatted(rating, filteredRatings));

        // Remove price filter
        searchResultsPage.filterPrice(Collections.min(defaultPriceRange), Collections.max(defaultPriceRange));

        // The price slice is reset
        actualPriceFilter = searchResultsPage.getPriceFilterValues();
        softAssert.assertNotEquals(actualPriceFilter, priceFilter, "Price filter is not reset: %s".formatted(actualPriceFilter));

        softAssert.assertAll();
    }
}


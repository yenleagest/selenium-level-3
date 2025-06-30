package testcases.agoda;

import data.models.agoda.Hotel;
import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.agoda.HomePage;
import pages.agoda.HotelDetailsPage;
import pages.agoda.SearchResultsPage;
import testcases.TestBase;
import testdata.AgodaTestData;
import utils.HotelFilters;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class HotelDetailsPageTest extends TestBase {

    SoftAssert softAssert;
    HomePage homePage;
    SearchResultsPage searchResultsPage;
    HotelDetailsPage hotelDetailsPage;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    List<Hotel> hotels;
    Hotel hotelDetails;
    List<Hotel> filteredHotels;
    List<String> reviewDetails;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL(); // Navigate to https://www.agoda.com/
        softAssert = new SoftAssert();
        homePage = new HomePage();
        searchResultsPage = new SearchResultsPage();
        hotelDetailsPage = new HotelDetailsPage();
    }

    @Test(dataProvider = "dataByMethod", groups = {"smoke", "regression"}, description = "Hotel details page is displayed with correct information")
    public void hotelDetailsPage(AgodaTestData data) {

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

        // Filter the non-smoking hotels and choose the 5th hotel in the list
        searchResultsPage.filterByFacility(data.getFacility());
        hotels = searchResultsPage.getHotels(data.getResultCount());
        searchResultsPage.goToHotelDetailsPage(hotels.getLast().getName());

        // The hotel detailed page is displayed with correct info
        hotelDetails = hotelDetailsPage.getHotel();
        softAssert.assertTrue(hotels.getLast().equals(hotelDetails));
        softAssert.assertTrue(hotelDetails.getBenefits().containsAll(data.getBenefits()));

        // Move mouse to point of the hotel to show detailed review points
        reviewDetails = hotelDetailsPage.showReviewDetails();

        // Detailed review popup appears and show the following information:
        // Cleanliness, Facilities, Service, Location, Value for money
        softAssert.assertTrue(reviewDetails.containsAll(data.getReviewDetails()));

        // Back to the search result page and choose the first hotel
        DriverUtils.closeCurrentTab();
        searchResultsPage.goToHotelDetailsPage(hotels.getFirst().getName());

        // The hotel detailed page is displayed with correct info
        hotelDetails = hotelDetailsPage.getHotel();
        softAssert.assertTrue(hotels.getFirst().equals(hotelDetails));
        softAssert.assertTrue(hotelDetails.getBenefits().containsAll(data.getBenefits()));

        softAssert.assertAll();
    }
}

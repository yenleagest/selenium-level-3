package testcases.vj;

import data.models.vj.FlightInfo;
import data.models.vj.Passenger;
import data.models.vj.Ticket;
import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.vj.HomePage;
import pages.vj.PassengerInformationPage;
import pages.vj.SelectFlightOptionsPage;
import testcases.TestBase;
import testdata.VJTestData;

import java.time.LocalDate;

public class SearchTicketsOnASpecificDayTest extends TestBase {

    HomePage homePage;
    SelectFlightOptionsPage SelectFlightOptionsPage;
    PassengerInformationPage passengerInformationPage;
    SoftAssert softAssert;
    Ticket ticket;
    FlightInfo departureFlight;
    FlightInfo returnFlight;
    Passenger passenger;
    LocalDate departureDate;
    LocalDate returnDate;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();
        homePage = new HomePage();
        SelectFlightOptionsPage = new SelectFlightOptionsPage();
        passengerInformationPage = new PassengerInformationPage();
        softAssert = new SoftAssert();
    }

    @Test(dataProvider = "dataByMethodLocale", groups = {"smoke", "regression"}, description = "Search and choose tickets on a specific day successfully")
    public void searchTicketsOnASpecificDay(VJTestData data) {

        departureDate = LocalDate.now().plusDays(1);
        returnDate = departureDate.plusDays(3);
        passenger = new Passenger(2, 0, 0);
        departureFlight = new FlightInfo("VND", data.getDepartureAirport(), data.getDestinationAirport(), departureDate, passenger);
        returnFlight = new FlightInfo("VND", data.getDestinationAirport(), data.getDepartureAirport(), returnDate, passenger);
        ticket = new Ticket(true, returnDate, departureFlight);

        homePage.searchFlights(ticket);
        SelectFlightOptionsPage.closeAds();
        softAssert.assertEquals(SelectFlightOptionsPage.getFlightInfo(), departureFlight);
        SelectFlightOptionsPage.selectCheapestFlight(false);

        softAssert.assertEquals(SelectFlightOptionsPage.getFlightInfo(), returnFlight);
        SelectFlightOptionsPage.selectCheapestFlight(true);

        softAssert.assertEquals(passengerInformationPage.getReservationInfo(true), departureFlight);
        softAssert.assertEquals(passengerInformationPage.getReservationInfo(false), returnFlight);

        softAssert.assertAll();
    }
}

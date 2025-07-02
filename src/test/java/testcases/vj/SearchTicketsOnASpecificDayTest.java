package testcases.vj;

import data.enums.vj.FareOption;
import data.enums.vj.FlightDirection;
import data.enums.vj.FlightType;
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
    SelectFlightOptionsPage selectFlightOptionsPage;
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
        selectFlightOptionsPage = new SelectFlightOptionsPage();
        passengerInformationPage = new PassengerInformationPage();
        softAssert = new SoftAssert();

        departureDate = LocalDate.now().plusDays(1);
        returnDate = departureDate.plusDays(3);
        passenger = new Passenger(2, 0, 0);
    }

    @Test(dataProvider = "dataByMethodLocale", groups = {"smoke", "regression"}, description = "Search and choose tickets on a specific day successfully")
    public void searchTicketsOnASpecificDay(VJTestData data) {

        departureFlight = new FlightInfo("VND", data.getDepartureAirport(), data.getDestinationAirport(), departureDate, passenger);
        returnFlight = new FlightInfo("VND", data.getDestinationAirport(), data.getDepartureAirport(), returnDate, passenger);
        ticket = new Ticket(FlightType.RETURN, departureFlight, returnFlight, FareOption.ANY);

        homePage.searchFlights(ticket);
        selectFlightOptionsPage.closeAds();
        softAssert.assertEquals(selectFlightOptionsPage.getFlightInfo(), departureFlight);
        selectFlightOptionsPage.selectFlightByPrice(FlightDirection.DEPARTURE);

        softAssert.assertEquals(selectFlightOptionsPage.getFlightInfo(), returnFlight);
        selectFlightOptionsPage.selectFlightByPrice(FlightDirection.RETURN);

        softAssert.assertEquals(passengerInformationPage.getReservationInfo(FlightDirection.DEPARTURE), departureFlight);
        softAssert.assertEquals(passengerInformationPage.getReservationInfo(FlightDirection.RETURN), returnFlight);

        softAssert.assertAll();
    }
}

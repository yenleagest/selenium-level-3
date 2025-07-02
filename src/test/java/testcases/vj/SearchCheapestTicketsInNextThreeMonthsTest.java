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
import pages.vj.SelectFlightFarePage;
import pages.vj.SelectFlightOptionsPage;
import testcases.TestBase;
import testdata.VJTestData;

import java.time.LocalDate;
import java.time.YearMonth;

public class SearchCheapestTicketsInNextThreeMonthsTest extends TestBase {

    HomePage homePage;
    SelectFlightOptionsPage selectFlightOptionsPage;
    PassengerInformationPage passengerInformationPage;
    SelectFlightFarePage selectFlightFarePage;
    SoftAssert softAssert;
    Ticket ticket;
    FlightInfo departureFlight;
    FlightInfo returnFlight;
    Passenger passenger;
    YearMonth departureMonth;
    String lowestPrice;
    LocalDate departuteDate;
    LocalDate returnDate;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();
        homePage = new HomePage();
        selectFlightFarePage = new SelectFlightFarePage();
        selectFlightOptionsPage = new SelectFlightOptionsPage();
        passengerInformationPage = new PassengerInformationPage();
        softAssert = new SoftAssert();

        departureMonth = YearMonth.from(LocalDate.now().plusMonths(3));
        passenger = new Passenger(2, 0, 0);
    }

    @Test(dataProvider = "dataByMethodLocale", groups = {"smoke", "regression"}, description = "Search and choose cheapest tickets in next three months successfully")
    public void searchCheapestTicketsInNextThreeMonths(VJTestData data) {

        departureFlight = new FlightInfo("VND", data.getDepartureAirport(), data.getDestinationAirport(), null, passenger);
        returnFlight = new FlightInfo("VND", data.getDestinationAirport(), data.getDepartureAirport(), null, passenger);
        ticket = new Ticket(FlightType.RETURN, departureFlight, returnFlight, FareOption.LOWEST);

        homePage.searchFlights(ticket);

        softAssert.assertEquals(selectFlightFarePage.getFlightInfo(FlightDirection.DEPARTURE), departureFlight);
        softAssert.assertEquals(selectFlightFarePage.getFlightInfo(FlightDirection.RETURN), returnFlight);

        lowestPrice = selectFlightFarePage.getCheapestPrice(departureMonth, FlightDirection.DEPARTURE);
        departuteDate = selectFlightFarePage.getTakeOffDateByPrice(FlightDirection.DEPARTURE, departureMonth, lowestPrice);
        selectFlightFarePage.selectFlightOnSpecificDate(FlightDirection.DEPARTURE, departuteDate);
        departureFlight.setTakeOffDate(departuteDate);

        returnDate = departuteDate.plusDays(3);
        selectFlightFarePage.selectFlightOnSpecificDate(FlightDirection.RETURN, returnDate);
        returnFlight.setTakeOffDate(returnDate);

        selectFlightOptionsPage.closeAds();
        softAssert.assertEquals(selectFlightOptionsPage.getFlightInfo(), ticket.getDepartureFlight());
        selectFlightOptionsPage.selectFlightByPrice(FlightDirection.DEPARTURE);

        softAssert.assertEquals(selectFlightOptionsPage.getFlightInfo(), ticket.getReturnFlight());
        selectFlightOptionsPage.selectFlightByPrice(FlightDirection.RETURN);

        softAssert.assertEquals(passengerInformationPage.getReservationInfo(FlightDirection.DEPARTURE), departureFlight);
        softAssert.assertEquals(passengerInformationPage.getReservationInfo(FlightDirection.RETURN), returnFlight);

        softAssert.assertAll();
    }
}

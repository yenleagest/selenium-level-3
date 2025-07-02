package pages.vj;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.enums.vj.FareOption;
import data.enums.vj.FlightType;
import data.enums.vj.VJLocale;
import data.models.vj.Passenger;
import data.models.vj.Ticket;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.LocalizedTextWrapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static common.Constants.ENGLISH_YEAR_MONTH_FORMATTER;
import static common.Constants.STANDARD_YEAR_MONTH_FORMATTER;
import static common.Constants.VJ_LOCALE;

@Slf4j
public class HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public HomePage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    private enum LocalizedText {
        ACCEPT_COOKIE_BTN, RETURN_BTN, ONEWAY_BTN, DEPARTURE_TEXTBOX, DESTINATION_TEXTBOX, DEPARTURE_DATEPICKER, RETURN_DATEPICKER, PASSENGER_ADULTS, PASSENGER_CHILDREN, PASSENGER_INFANTS, FARE_CHECKBOX, SEARCH_BTN,
    }

    private final By suggestionPanel = By.className("scrollCustom");
    private final By rdrMonthName = By.className("rdrMonthName");
    private final By nextMonthBtn = By.className("rdrNextButton");
    private final By previousMonthBtn = By.className("rdrPprevButton");
    private final By decreaseBtn = By.xpath("preceding-sibling::button");
    private final By increaseBtn = By.xpath("following-sibling::button");
    private final String acceptCookieBtn = "//div[@aria-describedby='popup-dialog-description']//h5[text()='%s']";
    private final String returnRadioBtn = "//div[@role='radiogroup']//span[text()='%s']";
    private final String airportTextBox = "//label[text()='%s']/following-sibling::div/input";
    private final String airportOption = "//div[contains(@class, 'MuiExpansionPanelDetails')]//div[text()='%s']";
    private final String passengerValue = "//p[text()='%s']/following::span[contains(@class, 'MuiTypography')][1]";
    private final String fareCheckbox = "//div[contains(@class,'MuiPaper-rounded')]//h3[text()='%s']/preceding-sibling::span";
    private final String searchBtn = "//span[contains(@class, 'MuiButton')]/span[text()=\"%s\"]";
    private final String selectableDate = "//div[text() = '%s']/following-sibling::div//button[not(contains(@class, 'rdrDayDisabled'))]//span[text()='%s']";

    @Step("Search for tickets with following details: {ticket}")
    public void searchFlights(Ticket ticket) {
        LocalDate departureDate = Objects.requireNonNullElse(ticket.getDepartureFlight().getTakeOffDate(), LocalDate.now().plusDays(1));
        acceptCookie();
        selectFlightType(ticket.getFlightType());
        selectAirport(localizedText.get(LocalizedText.DEPARTURE_TEXTBOX), ticket.getDepartureFlight().getDepartureAirport());
        selectAirport(localizedText.get(LocalizedText.DESTINATION_TEXTBOX), ticket.getDepartureFlight().getDestinationAirport());
        selectDate(departureDate);
        if (ticket.getFlightType() == FlightType.RETURN) {
            LocalDate returnDate = Objects.requireNonNullElse(ticket.getReturnFlight().getTakeOffDate(), departureDate.plusDays(1));
            selectDate(returnDate);
        }
        submitPassenger(ticket.getDepartureFlight().getPassenger());
        if (ticket.getFareOption() == FareOption.LOWEST) selectFindLowestFareOption();
        search();
    }

    @Step("Accept cookie")
    private void acceptCookie() {
        $x(acceptCookieBtn.formatted(localizedText.get(LocalizedText.ACCEPT_COOKIE_BTN))).click();
    }

    @Step("Select return flight")
    private void selectFlightType(FlightType flightType) {
        $x(returnRadioBtn.formatted(flightType == FlightType.RETURN ? localizedText.get(LocalizedText.RETURN_BTN) : localizedText.get(
                LocalizedText.ONEWAY_BTN))).click();
    }

    @Step("Search for airport: {airport}")
    private void selectAirport(String textLocator, String airport) {
        SelenideElement textbox = $x(airportTextBox.formatted(textLocator));
        openAirportDropdown(textbox);
        searchAirport(textLocator, airport);
        String selectedAirport = selectAirportFromSuggestions(airport);
        waitForSelectionApplied(textbox, selectedAirport);
    }

    private void openAirportDropdown(SelenideElement textbox) {
        textbox.click();
        $(suggestionPanel).shouldBe(visible);
    }

    private void searchAirport(String textLocator, String airport) {
        $x(airportTextBox.formatted(textLocator)).setValue(airport);
    }

    private String selectAirportFromSuggestions(String airport) {
        SelenideElement option = $x(airportOption.formatted(airport)).shouldBe(visible);
        String airportName = option.getText().trim();
        option.click();
        return airportName;
    }

    private void waitForSelectionApplied(SelenideElement textbox, String airportName) {
        textbox.shouldHave(Condition.attributeMatching("value", ".*%s.*".formatted(airportName)));
    }

    @Step("Select date: {date}")
    private void selectDate(LocalDate date) {
        alignDatePickerToMonth(date);
        String yearMonth = $(rdrMonthName).shouldBe(visible).shouldNotHave(exactText("")).getText().trim();
        $x(selectableDate.formatted(
                VJ_LOCALE == VJLocale.VI ? yearMonth.toLowerCase() : yearMonth,
                date.getDayOfMonth()
        )).click();
    }

    @Step("Submit passenger: {passenger}")
    private void submitPassenger(Passenger passenger) {
        submitPassengerAttribute(localizedText.get(LocalizedText.PASSENGER_ADULTS), passenger.getAdults());
        submitPassengerAttribute(localizedText.get(LocalizedText.PASSENGER_CHILDREN), passenger.getChildren());
        submitPassengerAttribute(localizedText.get(LocalizedText.PASSENGER_INFANTS), passenger.getInfants());
    }

    @Step("Submit number of {attributeText}: {count}")
    private void submitPassengerAttribute(String attributeText, int count) {
        int currentAdults = getCurrentAttributeValue(attributeText);
        adjustUntilEqual(attributeText, currentAdults, count);
    }

    @Step("Select 'Find lowest fare' option")
    private void selectFindLowestFareOption() {
        $x(fareCheckbox.formatted(localizedText.get(LocalizedText.FARE_CHECKBOX))).click();
    }

    @Step("Hit search button")
    private void search() {
        $x(searchBtn.formatted(localizedText.get(LocalizedText.SEARCH_BTN))).click();
    }

    @Step("Set {attributeText} to {target}")
    private void adjustUntilEqual(String attributeText, int current, int target) {
        if (current < target) {
            for (int i = current; i < target; i++) {
                incrPassenger(attributeText);
            }
        } else if (current > target) {
            for (int i = current; i > target; i--) {
                decrPassenger(attributeText);
            }
        }
    }

    private void incrPassenger(String attributeText) {
        $x(passengerValue.formatted(attributeText)).$(increaseBtn).click();
    }

    private void decrPassenger(String attributeText) {
        $x(passengerValue.formatted(attributeText)).$(decreaseBtn).click();
    }

    private int getCurrentAttributeValue(String attributeText) {
        return Integer.parseInt($x(passengerValue.formatted(attributeText))
                                        .shouldNotHave(exactText(""))
                                        .getText()
                                        .trim());
    }

    @Step("Align date picker to the month of the given date: {localDate}")
    private void alignDatePickerToMonth(LocalDate localDate) {
        YearMonth current = getYearMonthFromDatePicker();
        YearMonth target = YearMonth.from(localDate);
        while (!Objects.requireNonNull(current).equals(target)) {
            if (current.isBefore(target)) $(nextMonthBtn).click();
            else $(previousMonthBtn).click();
            current = getYearMonthFromDatePicker();
        }
    }

    @Step("Get the year and month from the date picker caption")
    private YearMonth getYearMonthFromDatePicker() {
        String yearMonth = $(rdrMonthName).shouldBe(visible).shouldNotHave(exactText("")).getText().trim();

        DateTimeFormatter formatter;
        if (VJ_LOCALE == VJLocale.EN) formatter = ENGLISH_YEAR_MONTH_FORMATTER;
        else if (VJ_LOCALE == VJLocale.VI) {
            yearMonth = yearMonth.split(" ", 2)[1].trim();
            formatter = STANDARD_YEAR_MONTH_FORMATTER;
        } else
            // fallback to standard numeric format
            formatter = STANDARD_YEAR_MONTH_FORMATTER;

        try {
            return YearMonth.parse(yearMonth, formatter);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse year and month from text: '{}'. Error: {}", yearMonth, e.getMessage());
            return null;
        }
    }
}

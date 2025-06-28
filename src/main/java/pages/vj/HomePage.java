package pages.vj;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
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
import java.util.Locale;
import java.util.Objects;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static pages.vj.HomePage.LocalizedText.ACCEPT_COOKIE_BTN;
import static pages.vj.HomePage.LocalizedText.DEPARTURE_TEXTBOX;
import static pages.vj.HomePage.LocalizedText.DESTINATION_TEXTBOX;
import static pages.vj.HomePage.LocalizedText.ONEWAY_BTN;
import static pages.vj.HomePage.LocalizedText.PASSENGER_ADULTS;
import static pages.vj.HomePage.LocalizedText.PASSENGER_CHILDREN;
import static pages.vj.HomePage.LocalizedText.PASSENGER_INFANTS;
import static pages.vj.HomePage.LocalizedText.RETURN_BTN;
import static pages.vj.HomePage.LocalizedText.SEARCH_BTN;

@Slf4j
public class HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public HomePage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    protected enum LocalizedText {
        ACCEPT_COOKIE_BTN,
        RETURN_BTN,
        ONEWAY_BTN,
        DEPARTURE_TEXTBOX,
        DESTINATION_TEXTBOX,
        PASSENGER_ADULTS,
        PASSENGER_CHILDREN,
        PASSENGER_INFANTS,
        DEPARTURE_DATEPICKER,
        RETURN_DATEPICKER,
        SEARCH_BTN,
    }

    private final By suggestionPanel = By.className("scrollCustom");
    private final By rdrMonthName = By.className("rdrMonthName");
    private final By nextMonthBtn = By.className("rdrNextButton");
    private final By previousMonthBtn = By.className("rdrPprevButton");
    private final String acceptCookieBtn = "//div[@aria-describedby='popup-dialog-description']//h5[text()='%s']";
    private final String returnRadioBtn = "//div[@role='radiogroup']//span[text()='%s']";
    private final String airportTextBox = "//label[text()='%s']/following-sibling::div/input";
    private final String airportOption = "//div[contains(@class, 'MuiExpansionPanelDetails')]//div[text()='%s']";
    private final String passengerValue = "//p[text()='%s']/following::span[contains(@class, 'MuiTypography')][1]";
    private final String decreaseBtn = passengerValue.concat("/preceding-sibling::button");
    private final String increaseBtn = passengerValue.concat("/following-sibling::button");
    private final String searchBtn = "//span[contains(@class, 'MuiButton')]/span[text()=\"%s\"]";
    private final String selectedDate = "//div[translate(text(), 't', 'T') = '%s']/following-sibling::div//button[not(contains(@class, 'rdrDayDisabled'))]//span[text()='%s']";

    @Step("Search for tickets with following details: {ticket}")
    public void searchFlights(Ticket ticket) {
        acceptCookie();
        selectFlightType(ticket.isReturnFlight());
        searchAirport(localizedText.get(DEPARTURE_TEXTBOX), ticket.getFlightInfo().getDepartureAirport());
        searchAirport(localizedText.get(DESTINATION_TEXTBOX), ticket.getFlightInfo().getDestinationAirport());
        selectDate(ticket.getFlightInfo().getTakeOffDate());
        if (ticket.isReturnFlight())
            selectDate(ticket.getReturnDate());
        submitPassenger(ticket.getFlightInfo().getPassenger());
        search();
    }

    @Step("Accept cookie")
    private void acceptCookie() {
        $x(acceptCookieBtn.formatted(localizedText.get(ACCEPT_COOKIE_BTN))).click();
    }

    @Step("Select return flight")
    private void selectFlightType(boolean isReturnFlight) {
        $x(returnRadioBtn.formatted(isReturnFlight ? localizedText.get(RETURN_BTN) : localizedText.get(ONEWAY_BTN))).click();
    }

    @Step("Search for airport: {airport}")
    private void searchAirport(String textLocator, String airport) {
        SelenideElement textbox = $x(airportTextBox.formatted(textLocator));
        openAirportDropdown(textbox);
        enterAirportSearch(textLocator, airport);
        String selectedAirport = selectAirportFromSuggestions(airport);
        waitForSelectionApplied(textbox, selectedAirport);
    }

    private void openAirportDropdown(SelenideElement textbox) {
        textbox.click();
        $(suggestionPanel).shouldBe(visible);
    }

    private void enterAirportSearch(String textLocator, String airport) {
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
        $x(selectedDate.formatted(yearMonth, date.getDayOfMonth())).click();
    }

    @Step("Submit passenger: {passenger}")
    private void submitPassenger(Passenger passenger) {
        submitPassengerAttribute(localizedText.get(PASSENGER_ADULTS), passenger.getAdults());
        submitPassengerAttribute(localizedText.get(PASSENGER_CHILDREN), passenger.getChildren());
        submitPassengerAttribute(localizedText.get(PASSENGER_INFANTS), passenger.getInfants());
    }

    @Step("Submit number of {attributeText}: {count}")
    private void submitPassengerAttribute(String attributeText, int count) {
        int currentAdults = getCurrentAttributeValue(attributeText);
        adjustUntilEqual(attributeText, currentAdults, count);
    }

    @Step("Hit search button")
    private void search() {
        $x(searchBtn.formatted(localizedText.get(SEARCH_BTN))).click();
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
        $x(increaseBtn.formatted(attributeText)).click();
    }

    private void decrPassenger(String attributeText) {
        $x(decreaseBtn.formatted(attributeText)).click();
    }

    private int getCurrentAttributeValue(String attributeText) {
        return Integer.parseInt($x(passengerValue.formatted(attributeText)).shouldNotHave(exactText("")).getText().trim());
    }

    @Step("Align date picker to the month of the given date: {localDate}")
    private void alignDatePickerToMonth(LocalDate localDate) {
        YearMonth current = getYearMonthFromDatePicker();
        YearMonth target = YearMonth.from(localDate);
        while (!Objects.requireNonNull(current).equals(target)) {
            if (current.isBefore(target))
                $(nextMonthBtn).click();
            else
                $(previousMonthBtn).click();
            current = getYearMonthFromDatePicker();
        }
    }

    @Step("Get the year and month from the date picker caption")
    private YearMonth getYearMonthFromDatePicker() {
        String yearMonth = $(rdrMonthName).shouldBe(visible).shouldNotHave(exactText("")).getText().trim();
        String baseUrl = System.getProperty("selenide.baseUrl");

        DateTimeFormatter formatter;
        if (baseUrl.endsWith("/en"))
            formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        else if (baseUrl.endsWith("/vi")) {
            yearMonth = yearMonth.replace("Tháng", "").trim(); // new Locale("vi") only works with "Tháng Sáu 2025" not "Tháng 06 2025"
            formatter = DateTimeFormatter.ofPattern("MM yyyy");
        } else
            // fallback to standard numeric format
            formatter = DateTimeFormatter.ofPattern("MM yyyy");

        try {
            return YearMonth.parse(yearMonth, formatter);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse year and month from text: '{}'. Error: {}", yearMonth, e.getMessage());
            return null;
        }
    }
}

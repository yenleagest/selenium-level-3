package pages.vj;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.enums.vj.FlightDirection;
import data.models.vj.FlightInfo;
import data.models.vj.Passenger;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import reports.AllureManager;
import utils.LocalizedTextWrapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static common.Constants.YEAR_MONTH_FORMATTER_WITH_FLASH;

@Slf4j
public class SelectFlightFarePage extends HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public SelectFlightFarePage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    private enum LocalizedText {
        FARE_LABEL, DEPARTURE_LABEL, RETURN_LABEL
    }

    private enum AirportType {
        DEPARTURE, DESTINATION
    }

    public static record CheapestFlights(LocalDate departureDate, String departurePrice, LocalDate returnDate, String returnPrice) {
    }

    private final String currency = "//span[text()='%s']/following-sibling::span";
    private final String flightDirectionLabel = "//p[contains(@class,'MuiTypography-h2')][text()='%s']";
    private final String optionByDate = "descendant::p[text() = '%s']";
    private final By airportsText = By.xpath("following-sibling::div");
    private final By passengerInfo = By.cssSelector(".MuiTypography-h5[variantmd='h3']");
    private final By monthSlider = By.xpath("following::div[contains(@class,'slick-list')][1]");
    private final By nextMonthBtn = By.xpath("preceding-sibling::button");
    private final By previousMonthBtn = By.xpath("following-sibling::button");
    private final By currentMonthSlider = By.xpath("following::div[contains(@class,'slick-current')][1]//p");
    private final By priceCalendar = By.xpath("parent::div/following-sibling::div[position()=2]");
    private final By selectableOption = By.xpath("descendant::span[normalize-space(text()) != '']");
    private final By dateByPrice = By.xpath("preceding::p[1]");
    private final By priceByDate = By.xpath("following-sibling::div");
    private final By continueBtn = By.cssSelector("span.MuiButton-label span");

    @Step("Get flight info for {direction} flight")
    public FlightInfo getFlightInfo(FlightDirection direction) {
        waitForPageToBeLoaded(); // without it some text elements may display in Vietnamese
        FlightInfo flightInfo = new FlightInfo(getCurrency(), getAirportName(direction, AirportType.DEPARTURE), getAirportName(direction, AirportType.DESTINATION), null, getPassengerInfo());
        log.info("{} flight info for: {}", direction, flightInfo);
        return flightInfo;
    }

    @Step("Find the cheapest tickets for {duration} days trip in next {target} months")
    public CheapestFlights getCheapestFlights(YearMonth target, int duration) {
        Map<LocalDate, String> departures = getDatePrices(FlightDirection.DEPARTURE, target);
        Map<LocalDate, String> returns = getDatePrices(FlightDirection.RETURN, target);
        return findCheapest(departures, returns, duration);
    }

    @Step("Select the {direction} flight that takes off on {localDate}")
    public void selectFlightOnSpecificDate(FlightDirection direction, LocalDate localDate) {
        alignMonthSliderToMonth(YearMonth.from(localDate), direction);
        selectFlightByDate(localDate, direction);
        if (direction == FlightDirection.RETURN) {
            proceedWithFlightSelection();
        }
    }

    private Passenger getPassengerInfo() {
        return Passenger.fromString($(passengerInfo).getText());
    }

    @Step("Get currency")
    private String getCurrency() {
        String text = $x(currency.formatted(localizedText.get(LocalizedText.FARE_LABEL))).shouldNotHave(Condition.exactText("")).getText();
        // text looks like '(Prices displayed in currency VND)'
        Matcher m = Pattern.compile("([A-Z]{3})\\)").matcher(text);
        return m.find() ? m.group(1) : null;
    }

    @Step("Get {airportType} airport of the {direction} flight")
    private String getAirportName(FlightDirection direction, AirportType airportType) {
        String text = directionLabel(direction).find(airportsText).shouldNotHave(Condition.exactText("")).getText();
        // text looks like 'Ho Chi Minh (SGN) Ha Noi (HAN)'
        log.info("Airport text: {}", text);
        return text.split("\\)", 2)[airportType == AirportType.DEPARTURE ? 0 : 1].split("\\(")[0].trim();
    }

    @Step("Align date picker to the month of the given date: {target}")
    private void alignMonthSliderToMonth(YearMonth target, FlightDirection direction) {
        YearMonth current = getCurrentYearMonth(direction);
        if (current.equals(target)) {
            return;
        }

        SelenideElement directionLabel = directionLabel(direction);
        String prevText = directionLabel.find(priceCalendar).getText();

        while (!Objects.requireNonNull(current).equals(target)) {
            if (current.isBefore(target)) directionLabel.find(monthSlider).find(previousMonthBtn).click();
            else directionLabel.find(monthSlider).find(nextMonthBtn).click();
            current = getCurrentYearMonth(direction);
        }
        // wait for the slider to be updated to target month
        directionLabel.find(currentMonthSlider).shouldHave(Condition.text(target.format(YEAR_MONTH_FORMATTER_WITH_FLASH)));
        directionLabel.find(priceCalendar).shouldNotHave(Condition.exactText(prevText));
    }

    private YearMonth getCurrentYearMonth(FlightDirection direction) {
        if (direction == FlightDirection.RETURN)
            $(continueBtn).scrollIntoView("{block: 'center'}");

        String text = directionLabel(direction)
                .find(currentMonthSlider)
                .shouldNotHave(Condition.exactText(""))
                .getText().trim();

        return YearMonth.parse(text, YEAR_MONTH_FORMATTER_WITH_FLASH);
    }

    private void selectFlightByDate(LocalDate localDate, FlightDirection direction) {
        SelenideElement calendar = directionLabel(direction).find(priceCalendar);
        // wait for the last option to be loaded completely
        int lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        calendar.find(By.xpath(optionByDate.formatted(lastDayOfMonth))).find(priceByDate)
                .shouldNotHave(Condition.exactText(""))
                .shouldBe(clickable);

        calendar.find(By.xpath(optionByDate.formatted(localDate.getDayOfMonth())))
                .scrollIntoView("{block: 'center'}")
                .click();
    }

    @Step("Click continue button to proceed with flight selection")
    private void proceedWithFlightSelection() {
        $(continueBtn).shouldBe(visible).click();
    }

    private SelenideElement directionLabel(FlightDirection direction) {
        String directionLabel = direction == FlightDirection.DEPARTURE ? localizedText.get(LocalizedText.DEPARTURE_LABEL) : localizedText.get(LocalizedText.RETURN_LABEL);
        return $x(flightDirectionLabel.formatted(directionLabel));

    }

    private void waitForPageToBeLoaded() {
        directionLabel(FlightDirection.DEPARTURE).find(priceCalendar).findAll(selectableOption).shouldHave(CollectionCondition.sizeGreaterThan(0));
        directionLabel(FlightDirection.RETURN).find(priceCalendar).findAll(selectableOption).shouldHave(CollectionCondition.sizeGreaterThan(0));
    }

    private Map<LocalDate, String> getDatePrices(FlightDirection direction, YearMonth target) {

        Map<LocalDate, String> datePrices = new HashMap<>(getDatePrices(direction));
        YearMonth start = YearMonth.now().plusMonths(1);

        for (YearMonth ym = start; !ym.isAfter(target); ym = ym.plusMonths(1)) {
            alignMonthSliderToMonth(ym, direction);

            datePrices.putAll(getDatePrices(direction));
        }
        return datePrices;
    }

    private Map<LocalDate, String> getDatePrices(FlightDirection direction) {
        YearMonth currentMonth = getCurrentYearMonth(direction);
        Map<LocalDate, String> datePrices;

        datePrices = directionLabel(direction)
                .find(priceCalendar)
                .findAll(selectableOption)
                .asDynamicIterable() // avoid StaleElementReferenceException
                .stream()
                .filter(this::isValidDayElement)
                .collect(Collectors.toMap(
                        el -> extractDate(el, currentMonth),
                        this::extractPrice
                ));

        AllureManager.saveLog("Prices for %s flight in month %s".formatted(direction, currentMonth), formatDatePrices(datePrices));
        return datePrices;
    }

    private boolean isValidDayElement(SelenideElement element) {
        String text = element.find(dateByPrice).shouldNotHave(Condition.exactText("")).getText().trim();
        return text.matches("\\d+");
    }

    private String extractPrice(SelenideElement element) {
        return element.getText().trim();
    }

    private LocalDate extractDate(SelenideElement element, YearMonth currentMonth) {
        int day = Integer.parseInt(element.find(dateByPrice).getText().trim());
        return currentMonth.atDay(day);
    }

    private CheapestFlights findCheapest(Map<LocalDate, String> departures, Map<LocalDate, String> returns, int duration) {
        int minSum = Integer.MAX_VALUE;
        CheapestFlights result = null;

        for (Map.Entry<LocalDate, String> dep : departures.entrySet()) {
            LocalDate departureDate = dep.getKey();
            LocalDate returnDate = departureDate.plusDays(duration);
            String departurePrice = dep.getValue().trim();

            if (!returns.containsKey(returnDate)) continue;  // when departure date + duration exceeds available return dates
            String returnPrice = returns.get(returnDate).trim();

            try {
                int depVal = Integer.parseInt(departurePrice.replaceAll("\\D", ""));
                int retVal = Integer.parseInt(returnPrice.replaceAll("\\D", ""));
                int sum = depVal + retVal;

                if (sum < minSum) {
                    minSum = sum;
                    result = new CheapestFlights(departureDate, departures.get(departureDate), returnDate, returns.get(returnDate));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid price format for combination of %s".formatted(
                                new CheapestFlights(departureDate, departures.get(departureDate), returnDate, returns.get(returnDate))
                        ),
                        e
                );
            }
        }

        AllureManager.saveLog(
                "Cheapest flight found",
                "\n• Departure: %s — Price: %s\n• Return   : %s — Price: %s".formatted(
                        Objects.requireNonNull(result).departureDate,
                        result.departurePrice,
                        result.returnDate,
                        result.returnPrice
                )
        );

        return result;
    }

    private String formatDatePrices(Map<LocalDate, String> datePrices) {
        StringBuilder sb = new StringBuilder("\n");
        datePrices.forEach((date, price) -> sb.append("• %s: %s%n".formatted(date, price)));
        return sb.toString();
    }
}



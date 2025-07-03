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
import utils.LocalizedTextWrapper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final String currency = "//span[text()='%s']/following-sibling::span";
    private final String flightDirectionLabel = "//p[contains(@class,'MuiTypography-h2')][text()='%s']";
    private final String optionByPrice = ".//span[normalize-space(text()) = '%s']";
    private final String optionByDate = "descendant::p[text() = '%s']";
    private final By airportsText = By.xpath("following-sibling::div");
    private final By passengerInfo = By.cssSelector(".MuiTypography-h5[variantmd='h3']");
    private final By monthSlider = By.xpath("following::div[contains(@class,'slick-list')][1]");
    private final By nextMonthBtn = By.xpath("preceding-sibling::button");
    private final By previousMonthBtn = By.xpath("following-sibling::button");
    private final By currentMonthSlider = By.xpath("following::div[contains(@class,'slick-current')][1]");
    private final By priceCalendar = By.xpath("parent::div/following-sibling::div[position()=2]");
    private final By selectableOption = By.xpath("descendant::span[normalize-space(text()) != '']");
    private final By dateByPrice = By.xpath("preceding::p[1]");
    private final By continueBtn = By.cssSelector("span.MuiButton-label span");

    @Step("Get flight info for {direction} flight")
    public FlightInfo getFlightInfo(FlightDirection direction) {
        waitForPageToBeLoaded(); // without it some text elements may display in Vietnamese
        FlightInfo flightInfo = new FlightInfo(getCurrency(), getAirportName(direction, AirportType.DEPARTURE), getAirportName(direction, AirportType.DESTINATION), null, getPassengerInfo());
        log.info("{} flight info for: {}", direction, flightInfo);
        return flightInfo;
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
        SelenideElement directionLabel = directionLabel(direction);
        YearMonth current = getCurrentYearMonth(direction);

        while (!Objects.requireNonNull(current).equals(target)) {
            if (current.isBefore(target)) directionLabel.find(monthSlider).find(previousMonthBtn).click();
            else directionLabel.find(monthSlider).find(nextMonthBtn).click();
            current = getCurrentYearMonth(direction);
        }
        // wait for the slider to be updated to target month
        directionLabel.find(currentMonthSlider).shouldHave(Condition.text(target.format(YEAR_MONTH_FORMATTER_WITH_FLASH)));
    }

    private YearMonth getCurrentYearMonth(FlightDirection direction) {
        String text = directionLabel(direction)
                .find(currentMonthSlider)
                .shouldNotHave(Condition.exactText(""))
                .getText();
        Matcher matcher = Pattern.compile("\\d{2}/\\d{4}").matcher(text);
        if (matcher.find()) {
            return YearMonth.parse(matcher.group(), YEAR_MONTH_FORMATTER_WITH_FLASH);
        } else {
            throw new IllegalStateException("Cannot find year month in format %s in: %s".formatted(YEAR_MONTH_FORMATTER_WITH_FLASH, text));
        }
    }

    public String getCheapestPrice(YearMonth yearMonth, FlightDirection direction) {
        alignMonthSliderToMonth(yearMonth, direction);
        return directionLabel(direction)
                .find(priceCalendar)
                .findAll(selectableOption)
                .asDynamicIterable() // avoid StaleElementReferenceException
                .stream()
                .min(Comparator.comparingInt(e -> Integer.parseInt(e.getText().replace(",", ""))))
                .map(SelenideElement::getText)
                .orElseThrow(() -> new RuntimeException("No prices found"));
    }

    private void selectFlightByDate(LocalDate localDate, FlightDirection direction) {
        directionLabel(direction)
                .find(priceCalendar)
                .shouldBe(visible)
                .find(By.xpath(optionByDate.formatted(localDate.getDayOfMonth())))
                .shouldBe(clickable)
                .click();
    }


    @Step("Get take-off date for {direction} flight priced at {price} within {yearMonth}")
    public LocalDate getTakeOffDateByPrice(FlightDirection direction, YearMonth yearMonth, String price) {
        String text = directionLabel(direction).find(priceCalendar).$x(optionByPrice.formatted(price)).find(dateByPrice).shouldNotHave(Condition.exactText("")).getText().trim();
        return yearMonth.atDay(Integer.parseInt(text));
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
}


package pages.vj;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.enums.vj.FlightDirection;
import data.enums.vj.VJLocale;
import data.models.vj.FlightInfo;
import data.models.vj.Passenger;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.LocalizedTextWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static common.Constants.ENGLISH_DATE_FORMATTER;
import static common.Constants.VJ_LOCALE;

@Slf4j
public class SelectFlightOptionsPage extends HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public SelectFlightOptionsPage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    protected enum LocalizedText {
        CONTINUE_BTN, DEPARTURE_LABEL, DESTINATION_LABEL, RESERVATION_DEPARTURE_LABEL, RESERVATION_RETURN_LABEL, PRICE_LABEL, TAXES_AND_FEES_LABEL,
    }

    private final By closeAdsBtn = By.cssSelector("[aria-label='close']");
    private final By flightInfo = By.cssSelector(".MuiTypography-h5[variantmd='h3']");
    private final By currency = By.cssSelector("p.MuiTypography-h4 ~ p.MuiTypography-body1");
    private final By flightsPrice = By.cssSelector("p.MuiTypography-h4:not([variantlg='h3']");
    private final By takeOffDate = By.cssSelector(".slick-active.slick-center div p:nth-child(2)");
    private final By flightDetailsPanel = By.cssSelector(".MuiCollapse-container.MuiCollapse-entered");
    private final String flightByPrice = "//p[contains(@class, 'MuiTypography-h4')][text()='%s']";
    private final String continueBtn = "//span[contains(@class, 'MuiTypography-h4')][text()='%s']";
    private final String reservationInfo = "//p[text()='%s']/ancestor::div//h4[text()='%s']/following-sibling::h4";


    @Step("Close VJ advertisement")
    public void closeAds() {
        $(closeAdsBtn).shouldBe(visible).click();
    }

    @Step("Get flight info")
    public FlightInfo getFlightInfo() {
        ElementsCollection collection = $$(flightInfo).shouldHave(CollectionCondition.size(3));
        /* example flight info elements:
         * 1st element: Return | 2 Adults, 1 Childrens, 1 Infants
         * 2nd element: From Ho Chi Minh (SGN)
         * 3rd element: To Ha Noi (HAN)
         */
        FlightInfo flightInfo = new FlightInfo(getCurrency(), getAirport(collection, FlightDirection.DEPARTURE), getAirport(collection, FlightDirection.RETURN), getTakeOffDate(), getPassengerInfo(collection));
        log.info("FlightInfo info: {}", flightInfo);
        return flightInfo;
    }

    @Step("Select cheapest {direction} flight")
    public void selectFlightByPrice(FlightDirection direction) {
        String cheapestPrice = getCheapestPrice();
        selectFlightByPrice(cheapestPrice);
        hitContinueButton(direction);
    }

    private void selectFlightByPrice(String price) {
        $x(flightByPrice.formatted(price)).shouldBe(visible).click();
        $(flightDetailsPanel).shouldBe(visible);
    }

    public void hitContinueButton(FlightDirection direction) {
        $x(continueBtn.formatted(localizedText.get(LocalizedText.CONTINUE_BTN))).shouldBe(visible).click();
        waitForReservationInfoDisplayed(direction);
    }

    private void waitForReservationInfoDisplayed(FlightDirection direction) {
        LocalizedText directionLabel = direction == FlightDirection.RETURN ? LocalizedText.RESERVATION_RETURN_LABEL : LocalizedText.RESERVATION_DEPARTURE_LABEL;
        $x(reservationInfo.formatted(localizedText.get(directionLabel), localizedText.get(LocalizedText.PRICE_LABEL))).shouldBe(visible);
        $x(reservationInfo.formatted(localizedText.get(directionLabel), localizedText.get(LocalizedText.PRICE_LABEL))).shouldBe(visible);
    }

    @Step("Get currency value")
    private String getCurrency() {
        return $(currency).shouldNotHave(Condition.exactText("")).getText().split(" ")[1].trim();
    }

    @Step("Get passenger info")
    private Passenger getPassengerInfo(ElementsCollection flightInfo) {
        return Passenger.fromString(flightInfo.get(0).shouldNotHave(Condition.exactText("")).getText());
    }

    @Step("Get {direction} airport name")
    private String getAirport(ElementsCollection flightInfo, FlightDirection direction) {
        String fullText = flightInfo.get(direction == FlightDirection.DEPARTURE ? 1 : 2).shouldNotHave(Condition.exactText("")).getText();
        // remove the known prefix
        LocalizedText prefix = direction == FlightDirection.DEPARTURE ? LocalizedText.DEPARTURE_LABEL : LocalizedText.DESTINATION_LABEL;
        String withoutPrefix = fullText.replaceFirst(Pattern.quote(localizedText.get(prefix)), "").trim();
        // remove the trailing airport code in parentheses
        return withoutPrefix.replaceFirst("\\s*\\([^)]*\\)$", "").trim();
    }

    @Step("Get take off date")
    private LocalDate getTakeOffDate() {
        String text = $(takeOffDate).shouldNotHave(Condition.exactText("")).getText().trim();

        if (VJ_LOCALE == VJLocale.EN) {
            String cleanedWithYear = "%s %s".formatted(text.replaceAll("(\\d+)(st|nd|rd|th)", "$1"), LocalDate.now().getYear());
            return LocalDate.parse(cleanedWithYear, ENGLISH_DATE_FORMATTER);
        } else if (VJ_LOCALE == VJLocale.VI) {
            // build a formatter: "d 'tháng' M" with default year = current year
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("d 'tháng' M").parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear()).toFormatter(Locale.forLanguageTag("vi"));

            return LocalDate.parse(text, formatter);
        } else {
            throw new IllegalStateException("Unsupported take off data format: " + text);
        }
    }

    private String getCheapestPrice() {
        return $$(flightsPrice).shouldHave(CollectionCondition.sizeGreaterThan(0)).stream().min(Comparator.comparingInt(e -> Integer.parseInt(e.getText().replace(",", "")))).map(SelenideElement::getText).orElseThrow(() -> new RuntimeException("No prices found"));
    }
}

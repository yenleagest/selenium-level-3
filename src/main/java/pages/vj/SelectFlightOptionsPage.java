package pages.vj;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.models.vj.FlightInfo;
import data.models.vj.Passenger;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.LocalizedTextWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static pages.vj.SelectFlightOptionsPage.LocalizedText.DEPARTURE_LABEL;
import static pages.vj.SelectFlightOptionsPage.LocalizedText.DESTINATION_LABEL;
import static pages.vj.SelectFlightOptionsPage.LocalizedText.CONTINUE_BTN;
import static pages.vj.SelectFlightOptionsPage.LocalizedText.PRICE_LABEL;
import static pages.vj.SelectFlightOptionsPage.LocalizedText.RESERVATION_DEPARTURE_LABEL;
import static pages.vj.SelectFlightOptionsPage.LocalizedText.RESERVATION_RETURN_LABEL;


@Slf4j
public class SelectFlightOptionsPage extends HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public SelectFlightOptionsPage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    protected enum LocalizedText {
        CONTINUE_BTN,
        DEPARTURE_LABEL,
        DESTINATION_LABEL,
        RESERVATION_DEPARTURE_LABEL,
        RESERVATION_RETURN_LABEL,
        PRICE_LABEL,
        TAXES_AND_FEES_LABEL,
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
        /* the text elements looks like below:
         * 1st element: Return | 2 Adults, 1 Childrens, 1 Infants
         * 2nd element: From Ho Chi Minh (SGN)
         * 3rd element: To Ha Noi (HAN)
         */
        FlightInfo flightInfo = new FlightInfo(
                getCurrency(),
                getAirport(collection, true),
                getAirport(collection, false),
                getTakeOffDate(),
                getPassengerInfo(collection)
        );
        log.info("FlightInfo info: {}", flightInfo);
        return flightInfo;
    }

    @Step("Select cheapest flight")
    public void selectCheapestFlight(boolean isReturnFlight) {
        String cheapestPrice = getCheapestPrice();
        selectCheapestFlight(cheapestPrice);
        hitContinueButton(isReturnFlight);
    }

    private void selectCheapestFlight(String price) {
        $x(flightByPrice.formatted(price)).shouldBe(visible).click();
        $(flightDetailsPanel).shouldBe(visible);
    }

    public void hitContinueButton(boolean isReturnFlight) {
        $x(continueBtn.formatted(localizedText.get(CONTINUE_BTN))).shouldBe(visible).click();
        waitForReservationInfoDisplayed(isReturnFlight);
    }

    private void waitForReservationInfoDisplayed(boolean isReturnFlight) {
        $x(reservationInfo.formatted(localizedText.get(isReturnFlight ? RESERVATION_RETURN_LABEL : RESERVATION_DEPARTURE_LABEL), localizedText.get(PRICE_LABEL))).shouldBe(visible);
        $x(reservationInfo.formatted(localizedText.get(isReturnFlight ? RESERVATION_RETURN_LABEL : RESERVATION_DEPARTURE_LABEL), localizedText.get(PRICE_LABEL))).shouldBe(visible);
    }

    @Step("Get currency value")
    private String getCurrency() {
        return $(currency).shouldNotHave(Condition.exactText("")).getText().split(" ")[1].trim();
    }

    @Step("Get passenger info")
    private Passenger getPassengerInfo(ElementsCollection flightInfo) {
        String[] passengerInfo = flightInfo.get(0).shouldNotHave(Condition.exactText("")).getText().split("\\|")[1].trim().split(",");
        int adults = 0;
        int children = 0;
        int infants = 0;
        if (passengerInfo.length == 1)
            adults = Integer.parseInt(passengerInfo[0].split(" ")[0]);
        else if (passengerInfo.length == 2)
            children = Integer.parseInt(passengerInfo[1].split(" ")[0]);
        else if (passengerInfo.length == 3)
            infants = Integer.parseInt(passengerInfo[2].split(" ")[0]);
        else
            throw new IllegalStateException("Unexpected passenger info format: " + flightInfo.get(0).getText());
        return new Passenger(adults, children, infants);
    }

    @Step("Get airport name")
    private String getAirport(ElementsCollection flightInfo, boolean isDeparture) {
        String fullText = flightInfo.get(isDeparture ? 1 : 2).shouldNotHave(Condition.exactText("")).getText();
        // remove the known prefix
        String withoutPrefix = fullText.replaceFirst(Pattern.quote(localizedText.get(isDeparture ? DEPARTURE_LABEL : DESTINATION_LABEL)), "").trim();
        // remove the trailing airport code in parentheses
        return withoutPrefix.replaceFirst("\\s*\\([^)]*\\)$", "").trim();
    }

    @Step("Get take off date")
    private LocalDate getTakeOffDate() {
        String text = $(takeOffDate).shouldNotHave(Condition.exactText("")).getText().trim();
        String baseUrl = System.getProperty("selenide.baseUrl");

        if (baseUrl.endsWith("/en")) {
            String cleanedWithYear = "%s %s".formatted(text.replaceAll("(\\d+)(st|nd|rd|th)", "$1"), LocalDate.now().getYear());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
            return LocalDate.parse(cleanedWithYear, formatter);
        } else if (baseUrl.endsWith("/vi")) {
            Pattern pattern = Pattern.compile("(\\d{1,2})\\s+tháng\\s+(\\d{1,2})");
            Matcher matcher = pattern.matcher(text);

            if (matcher.matches()) {
                int day = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int year = LocalDate.now().getYear();
                return LocalDate.of(year, month, day);
            }
        } else {
            throw new IllegalStateException("Unsupported take off data format: " + text);
        }
        return null;
    }

    private String getCheapestPrice() {
        return $$(flightsPrice).shouldHave(CollectionCondition.sizeGreaterThan(0))
                .stream()
                .map(SelenideElement::getText) // get the price string like "4,090"
                .map(text -> text.replace(",", "")) // remove comma to compare → "4090"
                .mapToInt(Integer::parseInt).min() // get the lowest price
                .stream()
                .mapToObj(min -> String.format("%,d", min))  // format back to "4,090" with comma
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No prices found"));
    }
}

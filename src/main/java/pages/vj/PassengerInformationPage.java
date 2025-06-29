package pages.vj;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import data.enums.vj.FlightDirection;
import data.models.vj.FlightInfo;
import data.models.vj.Passenger;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.LocalizedTextWrapper;

import java.time.LocalDate;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static common.Constants.EUROPEAN_DATE_FORMATTER;

@Slf4j
public class PassengerInformationPage extends HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public PassengerInformationPage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    private enum LocalizedText {
        RESERVATION_DEPARTURE_LABEL, RESERVATION_RETURN_LABEL, PRICE_LABEL
    }

    private enum AirportType {
        DEPARTURE, DESTINATION
    }

    private final By passengerInfo = By.cssSelector(".MuiTypography-h5[variantmd='h3']");
    private final String currency = "//div[@class='MuiExpansionPanelSummary-content']//h4[text()='%s']/following-sibling::h4";
    private final String reservationInfo = "//p[text()='%s']/ancestor::div[1]/following-sibling::div//h5";


    @Step("Get reservation info")
    public FlightInfo getReservationInfo(FlightDirection direction) {
        ElementsCollection info = $$x(reservationInfo.formatted(localizedText.get(direction == FlightDirection.DEPARTURE ? LocalizedText.RESERVATION_DEPARTURE_LABEL : LocalizedText.RESERVATION_RETURN_LABEL))).shouldHave(CollectionCondition.size(3));
        /* example reservation info elements:
         * 1st element: Ho Chi Minh (SGN)
         * 2nd element: Ha Noi (HAN)
         * 3rd element: Sun, 29/06/2025 | 00:35 - 02:30 | VJ1170 | Eco
         */
        FlightInfo reservation = new FlightInfo(getCurrency(), getAirport(info, AirportType.DEPARTURE), getAirport(info, AirportType.DESTINATION), getTakeOffDate(info), getPassengerInfo());
        log.info("Reservation info: {}", reservation);
        return reservation;
    }

    @Step("Get currency value")
    private String getCurrency() {
        return $x(currency.formatted(localizedText.get(LocalizedText.PRICE_LABEL))).shouldNotHave(Condition.exactText("")).getText().split(" ")[1].trim();
    }

    private Passenger getPassengerInfo() {
        return Passenger.getPassengerInfo($(passengerInfo).getText());
    }

    @Step("Get airport name")
    private String getAirport(ElementsCollection reservationInfo, AirportType airport) {
        return reservationInfo.get(airport == AirportType.DEPARTURE ? 0 : 1).shouldNotHave(Condition.exactText("")).getText().split("\\(")[0].trim();
    }

    @Step("Get take off date")
    private LocalDate getTakeOffDate(ElementsCollection reservationInfo) {
        String dateText = reservationInfo.get(2).shouldNotHave(Condition.exactText("")).getText().split(",")[1].split("\\|")[0].trim();
        return LocalDate.parse(dateText, EUROPEAN_DATE_FORMATTER);
    }
}

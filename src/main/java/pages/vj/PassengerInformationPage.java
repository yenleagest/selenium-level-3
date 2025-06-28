package pages.vj;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import data.models.vj.FlightInfo;
import data.models.vj.Passenger;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.LocalizedTextWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static pages.vj.PassengerInformationPage.LocalizedText.PRICE_LABEL;
import static pages.vj.PassengerInformationPage.LocalizedText.RESERVATION_DEPARTURE_LABEL;
import static pages.vj.PassengerInformationPage.LocalizedText.RESERVATION_RETURN_LABEL;

@Slf4j
public class PassengerInformationPage extends HomePage {

    private final LocalizedTextWrapper<LocalizedText> localizedText;

    public PassengerInformationPage() {
        this.localizedText = new LocalizedTextWrapper<>(this.getClass().getSimpleName());
    }

    enum LocalizedText {
        RESERVATION_DEPARTURE_LABEL, RESERVATION_RETURN_LABEL, PRICE_LABEL
    }

    private final By passengerInfo = By.cssSelector(".MuiTypography-h5[variantmd='h3']");
    private final String currency = "//div[@class='MuiExpansionPanelSummary-content']//h4[text()='%s']/following-sibling::h4";
    private final String reservationInfo = "//p[text()='%s']/ancestor::div[1]/following-sibling::div//h5";


    @Step("Get reservation info")
    public FlightInfo getReservationInfo(boolean isDeparture) {
        ElementsCollection info = $$x(reservationInfo.formatted(localizedText.get(isDeparture ? RESERVATION_DEPARTURE_LABEL : RESERVATION_RETURN_LABEL))).shouldHave(CollectionCondition.size(3));
        /* the text elements looks like below:
         * 1st element: Ho Chi Minh (SGN)
         * 2nd element: Ha Noi (HAN)
         * 3rd element: Sun, 29/06/2025 | 00:35 - 02:30 | VJ1170 | Eco
         */
        FlightInfo reservation = new FlightInfo(getCurrency(), getAirport(info, true), getAirport(info, false), getTakeOffDate(info), getPassengerInfo());
        log.info("Reservation info: {}", reservation);
        return reservation;
    }

    @Step("Get currency value")
    private String getCurrency() {
        return $x(currency.formatted(localizedText.get(PRICE_LABEL))).shouldNotHave(Condition.exactText("")).getText().split(" ")[1].trim();
    }

    @Step("Get passenger info")
    private Passenger getPassengerInfo() {
        String[] info = $(passengerInfo).getText().split("\\|")[1].trim().split(",");
        int adults = 0;
        int children = 0;
        int infants = 0;
        if (info.length == 1) adults = Integer.parseInt(info[0].split(" ")[0]);
        else if (info.length == 2) children = Integer.parseInt(info[1].split(" ")[0]);
        else if (info.length == 3) infants = Integer.parseInt(info[2].split(" ")[0]);
        else throw new IllegalStateException("Unexpected passenger info format: " + $(passengerInfo).getText());
        return new Passenger(adults, children, infants);
    }

    @Step("Get airport name")
    private String getAirport(ElementsCollection reservationInfo, boolean isDeparture) {
        return reservationInfo.get(isDeparture ? 0 : 1).shouldNotHave(Condition.exactText("")).getText().split("\\(")[0].trim();
    }

    @Step("Get take off date")
    private LocalDate getTakeOffDate(ElementsCollection reservationInfo) {
        String dateText = reservationInfo.get(2).shouldNotHave(Condition.exactText("")).getText().split(",")[1].split("\\|")[0].trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateText, formatter);
    }
}

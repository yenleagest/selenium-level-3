package pages.sia;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.openqa.selenium.By;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class HomePage {

    private final By searchTextBox = By.className("select2-search__field");
    private final By submitButton = By.cssSelector("[type='submit']");
    private final String locationComboBox = "[name='%s'] ~ span";
    private final String datePicker = "[name='%s']";
    private final String selectableDate = ".ui-datepicker-group-%s [data-date='%d']";

    @Getter
    @AllArgsConstructor
    private enum LocationType {
        DEPARTURE("from"),
        DESTINATION("to");

        private final String label;
    }

    @Getter
    @AllArgsConstructor
    private enum Direction {
        DEPARTURE("fromdate", "first"),
        RETURN("todate", "last");

        private final String label;
        private final String position;
    }

    @Step("Submit quote with departure: {departureLocation}, destination: {destinationLocation}, departure date: {departureDate}, return date: {returnDate}")
    public void submitQuote(String departureLocation, String destinationLocation, LocalDate departureDate, LocalDate returnDate) {
        selectLocation(LocationType.DEPARTURE, departureLocation);
        selectLocation(LocationType.DESTINATION, destinationLocation);
        selectDate(Direction.DEPARTURE, departureDate);
        selectDate(Direction.RETURN, returnDate);
        goToTravellersPage();
    }

    @Step("Select {location} location: {location}")
    private void selectLocation(LocationType type, String location) {
        SelenideElement comboBox = $(locationComboBox.formatted(type.getLabel()));
        comboBox.click();
        $(searchTextBox).shouldBe(visible).setValue(location).pressEnter();
        comboBox.shouldHave(Condition.text(location));
    }

    @Step("Select {direction} date: {date}")
    private void selectDate(Direction direction, LocalDate date) {
        $(datePicker.formatted(direction.getLabel())).click();
        $(selectableDate.formatted(direction.getPosition(), date.getDayOfMonth()))
                .shouldBe(clickable).click();
    }

    @Step("Go to Travellers page")
    private void goToTravellersPage() {
        $(submitButton).click();
    }
}

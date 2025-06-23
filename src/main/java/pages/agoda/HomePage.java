package pages.agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.models.Occupancy;
import drivers.DriverUtils;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static pages.agoda.HomePage.OccupancyType.ADULTS;
import static pages.agoda.HomePage.OccupancyType.CHILDREN;
import static pages.agoda.HomePage.OccupancyType.ROOMS;

public class HomePage {

    private final By googleIframe = By.cssSelector("iframe[src*='accounts.google.com']");
    private final By iframeCloseBtn = By.id("close");
    private final By menuIcon = By.className("HamburgerMenu");
    private final By datePickerCaption = By.className("DayPicker-Caption");
    private final By previousMonthBtn = By.cssSelector("[aria-label='Previous Month']");
    private final By nextMonthBtn = By.cssSelector("[aria-label='Next Month']");
    private final By selectedCurrency = By.cssSelector("[data-element-name='currency-container-selected-currency-name']");
    private final By vndCurrency = By.xpath("//p[text()='Vietnamese Dong']");
    private final By searchBox = By.id("textInput");
    private final By occContainer = By.className("OccupancySelector");
    private final By searchBtn = By.cssSelector("[data-selenium='searchButton']");
    private final By closeDownloadAppBtn = By.cssSelector("[leadingicon='fill.symbol.close']");

    // dynamic locators
    private final String searchSuggestion = "[data-text='%s']";
    private final String selectedDate = "[data-selenium-date='%s']";


    private int getRoomCount() {
        return parseValueFromElement(OccupancyType.ROOMS.valueSelector());
    }

    private int getAdultCount() {
        return parseValueFromElement(OccupancyType.ADULTS.valueSelector());
    }

    private int getChildCount() {
        return parseValueFromElement(OccupancyType.CHILDREN.valueSelector());
    }

    private int parseValueFromElement(String selector) {
        return Integer.parseInt($(selector).getText().trim());
    }

    // define an enum to manage occupancy's locators
    @AllArgsConstructor
    public enum OccupancyType {

        ROOMS("occupancyRooms"),
        ADULTS("occupancyAdults"),
        CHILDREN("occupancyChildren");

        private final String seleniumKey;

        public String controlSelector() {
            return "[data-selenium='%s']".formatted(seleniumKey);
        }

        public String plusButton() {
            return controlSelector().concat(" [data-selenium='plus']");
        }

        public String minusButton() {
            return controlSelector().concat(" [data-selenium='minus']");
        }

        public String valueSelector() {
            return controlSelector().concat(" [data-component*='desktop-occ']");
        }
    }

    @Step("Search a hotel with given information: {location}, check-in {checkIn}, check-out {checkOut}, occupancy: {occupancy}")
    public void searchHotel(String location, LocalDate checkIn, LocalDate checkOut, Occupancy occupancy) {
        selectVndCurrency();
        searchForLocation(location);
        selectDate(checkIn, checkOut);
        setOccupancyTo(occupancy);
        clickSearchButton();
    }

    @Step("Close occupancy container")
    public void confirmOccupancy() {
        $(occContainer).click();
    }

    @Step("Get current occupancy")
    public Occupancy getCurrentOccupancy() {
        return new Occupancy(getRoomCount(), getAdultCount(), getChildCount());
    }

    @Step("Increase {occType} by 1")
    public void increaseOcc(OccupancyType occType) {
        $(occType.plusButton()).click();
    }

    @Step("Decrease {occType} by 1")
    public void decreaseOcc(OccupancyType occType) {
        $(occType.minusButton()).click();
    }

    @Step("Search for location: {location}")
    private void searchForLocation(String location) {
        inputValueToSearchBox(location);
        selectFirstSuggestion(location);
    }

    @Step("Select check-in and check-out date: {checkIn} - {checkOut}")
    private void selectDate(LocalDate checkIn, LocalDate checkOut) {
        selectDate(checkIn);
        selectDate(checkOut);
    }

    @Step("Click search button")
    private void clickSearchButton() {
        $(searchBtn).click();
        DriverUtils.switchToLatestTab();
    }

    @Step("Input value to search box: {value}")
    private void inputValueToSearchBox(String value) {
        SelenideElement e = $(searchBox).shouldBe(visible);
        e.click();
        e.setValue(value);

    }

    @Step("Select first suggestion for location: {location}")
    private void selectFirstSuggestion(String location) {
        SelenideElement suggestion = $(searchSuggestion.formatted(location));
        suggestion.click();
    }

    @Step("Select date: {date}")
    private void selectDate(LocalDate date) {
        alignDatePickerToMonth(date);
        SelenideElement selectedDate = $(this.selectedDate.formatted(date));
        selectedDate.click();
    }

    @Step("Close app download ads if displayed")
    private void closeAppDownloadAds() {
        if ($(closeDownloadAppBtn).isDisplayed()) {
            $(closeDownloadAppBtn).click();
        }
    }

    @Step("Select occupancy: {target}")
    private void setOccupancyTo(Occupancy target) {
        adjustRooms(target.getRooms());
        adjustAdults(target.getAdults());
        adjustChildren(target.getChildren());
        confirmOccupancy(); // to close the container
    }

    @Step("Select {target} rooms")
    private void adjustRooms(int target) {
        adjustUntilEqual(ROOMS, getCurrentOccupancy().getRooms(), target);
    }

    @Step("Select {target} adults")
    private void adjustAdults(int target) {
        adjustUntilEqual(ADULTS, getCurrentOccupancy().getAdults(), target);
    }

    @Step("Select {target} children")
    private void adjustChildren(int target) {
        adjustUntilEqual(CHILDREN, getCurrentOccupancy().getChildren(), target);
    }

    @Step("Set {type} to {target}")
    private void adjustUntilEqual(OccupancyType type, int current, int target) {
        if (current < target) {
            for (int i = current; i < target; i++) {
                increaseOcc(type);
            }
        } else if (current > target) {
            for (int i = current; i > target; i--) {
                decreaseOcc(type);
            }
        }
    }

    @Step("Select Vietnamese Dong as currency")
    private void selectVndCurrency() {
        closeAppDownloadAds();
        closeGoogleIframe();
        selectMenu();
        openCurrencySelection();
        chooseVietnameseDong();
        selectMenu(); // to close the menu
    }

    @Step("Click on the menu icon")
    private void selectMenu() {
        $(menuIcon).click();
    }

    @Step("Click on current currency to open currency list")
    private void openCurrencySelection() {
        $(selectedCurrency).shouldBe(visible).click();
    }

    @Step("Select 'Vietnamese Dong' from currency list")
    private void chooseVietnameseDong() {
        $(vndCurrency).shouldBe(visible).click();
    }

    @Step("Close Google account pop-up if present")
    private void closeGoogleIframe() {
        // a Google pop-up will open and overlap the menu button if running in other browsers rather than Chrome
        if ($(googleIframe).exists()) {
            SelenideElement iframe = $(googleIframe);
            iframe.shouldBe(visible);
            DriverUtils.switchToIframe(iframe);
            $(iframeCloseBtn).click();
            DriverUtils.switchToDefaultContent();
        }
    }

    private void alignDatePickerToMonth(LocalDate localDate) {
        YearMonth current = getYearMonthFromDatePicker();
        YearMonth target = YearMonth.from(localDate);
        while (!current.equals(target)) {
            if (current.isBefore(target))
                $(nextMonthBtn).click();
            else
                $(previousMonthBtn).click();
            current = getYearMonthFromDatePicker();
        }
    }

    private YearMonth getYearMonthFromDatePicker() {
        // use YearMonth since the value of datePickerCaption is something like "July 2025"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        String yearMonth = $(datePickerCaption).shouldBe(visible).shouldNotHave(Condition.exactText("")).getText().trim();
        return YearMonth.parse(yearMonth, formatter);
    }
}

package pages.agoda;

import actions.HomePageActions;
import data.models.Occupancy;
import drivers.DriverUtils;
import elements.Element;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import utils.DateUtils;

import java.time.LocalDate;

public class HomePage {

    private final HomePageActions homePageActions = new HomePageActions(this);
    private final Element searchBox = new Element(By.id("textInput"));
    private final Element occupancyContainer = new Element(By.className("OccupancySelector"));
    private final Element searchBtn = new Element("[data-selenium='searchButton']");
    private final Element closeDownloadAppBtn = new Element("[leadingicon='fill.symbol.close']");

    // dynamic locators
    private final String searchSuggestion = "[data-text='%s']";
    private final String selectedDate = "[data-selenium-date='%s']";

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

    @Step("Search a hotel with given information: {location}, {duration} days from {offset} {weekday}, occupancy: {occupancy}")
    public void searchHotel(String location, DateUtils.WeekOffset offset, DateUtils.Weekday weekday, int duration, Occupancy occupancy) {
        searchForLocation(location);
        selectDate(offset, weekday, duration);
        homePageActions.setOccupancyTo(occupancy);
        clickSearchButton();
    }

    @Step("Click on occupancy container")
    public void selectOccupancyContainer() {
        occupancyContainer.click();
    }

    @Step("Get current occupancy")
    public Occupancy getCurrentOccupancy() {
        int rooms = Integer.parseInt(new Element(OccupancyType.ROOMS.valueSelector()).getRaw().getText().trim());
        int adults = Integer.parseInt(new Element(OccupancyType.ADULTS.valueSelector()).getRaw().getText().trim());
        int children = Integer.parseInt(new Element(OccupancyType.CHILDREN.valueSelector()).getRaw().getText().trim());
        return new Occupancy(rooms, adults, children);
    }

    @Step("Perform adjustment for occupancy: {occType}, increase: {increase}")
    public void adjustOccupancy(OccupancyType occType, boolean increase) {
        Element plusBtn = new Element(occType.plusButton());
        Element minusBtn = new Element(occType.minusButton());
        (increase ? plusBtn : minusBtn).click();
    }

    @Step("Search for location: {location}")
    private void searchForLocation(String location) {
        closeAppDownloadAds();
        inputValueToSearchBox(location);
        selectFirstSuggestion(location);
    }

    @Step("Select check-in and check-out date: {duration} days from {offset} {weekday}")
    private void selectDate(DateUtils.WeekOffset offset, DateUtils.Weekday weekday, int duration) {
        LocalDate checkinDate = DateUtils.getLocalDate(offset, weekday);
        LocalDate checkoutDate = checkinDate.plusDays(duration);
        selectDate(checkinDate.toString());
        selectDate(checkoutDate.toString());
    }

    @Step("Click search button")
    private void clickSearchButton() {
        searchBtn.click();
        DriverUtils.switchToLatestTab();
    }

    @Step("Input value to search box: {value}")
    private void inputValueToSearchBox(String value) {
        searchBox.click();
        searchBox.setValue(value);
    }

    @Step("Select first suggestion for location: {location}")
    private void selectFirstSuggestion(String location) {
        Element suggestion = new Element(searchSuggestion.formatted(location));
        suggestion.click();
    }

    @Step("Select date: {date}")
    private void selectDate(String date) {
        Element selectedDate = new Element(this.selectedDate.formatted(date));
        selectedDate.click();
    }

    @Step("Close app download ads if displayed")
    private void closeAppDownloadAds() {
        if (closeDownloadAppBtn.getRaw().isDisplayed()) {
            closeDownloadAppBtn.click();
        }
    }
}

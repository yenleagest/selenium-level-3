package pages.agoda;

import com.codeborne.selenide.SelenideElement;
import data.enums.agoda.Facilities;
import data.enums.agoda.SortBy;
import data.models.agoda.Hotel;
import data.models.agoda.PriceFilter;
import drivers.DriverUtils;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class SearchResultsPage extends HomePage {

    private final By minPrice = By.cssSelector("#SideBarLocationFilters #price_box_0");
    private final By maxPrice = By.cssSelector("#SideBarLocationFilters #price_box_1");
    // dynamic locators
    private final String sortBy = "[data-element-name='%s']";
    private final String rating = "#filter-menu-StarRatingWithLuxury ~ ul [data-element-value='%d']";
    // collection locators
    private final String cardContainer = "PropertyCardItem";
    private final String finalPrice = "[data-element-name='final-price']";
    private final String destination = "[data-selenium='area-city']";
    private final String hotelName = "[data-selenium='hotel-name']";
    private final String hotelByName = "//h3[@data-selenium='hotel-name'][text()='%s']";
    private final String starContainer = "[data-testid='rating-container'] span";
    private final String facilityFilter = "//div[@id='SideBarLocationFilters']//span[text()='%s']";

    @Step("Sort results by: {sortBy}")
    public void sortResultsBy(SortBy sortBy) {
        SelenideElement e = $(this.sortBy.formatted(sortBy.getDataElementName()));
        e.scrollIntoView("{block: 'center'}");
        e.click();
    }

    @Step("Filter price: {priceFilter}")
    public void filterPrice(PriceFilter priceFilter) {
        setFilterPrice(minPrice, priceFilter.getMin());
        setFilterPrice(maxPrice, priceFilter.getMax());
    }

    @Step("Filter result with {rating}-star rating")
    public void filterStar(int rating) {
        SelenideElement e = $(this.rating.formatted(rating));
        e.scrollIntoView(true).click();
    }

    @Step("Is {rating}-star rating selected")
    public boolean isStarRatingSelected(int rating) {
        SelenideElement e = $(this.rating.formatted(rating)).$("input");
        return e.isSelected();
    }

    @Step("Get price filter slider values")
    public PriceFilter getPriceFilterValues() {
        PriceFilter sliderValues = new PriceFilter(
                getFilterPrice(minPrice),
                getFilterPrice(maxPrice)
        );
        log.info("Price filter slider values: {}", sliderValues);
        return sliderValues;
    }

    @Step("Get hotel details for the first {count} results")
    public List<Hotel> getHotels(int count) {
        List<Hotel> cardContainers = new ArrayList<>();

        int retries = 0;
        // scroll into center of each card
        // for card loaded lazily, wait for it and its attribute to be displayed
        while (cardContainers.size() < count && retries++ < 2 * count) {
            int currIndex = cardContainers.size();
            SelenideElement card = $$(By.className(cardContainer)).get(currIndex).$(":first-child");

            card.scrollIntoView("{block: 'center'}").shouldBe(visible);

            Hotel hotel = getHotel(card);
            cardContainers.add(hotel);
            log.info("Hotel details [{}]: {}", cardContainers.size(), hotel);
        }

        return cardContainers;
    }

    @Step("Filter hotels by facility: {facility}")
    public void filterByFacility(Facilities facility) {
        SelenideElement e = $(By.xpath(facilityFilter.formatted(facility.getFacility())));
        e.scrollIntoView("{block: 'center'}").click();
    }

    @Step("Go to {hotelName} hotel details page")
    public void goToHotelDetailsPage(String hotelName) {
        SelenideElement hotel = $(By.xpath(hotelByName.formatted(hotelName)));
        hotel.scrollIntoView("{block: 'center'}").shouldBe(visible).click();
        DriverUtils.switchToLatestTab();
    }

    private Hotel getHotel(SelenideElement container) {
        return new Hotel(extractHotelName(container), extractDestination(container), extractPrice(container), extractRating(container), null);
    }

    private String extractDestination(SelenideElement container) {
        String dest = container.$(destination).shouldBe(visible).getText().split("-")[0].trim();
        if (dest.isEmpty()) {
            throw new IllegalStateException("Destination is missing or empty for a hotel card.");
        }
        return dest;
    }

    private String extractHotelName(SelenideElement container) {
        String name = container.$(hotelName).shouldBe(visible).getText().trim();
        if (name.isEmpty()) {
            throw new IllegalStateException("Hotel name is missing or empty for a hotel card.");
        }
        return name;
    }

    private int extractPrice(SelenideElement container) {
        String priceText = container.$(finalPrice).shouldBe(visible).getText().replaceAll("\\D", "");
        if (priceText.isEmpty()) {
            throw new IllegalStateException("Price is missing or empty for a hotel card.");
        }
        return Integer.parseInt(priceText);
    }

    private float extractRating(SelenideElement container) {
        String text = container.$(starContainer).shouldBe(visible).getText().split(" ")[0];
        return Float.parseFloat(text);
    }

    private void setFilterPrice(By locator, int value) {
        SelenideElement element = $(locator);
        element.clear();
        element.setValue(String.valueOf(value));
        element.sendKeys(Keys.ENTER);
        $$(By.className(cardContainer)).last().shouldBe(visible);
    }

    private int getFilterPrice(By locator) {
        String priceText = Objects.requireNonNull($(locator).getAttribute("value")).replaceAll("\\D", "");
        if (priceText.isEmpty()) {
            throw new IllegalStateException("Price filter is missing or empty.");
        }
        return Integer.parseInt(priceText);
    }
}

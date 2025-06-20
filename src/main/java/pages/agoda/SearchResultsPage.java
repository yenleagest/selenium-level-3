package pages.agoda;

import com.codeborne.selenide.SelenideElement;
import data.enums.SortBy;
import data.models.Hotel;
import data.models.PriceFilter;
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
    private final By currencySymbol = By.className("PriceFilter-searchbox__pricesymbol");
    // dynamic locators
    private final String sortBy = "[data-element-name='%s']";
    private final String rating = "#filter-menu-StarRatingWithLuxury ~ ul [data-element-value='%d']";
    // collection locators
    private final String cardContainer = "PropertyCardItem";
    private final String finalPrice = "[data-element-name='final-price']";
    private final String destination = "[data-selenium='area-city']";
    private final String starContainer = "[data-testid='rating-container'] span";

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

    public String getCurrencySymbol() {
        return $(currencySymbol).getText().trim();
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

    private Hotel getHotel(SelenideElement container) {
        return new Hotel(extractDestination(container), extractPrice(container), extractRating(container));
    }

    private String extractDestination(SelenideElement container) {
        String dest = container.$(destination).shouldBe(visible).getText().trim();
        if (dest.isEmpty()) {
            throw new IllegalStateException("Destination is missing or empty for a hotel card.");
        }
        return dest;
    }

    private int extractPrice(SelenideElement container) {
        String priceText = container.$(finalPrice).shouldBe(visible).getText().replaceAll("\\D", "");
        if (priceText.isEmpty()) {
            throw new IllegalStateException("Price is missing or empty for a hotel card.");
        }
        return Integer.parseInt(priceText);
    }

    private int extractRating(SelenideElement container) {
        String text = container.$(starContainer).shouldBe(visible).getText().split(" ")[0];
        return (int) Math.floor(Double.parseDouble(text));
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

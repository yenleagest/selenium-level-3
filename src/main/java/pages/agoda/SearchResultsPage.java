package pages.agoda;

import com.codeborne.selenide.SelenideElement;
import data.enums.SortBy;
import data.models.CardContainer;
import elements.Element;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;

@Slf4j
public class SearchResultsPage extends HomePage {

    private final Element minPrice = new Element("#SideBarLocationFilters #price_box_0");
    private final Element maxPrice = new Element("#SideBarLocationFilters #price_box_1");
    private final Element currencySymbol = new Element(By.className("PriceFilter-searchbox__pricesymbol"));
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
        Element sortElement = new Element(this.sortBy.formatted(sortBy.getDataElementName()));
        sortElement.getRaw().scrollIntoView("{block: 'center'}");
        sortElement.click();
    }

    @Step("Filter price from {min} to {max}")
    public void filterPrice(int min, int max) {
        // set min price and wait for the results to be filtered
        minPrice.setValue(String.valueOf(min));
        minPrice.getRaw().sendKeys(Keys.ENTER);
        Element.getCollection(By.className(cardContainer)).last().shouldBe(visible);

        // set max price and wait for the results to be filtered
        maxPrice.setValue(String.valueOf(max));
        maxPrice.getRaw().sendKeys(Keys.ENTER);
        Element.getCollection(By.className(cardContainer)).last().shouldBe(visible);
    }

    @Step("Get currency symbol")
    public String getCurrencySymbol() {
        String symbol = currencySymbol.getRaw().getText().trim();
        log.info("Currency symbol: {}", symbol);
        return symbol;
    }

    @Step("Filter result with {rating}-star rating")
    public void filterStar(int rating) {
        Element element = new Element(this.rating.formatted(rating));
        element.getRaw().scrollIntoView(true).click();
    }

    @Step("Is {rating}-star rating selected")
    public boolean isStarRatingSelected(int rating) {
        Element element = new Element(this.rating.formatted(rating), "input");
        return element.getRaw().isSelected();
    }

    @Step("Get price filter slider values")
    public List<Integer> getPriceFilterValues() {
        List<String> sliderValues = new ArrayList<>();
        sliderValues.add(Objects.requireNonNull(minPrice).getRaw().getAttribute("value"));
        sliderValues.add(Objects.requireNonNull(maxPrice).getRaw().getAttribute("value"));
        log.info("Price filter slider values: {}", sliderValues);

        return CollectionUtils.getPriceFromText(sliderValues);
    }

    @Step("Get hotel details for the first {count} results")
    public List<CardContainer> getHotels(int count) {
        List<CardContainer> cardContainers = new ArrayList<>();

        int retries = 0;
        // scroll into center of each card
        // for card loaded lazily, wait for it and its attribute to be displayed
        while (cardContainers.size() < count && retries++ < 2 * count) {
            int currIndex = cardContainers.size();
            SelenideElement card = Element.getCollection(By.className(cardContainer)).get(currIndex).$(":first-child");

            card.scrollIntoView("{block: 'center'}").shouldBe(visible);
            card.$(finalPrice).shouldBe(visible);
            card.$(destination).shouldBe(visible);
            card.$(starContainer).shouldBe(visible);

            CardContainer hotel = getHotel(card);
            cardContainers.add(hotel);
            log.info("Hotel details [{}]: {}", cardContainers.size(), hotel);
        }

        return cardContainers;
    }

    private CardContainer getHotel(SelenideElement container) {
        String dest = container.$(destination).getText();
        if (dest.trim().isEmpty()) {
            throw new IllegalStateException("Destination is missing or empty for a hotel card.");
        }

        String priceText = container.$(finalPrice).getText().replaceAll("\\D", "");
        if (priceText.isEmpty()) {
            throw new IllegalStateException("Price is missing or empty for a hotel card.");
        }

        int price = Integer.parseInt(priceText);

        String text = container.$(starContainer).getText().split(" ")[0];
        int rating = (int) Math.floor(Double.parseDouble(text));

        return new CardContainer(dest.trim(), price, rating);
    }
}

package pages.agoda;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.models.agoda.Hotel;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class HotelDetailsPage extends HomePage {

    private final By benefits = By.cssSelector("div[role='listitem']");
    private final By price = By.cssSelector("#hotelNavBar div[class*='priceDetail']");
    private final By destination = By.cssSelector("[data-selenium='hotel-address-map']");
    private final By hotelName = By.cssSelector("[data-selenium='hotel-header-name']");
    private final By rating = By.cssSelector("[data-selenium='mosaic-hotel-rating']");
    private final By reviewToolTipBtn = By.cssSelector("[aria-label='Show reviews breakdown']");
    private final By reviewToolTipDetails = By.cssSelector(".ReviewProgressTooltip ~ div span");

    @Step("Get hotel details")
    public Hotel getHotel() {
        Hotel hotel = new Hotel(getHotelName(), getDestination(), getPrice(), getRating(), getBenefits());
        log.info("Hotel details: {}", hotel);
        return hotel;
    }

    @Step("Show review details")
    public List<String> showReviewDetails() {
        SelenideElement reviewToolTip = $(reviewToolTipBtn).shouldBe(visible);
        reviewToolTip.hover();
        List<String> details = $$(reviewToolTipDetails).stream()
                .map(e -> e.shouldBe(visible).getText())
                .toList();
        log.info("Review details: {}", details);
        return details;
    }

    @Step("Get hotel's destination")
    private String getDestination() {
        return $(destination).shouldBe(visible).shouldNotHave(Condition.exactText("")).getText().trim();
    }

    @Step("Get hotel's name")
    private String getHotelName() {
        String name = $(hotelName).shouldBe(visible).getText().trim();
        if (name.isEmpty()) {
            throw new IllegalStateException("Hotel name is missing or empty for a hotel card.");
        }
        return name;
    }

    @Step("Get hotel's price")
    private int getPrice() {
        String priceText = $(price).shouldBe(visible).getText().replaceAll("\\D", "");
        if (priceText.isEmpty()) {
            throw new IllegalStateException("Price is missing or empty for a hotel card.");
        }
        return Integer.parseInt(priceText);
    }

    @Step("Get hotel's rating")
    private float getRating() {
        String text = $(rating).shouldBe(visible).getText().split(" ")[0];
        return Float.parseFloat(text);
    }

    @Step("Get hotel's benefits")
    private List<String> getBenefits() {
        return $$(benefits).stream()
                .map(SelenideElement::getText)
                .filter(text -> !text.isEmpty())
                .toList();
    }
}

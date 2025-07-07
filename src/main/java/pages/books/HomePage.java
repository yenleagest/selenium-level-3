package pages.books;

import com.codeborne.selenide.SelenideElement;
import data.enums.books.LocatorStrategy;
import drivers.DriverUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ShadowDomUtils;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;
import static common.Constants.TIMEOUT_SECONDS;

public class HomePage {

    protected final WebDriverWait wait = new WebDriverWait(DriverUtils.getDriver(), TIMEOUT_SECONDS);
    private final String searchLocator = "input#input";

    @Step("Search for books with keyword: {keyword}")
    public void searchForBook(LocatorStrategy strategy, String keyword) {
        if (strategy == LocatorStrategy.SELENIDE) {
            searchWithSelenide(keyword);
        } else {
            searchWithSelenium(keyword);
        }
    }

    private void searchWithSelenide(String keyword) {
        SelenideElement element = $(shadowDeepCss(searchLocator));
        element.setValue(keyword);
        element.sendKeys(Keys.ENTER);
    }

    private void searchWithSelenium(String keyword) {
        WebElement element = ShadowDomUtils
                .start()
                .through("book-app")
                .get(searchLocator);
        element.sendKeys(keyword + Keys.ENTER);
    }
}

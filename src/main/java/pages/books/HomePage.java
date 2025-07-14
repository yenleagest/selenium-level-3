package pages.books;

import com.codeborne.selenide.SelenideElement;
import data.enums.books.LocatorStrategy;
import drivers.DriverUtils;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yenleagest.ShadowUtils;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;
import static common.Constants.TIMEOUT_SECONDS;

public class HomePage {

    protected final WebDriverWait wait = new WebDriverWait(DriverUtils.getDriver(), TIMEOUT_SECONDS);
    protected final WebDriver driver = DriverUtils.getDriver();
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
        driver.findElement(ShadowUtils.byShadowCss(searchLocator)).sendKeys(keyword + Keys.ENTER);
    }
}

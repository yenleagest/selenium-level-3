package pages.books;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;

public class HomePage {

    private final String searchBox = "input#input";

    @Step("Search for books with keyword: {keyword}")
    public void searchForBook(String keyword) {
        SelenideElement element = $(shadowDeepCss(searchBox));
        element.click();
        element.setValue(keyword);
        element.sendKeys(Keys.ENTER);
    }
}

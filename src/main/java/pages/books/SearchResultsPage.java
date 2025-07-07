package pages.books;

import com.codeborne.selenide.CollectionCondition;
import data.enums.books.LocatorStrategy;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import utils.ShadowDomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class SearchResultsPage extends HomePage {

    @Step("Do all search results contain {keyword}")
    public boolean doAllResultsContainKeyword(LocatorStrategy strategy, String keyword) {
        List<String> titles = strategy == LocatorStrategy.SELENIDE ? getBookTitleWithSelenide() : getBookTitleFromWithSelenium();
        return Objects.requireNonNull(titles).stream()
                      .allMatch(title -> title.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Step("Get book title from search results")
    private List<String> getBookTitleWithSelenide() {
        try {
            $$(shadowDeepCss(".books li .placeholder[fadeout]")).shouldBe(CollectionCondition.sizeGreaterThan(0));
        } catch (AssertionError e) {
            log.error("No search results displayed for the given keyword: {}", e.getMessage());
            return List.of(); // return an empty list if no results are found
        }
        List<String> titles = $$(shadowDeepCss(".books li .title-container")).stream()
                                                                             .map(el -> el.getText().trim())
                                                                             .toList();
        log.info("Results title: {}", titles);
        return titles;
    }

    @Step("Get book title from search results")
    private List<String> getBookTitleFromWithSelenium() {
        List<String> titles = new ArrayList<>();
        // wait for search results
        wait.until(driver -> ShadowDomUtils
                .start()
                .through("book-app")
                .get("book-explore")
                .getAttribute("active") != null);

        List<WebElement> books = getAllResults();

        for (int i = 0; i < books.size(); i++) {
            WebElement bookItem = books.get(i);

            waitForResultToLoad(bookItem);

            // re-fetch the list of books in case new results were loaded
            books = getAllResults();

            titles.add(getTitle(bookItem));
        }
        log.info("Results title: {}", titles);
        return titles;
    }

    private List<WebElement> getAllResults() {
        return ShadowDomUtils
                .start()
                .through("book-app")
                .through("book-explore[active]")
                .getAll("book-item");
    }

    private void waitForResultToLoad(WebElement element) {
        WebElement placeholder = ShadowDomUtils
                .start()
                .through(element)
                .get(".placeholder");
        wait.until(driver -> placeholder.getAttribute("fadeout") != null);
    }

    private String getTitle(WebElement element) {
        return ShadowDomUtils
                .start()
                .through(element)
                .get(".title-container").getText().trim();
    }
}

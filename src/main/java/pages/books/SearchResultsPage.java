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
        List<WebElement> books = getAllResultsWithRetry();

        for (WebElement bookItem : books) {
            waitForResultToLoad(bookItem);
            titles.add(getTitle(bookItem));
        }
        log.info("Results title: {}", titles);
        return titles;
    }

    private List<WebElement> getAllResultsWithRetry() {
        waitForResultsToLoad();

        List<WebElement> books = getAllResults();
        int previousSize = books.size();
        int counter = 0;
        int maxRetries = 2;

        while (counter < maxRetries) {
            if (!books.isEmpty())
                waitForResultToLoad(books.getLast());
            List<WebElement> updated = getAllResults();
            if (updated.size() > previousSize) {
                log.info("New results loaded, re-fetching until it settles.");
                books = updated;
                previousSize = updated.size();
                counter = 0;
            } else {
                log.info("No new results loaded, retrying...[{}/{}].", counter + 1, maxRetries);
                counter++;
            }
        }
        return books;
    }

    private void waitForResultsToLoad() {
        wait.until(driver -> ShadowDomUtils
                .start()
                .through("book-app")
                .get("book-explore")
                .getAttribute("active") != null);
    }

    private List<WebElement> getAllResults() {
        return ShadowDomUtils
                .start()
                .through("book-app")
                .through("book-explore[active]")
                .getAll("book-item");
    }

    private void waitForResultToLoad(WebElement element) {
        wait.until(driver -> ShadowDomUtils
                .start()
                .through(element)
                .get(".placeholder").getAttribute("fadeout") != null);
    }

    private String getTitle(WebElement element) {
        return ShadowDomUtils
                .start()
                .through(element)
                .get(".title-container").getText().trim();
    }
}

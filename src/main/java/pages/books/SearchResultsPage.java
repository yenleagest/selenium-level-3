package pages.books;

import com.codeborne.selenide.CollectionCondition;
import data.enums.books.LocatorStrategy;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.yenleagest.ShadowUtils;

import java.util.List;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$$;
import static utils.BooksUtils.filterResults;

@Slf4j
public class SearchResultsPage extends HomePage {

    @Step("Do all search results contain {keyword}")
    public boolean doAllResultsContainKeyword(LocatorStrategy strategy, String keyword) {
        List<String> titles = strategy == LocatorStrategy.SELENIDE ? getBookTitleWithSelenide() : getBookTitleFromWithSelenium();
        List<String> nonMatchingTitles = filterResults(titles, keyword);

        return nonMatchingTitles.isEmpty();
    }

    @Step("Get book title from search results")
    private List<String> getBookTitleWithSelenide() {
        try {
            $$(shadowDeepCss(".books li .placeholder[fadeout]")).shouldBe(CollectionCondition.sizeGreaterThan(0));
        } catch (AssertionError e) {
            log.error("No search results displayed for the given keyword: {}", e.getMessage());
            return List.of(); // return an empty list if no results are found
        }
        return $$(shadowDeepCss(".books li .title-container")).stream()
                                                              .map(el -> el.getText().trim()).toList();
    }

    @Step("Get book title from search results")
    private List<String> getBookTitleFromWithSelenium() {
        waitForResultsToLoad();
        return getAllResultsWithRetry().stream().map(el -> el.getText().trim())
                                       .toList();
    }

    private List<WebElement> getAllResultsWithRetry() {
        List<WebElement> books = getAllResults();
        int previousSize = books.size();
        int counter = 0;
        int maxRetries = 2;

        while (counter < maxRetries) {
            if (!books.isEmpty())
                waitForResultToDisplay();
            if (getAllResults().size() > previousSize) {
                log.info("New results loaded, re-fetching until it settles.");
                books = getAllResults();
                previousSize = getAllResults().size();
                counter = 0;
            } else {
                log.info("No new results loaded, retrying...[{}/{}].", counter + 1, maxRetries);
                counter++;
            }
        }
        return books;
    }

    private void waitForResultsToLoad() {
        WebElement bookExplore = driver.findElement(ShadowUtils.byShadowCss("book-app book-explore"));
        wait.until(ExpectedConditions.domAttributeToBe(bookExplore, "active", ""));
    }

    private void waitForResultToDisplay() {
        WebElement placeholder = driver.findElement(ShadowUtils.byShadowCss(".books li .placeholder"));
        wait.until(ExpectedConditions.domAttributeToBe(placeholder, "fadeout", ""));
    }

    private List<WebElement> getAllResults() {
        return driver.findElements(ShadowUtils.byShadowCss("book-app book-explore[active] book-item .title-container"));
    }
}

package pages.books;

import com.codeborne.selenide.CollectionCondition;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class SearchResultsPage extends HomePage {

    private final String bookTitle = ".books li .title-container";
    private final String loadedResult = ".books li .placeholder[fadeout]";

    @Step("Do all search results contain {keyword}")
    public boolean doAllResultsContainKeyword(String keyword) {
        List<String> titles = getBookTitleFromSearchResults();
        return Objects.requireNonNull(titles).stream()
                .allMatch(title -> title.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Step("Get book title from search results")
    private List<String> getBookTitleFromSearchResults() {
        try {
            $$(shadowDeepCss(loadedResult)).shouldBe(CollectionCondition.sizeGreaterThan(0));
        } catch (AssertionError e) {
            log.error("No search results displayed for the given keyword: {}", e.getMessage());
            return List.of(); // return an empty list if no results are found
        }
        List<String> titles = $$(shadowDeepCss(bookTitle)).stream()
                .map(el -> el.getText().trim())
                .toList();
        log.info("Results title: {}", titles);
        return titles;
    }
}


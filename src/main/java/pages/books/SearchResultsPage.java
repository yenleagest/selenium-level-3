package pages.books;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
public class SearchResultsPage extends HomePage {

    private final String bookTitle = ".books li .title-container";
    private final String bookImage = ".books li img";

    @Step("Do all search results contain {keyword}")
    public boolean doAllResultsContainKeyword(String keyword) {
        List<String> titles = getBookTitleFromSearchResults();
        return titles.stream()
                .allMatch(title -> title.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Step("Get book title from search results")
    private List<String> getBookTitleFromSearchResults() {
        ElementsCollection images = $$(shadowDeepCss(bookImage)).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection titles = $$(shadowDeepCss(bookTitle));

        // keep scrolling and checking for new results
        // if the number of results does not increase after scrolling, increase retry count
        // if new results appear, reset retry and re-evaluate until max retries is reached
        int retry = 0;
        while (retry < 3) {
            images.last().scrollIntoView("{block: 'center'}");
            images.forEach(el -> el.shouldBe(image));
            if (titles.size() == $$(shadowDeepCss(bookTitle)).size()) {
                retry++;
            } else {
                titles = $$(shadowDeepCss(bookTitle));
                retry = 0;
            }
        }

        List<String> extractedTitles = titles.stream()
                .map(el -> {
                    el.shouldNotHave(Condition.exactText(""));
                    return el.getText().trim();
                })
                .toList();
        log.info("Results title: {}", extractedTitles);
        return extractedTitles;
    }
}


package pages.leapfrog;

import data.models.leapfrog.GameInfo;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.ExcelUtils;
import utils.GameInfoUtils.GameMismatch;
import utils.HtmlParser;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static utils.GameInfoUtils.findMismatchedGames;
import static utils.GameInfoUtils.findMissingGames;
import static utils.GameInfoUtils.logAndReportMismatch;
import static utils.GameInfoUtils.logAndReportMissing;
import static utils.GameInfoUtils.mapGameInfoByTitle;

@Slf4j
public class HomePage {

    private final By paginator = By.cssSelector(".top option[selected='selected']");

    @Step("Extract total number of pages from the paginator")
    public int getNumberOfPages() {
        return Integer.parseInt($(paginator).getText().split("of")[1].trim());
    }

    @Step("Verify that game info on the UI matches expected data from Excel file")
    public boolean doesGameInfoMatch(int total) {
        List<GameInfo> expected = ExcelUtils.getLeapFrogGameInfo();
        List<GameInfo> actual = HtmlParser.fromHttpClient(total);
        Map<String, GameInfo> actualMap = mapGameInfoByTitle(actual);

        List<GameInfo> missing = findMissingGames(expected, actualMap);
        List<GameMismatch> mismatch = findMismatchedGames(expected, actualMap);

        logAndReportMissing(missing);
        logAndReportMismatch(mismatch);

        return missing.isEmpty() && mismatch.isEmpty();
    }
}

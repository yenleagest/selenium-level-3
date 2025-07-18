package pages.leapfrog;

import data.models.leapfrog.GameInfo;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.ExcelUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static utils.GameInfoUtils.buildUIGameMap;
import static utils.GameInfoUtils.compareGameInfo;
import static utils.GameInfoUtils.logAndReportMismatch;
import static utils.GameInfoUtils.logAndReportMissing;

@Slf4j
public class HomePage {

    private final By paginator = By.cssSelector(".top option[selected='selected']");

    @Step("Extract total number of pages from the paginator")
    public int getNumberOfPages() {
        return Integer.parseInt($(paginator).getText().split("of")[1].trim());
    }

    @Step("Verify that game info on the UI matches expected data from Excel file")
    public boolean doesGameInfoMatch(int numberOfPages) {
        HashMap<Integer, GameInfo> expected = ExcelUtils.getLeapFrogGameInfo();
        HashMap<String, GameInfo> actualMap = buildUIGameMap(numberOfPages);

        List<GameInfo> missing = new ArrayList<>();
        HashMap<String, GameInfo> mismatch = new HashMap<>();
        compareGameInfo(expected, actualMap, missing, mismatch);

        logAndReportMissing(missing, expected);
        logAndReportMismatch(mismatch, expected);

        return missing.isEmpty() && mismatch.isEmpty();
    }
}

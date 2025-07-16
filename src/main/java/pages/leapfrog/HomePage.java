package pages.leapfrog;

import data.models.leapfrog.GameInfo;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import reports.AllureManager;
import utils.ExcelUtils;
import utils.HtmlParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

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

    private HashMap<String, GameInfo> buildUIGameMap(int numberOfPages) {
        List<GameInfo> uiInfo = HtmlParser.fromHttpClient(numberOfPages);
        return uiInfo.stream()
                     .collect(Collectors.toMap(GameInfo::getTitle, Function.identity(), (existing, duplicate) -> existing, HashMap::new));
    }

    private void compareGameInfo(HashMap<Integer, GameInfo> expected, HashMap<String, GameInfo> actualMap,
                                 List<GameInfo> missing, HashMap<String, GameInfo> mismatch) {
        for (HashMap.Entry<Integer, GameInfo> entry : expected.entrySet()) {
            GameInfo expectedGame = entry.getValue();
            GameInfo actual = actualMap.get(expectedGame.getTitle());
            if (actual == null) {
                missing.add(expectedGame);
            } else if (!actual.equals(expectedGame)) {
                mismatch.put(expectedGame.getTitle(), actual);
            }
        }
    }

    private void logAndReportMissing(List<GameInfo> missing, HashMap<Integer, GameInfo> expected) {
        if (!missing.isEmpty()) {
            String allMissing = expected.entrySet().stream()
                                        .filter(entry -> missing.contains(entry.getValue()))
                                        .map(entry -> "Row %d: %s".formatted(entry.getKey(), entry.getValue()))
                                        .collect(Collectors.joining("\n"));
            AllureManager.saveLog("These %d games were not displayed on the UI:".formatted(missing.size()), allMissing);
        }
    }

    private void logAndReportMismatch(HashMap<String, GameInfo> mismatch, HashMap<Integer, GameInfo> expected) {
        if (!mismatch.isEmpty()) {
            String mismatchDetails = expected.entrySet().stream()
                                             .filter(entry -> mismatch.containsKey(entry.getValue().getTitle()))
                                             .map(entry -> {
                                                 int row = entry.getKey();
                                                 GameInfo expectedInfo = entry.getValue();
                                                 GameInfo actual = mismatch.get(expectedInfo.getTitle());
                                                 return "Row %d:\nExpected: %s\nActual  : %s".formatted(row, expectedInfo, actual);
                                             })
                                             .collect(Collectors.joining("\n\n"));
            AllureManager.saveLog("These %d games were displayed on the UI but have mismatched info:".formatted(mismatch.size()), mismatchDetails);
        }
    }
}

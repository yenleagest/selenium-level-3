package utils;

import data.models.leapfrog.GameInfo;
import reports.AllureManager;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameInfoUtils {

    /**
     * utility class for processing and logging leapfrog game information
     * <p>
     * this class provides static methods to:
     * <ul>
     *     <li>build a hashmap of game information from html pages, keyed by title</li>
     *     <li>compare actual ui data with expected excel data</li>
     *     <li>log mismatches and missing entries to allure reports</li>
     * </ul>
     * <p>
     * it is designed to help in content testing workflows by handling the parsing,
     * mapping, and verification of {@link GameInfo} objects across multiple sources.
     */

    public static HashMap<String, GameInfo> buildUIGameMap(int total) {
        List<GameInfo> uiInfo = HtmlParser.fromHttpClient(total);
        return uiInfo.stream().collect(Collectors.toMap(
                GameInfo::getTitle, Function.identity(), (a, b) -> {
                    throw new IllegalStateException("Duplicated games found: %s and %s".formatted(a, b));
                }, HashMap::new
        ));
    }

    public static void compareGameInfo(HashMap<Integer, GameInfo> expected, HashMap<String, GameInfo> actualMap, List<GameInfo> missing, HashMap<String, GameInfo> mismatch) {
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

    public static void logAndReportMissing(List<GameInfo> missing, HashMap<Integer, GameInfo> expected) {
        if (!missing.isEmpty()) {
            String allMissing = expected
                    .entrySet()
                    .stream()
                    .filter(entry -> missing.contains(entry.getValue()))
                    .map(entry -> "Row %d: %s".formatted(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("\n"));
            AllureManager.saveLog("These %d games were not displayed on the UI:".formatted(missing.size()), allMissing);
        }
    }

    public static void logAndReportMismatch(HashMap<String, GameInfo> mismatch, HashMap<Integer, GameInfo> expected) {
        if (!mismatch.isEmpty()) {
            String mismatchDetails = expected.entrySet().stream().filter(entry -> mismatch.containsKey(entry.getValue().getTitle())).map(entry -> {
                int row = entry.getKey();
                GameInfo expectedInfo = entry.getValue();
                GameInfo actual = mismatch.get(expectedInfo.getTitle());
                return "Row %d:\nExpected: %s\nActual  : %s".formatted(row, expectedInfo, actual);
            }).collect(Collectors.joining("\n\n"));
            AllureManager.saveLog("These %d games were displayed on the UI but have mismatched info:".formatted(mismatch.size()), mismatchDetails);
        }
    }
}

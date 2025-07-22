package utils;

import data.models.leapfrog.GameInfo;
import reports.AllureManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public record GameMismatch(GameInfo expected, GameInfo actual) {
    }

    public static Map<String, GameInfo> mapGameInfoByTitle(List<GameInfo> gameInfos) {
        return gameInfos.stream()
                        .collect(Collectors.toMap(
                                GameInfo::getTitle,
                                Function.identity(),
                                (a, b) -> {
                                    throw new IllegalStateException("Duplicate titles found: %s and %s".formatted(a, b));
                                },
                                HashMap::new
                        ));
    }

    public static List<GameInfo> findMissingGames(List<GameInfo> expected, Map<String, GameInfo> actualMap) {
        return expected.stream()
                       .filter(e -> !actualMap.containsKey(e.getTitle()))
                       .toList();
    }

    public static List<GameMismatch> findMismatchedGames(List<GameInfo> expected, Map<String, GameInfo> actualMap) {
        return expected.stream()
                       .filter(e -> actualMap.containsKey(e.getTitle()) && isMismatched(e, actualMap.get(e.getTitle())))
                       .map(e -> new GameMismatch(e, actualMap.get(e.getTitle())))
                       .toList();
    }

    public static void logAndReportMissing(List<GameInfo> missing) {
        if (!missing.isEmpty()) {
            String allMissing = missing.stream()
                                       .map(game -> """
                                               [Missing: Row %d]
                                                   Title= %s  •  Age= %s  •  Price= %s
                                               -----------------------------------------------------------------------------
                                               """.formatted(
                                               game.getIndex(),
                                               game.getTitle(),
                                               game.getAgeRange(),
                                               game.getPrice()
                                       ).trim())
                                       .collect(Collectors.joining("\n\n"));
            AllureManager.saveLog("These %d games were not displayed on the UI:".formatted(missing.size()), allMissing);
        }
    }

    public static void logAndReportMismatch(List<GameMismatch> mismatches) {
        if (mismatches.isEmpty()) return;

        String mismatchDetails = mismatches.stream()
                                           .map(m -> {
                                               GameInfo expected = m.expected();
                                               GameInfo actual = m.actual();
                                               return """
                                                       [Expected: Row %d]
                                                           Title= %s  •  Age= %s  •  Price= %s
                                                       
                                                       [Actual: Page %d]
                                                           Title= %s  •  Age= %s  •  Price= %s
                                                       
                                                       [Mismatched Info]
                                                       %s
                                                       -----------------------------------------------------------------------------
                                                       """.formatted(
                                                       expected.getIndex(),
                                                       expected.getTitle(),
                                                       expected.getAgeRange(),
                                                       expected.getPrice(),
                                                       actual.getIndex(),
                                                       actual.getTitle(),
                                                       actual.getAgeRange(),
                                                       actual.getPrice(),
                                                       getMismatchDetails(expected, actual)
                                               ).trim();
                                           })
                                           .collect(Collectors.joining("\n\n"));

        AllureManager.saveLog(
                "These %d games were displayed on the UI but have mismatched info:".formatted(mismatches.size()),
                mismatchDetails
        );
    }

    private static boolean isMismatched(GameInfo expected, GameInfo actual) {
        if (!expected.getTitle().equals(actual.getTitle())) return true;
        if (!expected.getAgeRange().equals(actual.getAgeRange())) return true;
        if (!expected.getPrice().equals(actual.getPrice())) return true;
        return false;
    }

    private static String getMismatchDetails(GameInfo expected, GameInfo actual) {
        List<String> lines = new ArrayList<>();
        if (!expected.getAgeRange().equals(actual.getAgeRange()))
            lines.add("    Age    : expected '%s' but got '%s'".formatted(expected.getAgeRange(), actual.getAgeRange()));
        if (!expected.getPrice().equals(actual.getPrice()))
            lines.add("    Price  : expected '%s' but got '%s'".formatted(expected.getPrice(), actual.getPrice()));
        return String.join("\n", lines);
    }
}

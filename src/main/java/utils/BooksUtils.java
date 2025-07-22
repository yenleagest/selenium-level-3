package utils;

import reports.AllureManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BooksUtils {


    public static List<String> filterResults(List<String> titles, String keyword) {
        AllureManager.saveLog("Extracted titles:", formatResults(titles));

        List<String> nonMatchingTitles = getNonMatchingTitles(titles, keyword);
        List<String> matchingTitles = getMatchingTitles(titles, keyword);
        logAndReport(keyword, nonMatchingTitles, matchingTitles);
        return nonMatchingTitles;
    }

    private static List<String> getMatchingTitles(List<String> titles, String keyword) {
        return titles.stream()
                     .filter(title -> title.toLowerCase().contains(keyword.toLowerCase()))
                     .collect(Collectors.toList());
    }

    private static List<String> getNonMatchingTitles(List<String> titles, String keyword) {
        return Objects.requireNonNull(titles).stream()
                      .filter(title -> !title.toLowerCase().contains(keyword.toLowerCase()))
                      .toList();
    }

    private static void logAndReport(String keyword, List<String> nonMatchingTitles, List<String> matchingTitles) {
        if (nonMatchingTitles.isEmpty()) {
            AllureManager.saveLog("All titles contain the keyword '%s'.".formatted(keyword), formatResults(matchingTitles));
        } else {
            AllureManager.saveLog(
                    "%d title(s) do not contain the keyword '%s':".formatted(nonMatchingTitles.size(), keyword),
                    formatResults(nonMatchingTitles)
            );
            AllureManager.saveLog(
                    "%d matching title(s) found for keyword '%s':".formatted(matchingTitles.size(), keyword),
                    formatResults(matchingTitles)
            );
        }
    }

    private static String formatResults(List<String> results) {
        return results.stream().map(t -> "- " + t).collect(Collectors.joining("\n"));
    }
}

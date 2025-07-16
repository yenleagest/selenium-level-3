package utils;

import clients.LeapFrogHttpClient;
import data.models.leapfrog.GameInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class HtmlParser {

    /**
     * parses game information from multiple HTML pages fetched via the LeapFrogHttpClient using {@link Jsoup}
     * <p>
     * this method calls the LeapFrogHttpClient to retrieve {@code total} number of HTML pages
     * and for each page, it extracts titles, age ranges, and prices using CSS selectors
     * the extracted raw strings are then mapped into structured {@link GameInfo} objects
     * </p>
     *
     * @param total the number of pages to fetch and parse
     * @return a list of {@link GameInfo} objects aggregated from all pages
     * @throws IllegalStateException if the extracted lists of titles, ages, and prices do not match in size
     */
    public static List<GameInfo> fromHttpClient(int total) {
        List<String> metadata = LeapFrogHttpClient.getAll(total);
        List<GameInfo> result = new ArrayList<>();

        for (String html : metadata) {
            List<String> titles = extractTitles(html);
            List<String> ages = extractAgeRanges(html);
            List<String> prices = extractPrices(html);

            ensureDataSizeMatch(titles, ages, prices);
            List<GameInfo> games = mapToGameInfo(titles, ages, prices);

            result.addAll(games);
        }

        return result;
    }

    private static void ensureDataSizeMatch(List<String> titles, List<String> ages, List<String> prices) {
        if (titles.size() != ages.size() || titles.size() != prices.size()) {
            throw new IllegalStateException("Mismatched sizes in extracted data - Titles: %d, Ages: %d, Prices: %d"
                                                    .formatted(titles.size(), ages.size(), prices.size()));
        }
    }

    private static List<GameInfo> mapToGameInfo(List<String> titles, List<String> ages, List<String> prices) {
        return IntStream.range(0, titles.size())
                        .mapToObj(i -> new GameInfo(titles.get(i), ages.get(i), prices.get(i)))
                        .toList();
    }

    private static List<String> extractTitles(String html) {
        return fromHtml(html, "p.heading");
    }

    private static List<String> extractAgeRanges(String html) {
        return fromHtml(html, "p.ageDisplay");
    }

    private static List<String> extractPrices(String html) {
        return fromHtml(html, "span.single.price:not(.strike)").stream()
                                                               .map(price -> price.contains(":") ? price.split(":", 2)[1].trim() : price)
                                                               .toList();
    }

    private static List<String> fromHtml(String html, String cssLocator) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(cssLocator);
        return elements.stream()
                       .map(e -> e.text().trim())
                       .toList();
    }
}

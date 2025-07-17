package utils;

import clients.LeapFrogHttpClient;
import data.models.leapfrog.GameInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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
            List<GameInfo> games = extractGameInfos(html);
            result.addAll(games);
        }
        return result;
    }

    private static List<GameInfo> extractGameInfos(String html) {
        Document doc = Jsoup.parse(html);
        Elements items = doc.select(".resultList .catalog-product");

        return items.stream()
                    .map(item -> {
                        String title = item.select("p.heading").text().trim();
                        String age = item.select("p.ageDisplay").text().trim();
                        String priceRaw = item.select("span.single.price:not(.strike)").text().trim();
                        String price = priceRaw.contains(":") ? priceRaw.split(":", 2)[1].trim() : priceRaw;

                        return new GameInfo(title, age, price);
                    })
                    .toList();
    }
}

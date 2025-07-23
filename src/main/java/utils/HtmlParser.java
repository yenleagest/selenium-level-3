package utils;

import clients.LeapFrogHttpClient;
import data.models.leapfrog.GameInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import reports.AllureManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @return a list of {@link GameInfo} objects with page index set
     */
    public static List<GameInfo> fromHttpClient(int total) {
        Map<Integer, String> metadata = LeapFrogHttpClient.getAll(total);
        List<GameInfo> result = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : metadata.entrySet()) {
            int pageNum = entry.getKey();
            String html = entry.getValue();
            List<GameInfo> games = extractGameInfos(pageNum, html);
            games.forEach(g -> g.setIndex(pageNum));
            AllureManager.saveLog("Page %d found %d games".formatted(pageNum, games.size()), String.join("\n", games.stream().map(Object::toString).toList()));
            result.addAll(games);
        }
        return result;
    }

    private static List<GameInfo> extractGameInfos(int pageNum, String html) {
        Document doc = Jsoup.parse(html);
        Elements items = doc.select(".resultList .catalog-product");

        return items.stream()
                    .map(item -> {
                        String title = item.select("p.heading").text().trim();
                        String age = item.select("p.ageDisplay").text().replaceAll("\\s*-\\s*", "-").trim(); // to remove the space before the dash in 'Ages 4 -7 years'
                        String priceRaw = item.select("span.single.price:not(.strike)").text().trim();
                        String price = priceRaw.contains(":") ? priceRaw.split(":", 2)[1].trim() : priceRaw;

                        return new GameInfo(pageNum, title, age, price);
                    })
                    .toList();
    }
}

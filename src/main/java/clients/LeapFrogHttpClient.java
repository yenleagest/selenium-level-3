package clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import reports.AllureManager;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static common.Constants.LEAP_FROG_PRODUCT_PAGE;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
public class LeapFrogHttpClient {

    /**
     * fetches a list of HTML content pages from the LeapFrog product page
     * <p>
     * this method spawns a fixed thread pool of size {@code total} to fetch each page concurrently
     * each page corresponds to a specific page number formatted into the URL {@link common.Constants#LEAP_FROG_PRODUCT_PAGE}
     * </p>
     *
     * @param total the number of pages to fetch
     * @return a thread-safe map of page numbers to HTML page responses (as Strings)
     */
    public static Map<Integer, String> getAll(int total) {
        ExecutorService executor = newFixedThreadPool(total);
        Map<Integer, String> results = submitTasks(executor, total);
        shutdownExecutor(executor);
        return results;
    }

    private static Map<Integer, String> submitTasks(ExecutorService executor, int total) {
        Map<Integer, String> resultMap = Collections.synchronizedMap(new HashMap<>());

        for (int i = 1; i <= total; i++) {
            final int pageNum = i;
            executor.submit(() -> {
                String url = LEAP_FROG_PRODUCT_PAGE.formatted(pageNum);
                String startTimestamp = LocalTime.now().toString();
                long startTime = System.currentTimeMillis();
                log.info("[{}] Start fetching page {}", startTimestamp, pageNum);
                try {
                    String result = get(url);
                    resultMap.put(pageNum, result);
                    String endTimestamp = LocalTime.now().toString();
                    log.info("[{}] Finished fetching page {} (duration: {} s)", endTimestamp, pageNum, String.format("%.2f", (System.currentTimeMillis() - startTime) / 1000.0));
                } catch (Exception e) {
                    log.error("Failed to fetch page {}: {}", pageNum, e.getMessage());
                    resultMap.put(pageNum, "");
                }
            });
        }

        return resultMap;
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(10, TimeUnit.MINUTES);
            if (!finished) {
                log.error("Executor did not terminate in the allotted time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Executor termination interrupted.");
        }
    }

    private static String get(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest request = new HttpGet(url);

            HttpClientResponseHandler<String> handler = response -> {
                String body = EntityUtils.toString(response.getEntity());
                if (response.getCode() != 200)
                    AllureManager.saveLog("Non-200 response for: %s".formatted(url), body);

                return body;
            };

            return client.execute(request, handler);
        } catch (Exception e) {
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        }
    }
}

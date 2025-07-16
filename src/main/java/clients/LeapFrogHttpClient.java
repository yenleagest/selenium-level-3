package clients;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * @return a thread-safe list of HTML page responses (as Strings), ordered by submission index
     */
    public static List<String> getAll(int total) {
        ExecutorService executor = newFixedThreadPool(total);
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        submitTasks(executor, results, total);
        shutdownExecutor(executor);

        return results;
    }

    private static void submitTasks(ExecutorService executor, List<String> results, int total) {
        for (int i = 1; i <= total; i++) {
            final int pageNum = i;
            executor.submit(() -> {
                String url = LEAP_FROG_PRODUCT_PAGE.formatted(pageNum);
                try {
                    String result = get(url);
                    results.add(result);
                } catch (Exception e) {
                    log.error("Failed to fetch page {}: {}", pageNum, e.getMessage());
                    results.add("");
                }
            });
        }
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

            HttpClientResponseHandler<String> handler = response ->
                    EntityUtils.toString(response.getEntity());

            return client.execute(request, handler);

        } catch (Exception e) {
            throw new RuntimeException("GET request failed: " + e.getMessage(), e);
        }
    }
}

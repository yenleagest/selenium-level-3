package common;

public class Constants {
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("maxRetry", "3")), 3);
    public static final String RETRY_STRATEGY = System.getProperty("retryStrategy", "post-suite");
    public static final String RESOURCE_DATA_PATH = "src/test/resources/data/";
}

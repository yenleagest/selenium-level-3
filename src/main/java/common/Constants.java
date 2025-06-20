package common;

public class Constants {
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("maxRetry", "3")), 3);
    public static final String RETRY_STRATEGY = System.getProperty("retryStrategy", "post-suite");
    public static final String RESOURCE_TEST_DATA_PATH = getTestDataPath();

    private static String getTestDataPath() {
        if (System.getProperty("selenide.baseUrl").contains("agoda")) {
            return "src/test/resources/testdata/agoda/data.yml";
        } else {
            return "src/test/resources/testdata/vj/data.yml";
        }
    }
}

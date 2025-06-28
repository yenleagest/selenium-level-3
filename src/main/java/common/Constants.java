package common;

public class Constants {
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("maxRetry", "3")), 3);
    public static final String RETRY_STRATEGY = System.getProperty("retryStrategy", "post-suite");
    public static final String RESOURCE_TEST_DATA_PATH = getTestDataPath();

    private static String getTestDataPath() {
        String baseUrl = System.getProperty("selenide.baseUrl");
        if (baseUrl.contains("agoda")) {
            return "src/test/resources/testdata/agoda/data.yml";
        } else if (baseUrl.contains("vietjet")) {
            if (baseUrl.endsWith("vi"))
                return "src/test/resources/testdata/vj/vi-data.yml";
            else
                return "src/test/resources/testdata/vj/en-data.yml";
        } else {
            throw new IllegalStateException("No test data found for: " + baseUrl);
        }
    }
}

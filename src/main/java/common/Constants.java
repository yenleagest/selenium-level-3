package common;

import com.codeborne.selenide.Configuration;
import data.enums.vj.VJLocale;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Constants {
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("maxRetry", "3")), 3);
    public static final String RETRY_STRATEGY = System.getProperty("retryStrategy", "post-suite");
    public static final String RESOURCE_TEST_DATA_PATH = getTestDataPath();
    public static final VJLocale VJ_LOCALE = getVJLocale();
    public static DateTimeFormatter EUROPEAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter ENGLISH_YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    public static DateTimeFormatter ENGLISH_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH);
    public static DateTimeFormatter STANDARD_YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM yyyy");
    public static DateTimeFormatter YEAR_MONTH_FORMATTER_WITH_FLASH = DateTimeFormatter.ofPattern("MM/yyyy");


    private static String getTestDataPath() {
        String baseUrl = Configuration.baseUrl;
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

    private static VJLocale getVJLocale() {
        String baseUrl = Configuration.baseUrl;
        if (baseUrl.endsWith("/en")) {
            return VJLocale.EN;
        } else if (baseUrl.endsWith("/vi")) {
            return VJLocale.VI;
        } else {
            throw new IllegalStateException("Unsupported VietJet locale for: " + baseUrl);
        }
    }
}

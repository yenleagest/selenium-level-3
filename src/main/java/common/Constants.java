package common;

import com.codeborne.selenide.Configuration;
import data.enums.Environment;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Constants {
    public static final boolean DATA_DRIVEN_OUTPUT = Boolean.parseBoolean(System.getProperty("dataDrivenOutput", "false"));
    public static final Duration TIMEOUT_SECONDS = Duration.ofSeconds(10);
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("maxRetry", "3")), 3);
    public static final String RETRY_STRATEGY = System.getProperty("retryStrategy", "post-suite");
    public static final String RESOURCE_TEST_DATA_PATH = getTestDataPath();
    public static final String LEAP_FROG_PRODUCT_PAGE = "https://store.leapfrog.com/en-us/apps/c?p=%s&platforms=197&product_list_dir=asc&product_list_order=name";
    public static final String LEAP_FROG_EXCEL_PATH = "src/test/resources/testdata/leapfrog/leapfrog-games.xlsx";
    public static final String SIA_EXCEL_PATH = "src/test/resources/testdata/sia/siassistance-insurances.csv";
    public static final Environment ENVIRONMENT = Environment.getEnvironment(Configuration.baseUrl);
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
            return "";
        }
    }
}

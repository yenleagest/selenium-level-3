package common;

import utils.ConfigParser;

public class Constants {
    public static final int DEFAULT_TIMEOUT = Integer.parseInt(System.getProperty("timeout", "20000"));
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("maxRetry", "3")), 5);
    public static final String ENVIRONMENT = System.getProperty("environment", "agoda");
    public static final String BROWSER = System.getProperty("browser", "chrome");
    public static final String RUN_MODE = ConfigParser.get("runMode");
    public static final String GRID_URL = ConfigParser.get("gridURL");
    public static final String PAGE_LOAD_STRATEGY = ConfigParser.get("pageLoadStrategy");
    public static final String RETRY_STRATEGY = System.getProperty("retryStrategy", "post-suite");
    public static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "true"));
}

package common;

import utils.ConfigParser;

public class Constants {

    public static final String RESOURCE_DATA_PATH = "src/test/resources/data/";
    public static final int SELENIDE_DEFAULT_WAIT_MS = 20000;
    public static final int SELENIDE_DEFAULT_POLLING_MS = 200;
    public static final int MAX_RETRY = Math.min(ConfigParser.getInt("maxRetry"), 3);
}

package drivers;

import com.codeborne.selenide.Configuration;
import data.enums.Environment;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static common.Constants.BROWSER;
import static common.Constants.DEFAULT_TIMEOUT;
import static common.Constants.ENVIRONMENT;
import static common.Constants.GRID_URL;
import static common.Constants.HEADLESS;
import static common.Constants.PAGE_LOAD_STRATEGY;
import static common.Constants.RUN_MODE;


public class DriverUtils {

    // remove pathSegments
    public static synchronized void openURL() {
        Configuration.browser = BROWSER;
        Configuration.headless = HEADLESS;
        Configuration.timeout = DEFAULT_TIMEOUT;
        Configuration.pageLoadStrategy = PAGE_LOAD_STRATEGY;

        if (RUN_MODE.equalsIgnoreCase("grid"))
            Configuration.remote = GRID_URL;

        String baseUrl = Environment.getEnvironment(ENVIRONMENT).getBaseUrl();
        open(baseUrl);

        getWebDriver().manage().window().maximize();
    }

    public static synchronized void quitDriver() {
        if (getWebDriver() != null) {
            getWebDriver().quit();
        }
    }

    public static WebDriver getDriver() {
        return getWebDriver();
    }
}

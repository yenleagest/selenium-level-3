package drivers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideWait;
import com.codeborne.selenide.WebDriverRunner;
import data.enums.Environment;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import utils.ConfigParser;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static common.Constants.SELENIDE_DEFAULT_POLLING_MS;
import static common.Constants.SELENIDE_DEFAULT_WAIT_MS;


public class DriverUtils {

    public static Actions driverAction() {
        return new Actions(getWebDriver());
    }

    public static SelenideWait driverWait() {
        return new SelenideWait(WebDriverRunner.getWebDriver(), SELENIDE_DEFAULT_WAIT_MS, SELENIDE_DEFAULT_POLLING_MS);
    }

    public static void waitForElementToBeDisplayed(SelenideElement element) {
        driverWait().until((d) -> element.isDisplayed());
    }

    public static void waitForPageLoaded() {
        driverWait().until(webDriver ->
                (Boolean.FALSE.equals(executeJavaScript("return window.jQuery && jQuery.active != 0"))) &&
                        Boolean.TRUE.equals(executeJavaScript("return document.readyState == 'complete'")));

    }

    public static synchronized void openURL(String... pathSegments) {
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.headless = ConfigParser.getBoolean("headless");
        Configuration.timeout = ConfigParser.getInt("timeout");
        Configuration.pageLoadStrategy = ConfigParser.get("pageLoadStrategy");

        String runMode = ConfigParser.get("runMode");
        if (runMode.equalsIgnoreCase("grid"))
            Configuration.remote = ConfigParser.get("gridURL");

        String baseUrl = Environment.getEnvironment(ConfigParser.get("environment")).getBaseUrl();
        String path = String.join("/", pathSegments);

        open(baseUrl.concat(path));

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

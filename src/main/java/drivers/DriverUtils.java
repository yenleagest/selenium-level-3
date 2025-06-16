package drivers;

import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


public class DriverUtils {

    // remove pathSegments
    public static synchronized void openURL() {
        open("/");
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

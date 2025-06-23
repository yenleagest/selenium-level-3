package drivers;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


public class DriverUtils {

    public static synchronized void openURL() {
        open("/");
    }

    public static synchronized void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

    public static WebDriver getDriver() {
        return getWebDriver();
    }

    public static void switchToLatestTab() {
        switchTo().window(getDriver().getWindowHandles().size() - 1);
    }

    public static void switchToIframe(SelenideElement element) {
        switchTo().frame(element);
    }

    public static void switchToDefaultContent() {
        switchTo().defaultContent();
    }

    public static void closeCurrentTab() {
        getDriver().close();
        switchToLatestTab();
    }
}

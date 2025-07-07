package utils;

import drivers.DriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ShadowDomUtils {

    private final WebDriver driver = DriverUtils.getDriver();;
    private SearchContext current;

    private ShadowDomUtils() {
        this.current = driver;
    }

    public static ShadowDomUtils start() {
        return new ShadowDomUtils();
    }

    public ShadowDomUtils through(String locator) {
        WebElement element = current.findElement(By.cssSelector(locator));
        return through(element);
    }

    public ShadowDomUtils through(WebElement element) {
        attachShadowRoot(element);
        return this;
    }


    public WebElement get(String locator) {
        return current.findElement(By.cssSelector(locator));
    }

    public List<WebElement> getAll(String locator) {
        return current.findElements(By.cssSelector(locator));
    }

    private void attachShadowRoot(WebElement element) {
        Object shadowRoot = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot", element);

        if (shadowRoot == null) {
            throw new NoSuchElementException("No shadow root found at: " + element);
        }

        current = (SearchContext) shadowRoot;
    }
}

package elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Element {

    private final SelenideElement element;

    public Element(String parentSelector, String... childSelector) {
        if (childSelector.length == 0) {
            this.element = $(parentSelector);
        } else {
            this.element = $(parentSelector).$(childSelector[0]);
        }
    }

    public Element(By locator, By... childLocator) {
        if (childLocator.length == 0) {
            this.element = $(locator);
        } else {
            this.element = $(locator).$(childLocator[0]);
        }
    }

    public static ElementsCollection getCollection(By locator) {
        return $$(locator);
    }

    public void click() {
        element.shouldBe(clickable).click();
    }

    public void setValue(String value) {
        element.shouldBe(visible).clear();
        element.setValue(value);
    }

    public void sendKey(Keys key) {
        element.shouldBe(visible).sendKeys(key);
    }

    public SelenideElement getRaw() {
        return element;
    }
}

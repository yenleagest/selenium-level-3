package pages.sia;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import reports.AllureManager;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class PersonalInfoPage extends HomePage {

    private final By totalText = By.cssSelector(".plan-info ~ .text-green > span");

    @Step("Get total amount")
    public String getTotal() {
        String total = $(totalText).getText().trim();
        AllureManager.saveLog("Total amount", total);
        return total;
    }
}

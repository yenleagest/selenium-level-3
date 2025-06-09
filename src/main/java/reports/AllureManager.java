package reports;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.openqa.selenium.OutputType;

import java.util.Base64;


public class AllureManager {

    public static byte[] takeScreenshot() {
        String screenshotAsBase64 = Selenide.screenshot(OutputType.BASE64);
        return Base64.getDecoder().decode(screenshotAsBase64);
    }

    public static void setupAllureReporting() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }
}

package reports;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;

import java.util.Base64;

@Slf4j
public class AllureManager {

    public static byte[] takeScreenshot() {
        String screenshotAsBase64 = Selenide.screenshot(OutputType.BASE64);
        return Base64.getDecoder().decode(screenshotAsBase64);
    }

    public static void setupAllureReporting() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    public static void saveLog(String name, String message) {
        log.info(message);
        Allure.addAttachment(name, "text/plain", message);
    }
}

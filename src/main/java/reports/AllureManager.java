package reports;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;

import java.io.FileInputStream;
import java.util.Base64;

import static common.Constants.SIA_EXCEL_PATH;

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
        log.info("{}: {}", name, message);
        Allure.addAttachment(name, "text/plain", message);
    }

    public static void attachSIADataFile() {
        try (FileInputStream fis = new FileInputStream(SIA_EXCEL_PATH)) {
            Allure.addAttachment("siassistance-insurances.csv", "text/csv", fis, ".csv");
        } catch (Exception e) {
            log.error("Failed to attach CSV file: {}", e.getMessage());
        }
    }
}

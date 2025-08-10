package pages.sia;

import data.enums.sia.AdOns;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class AdOnsPage extends HomePage {

    private final By submitButton = By.className("btn-continue");
    private final By skipButton = By.className("btn-skip");
    private final String addOnCheckboxByDataPlan = "[name='adons[]'][data-plan='%s']~span";

    public void selectAdOns(AdOns adOns) {
        if (adOns != AdOns.NONE)
            toggleAddOn(adOns);
        goToPersonalInformationPage(adOns);
    }

    @Step("Toggle ad-ons {adOns}")
    private void toggleAddOn(AdOns adOns) {
        $(addOnCheckboxByDataPlan.formatted(adOns.getDataPlan())).click();
    }

    @Step("Go to Personal Information page")
    private void goToPersonalInformationPage(AdOns adOns) {
        if (adOns == AdOns.NONE)
            $(skipButton).click();
        else
            $(submitButton).click();
    }
}

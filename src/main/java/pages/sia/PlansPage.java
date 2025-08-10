package pages.sia;

import data.enums.sia.Plans;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class PlansPage extends HomePage {

    private final String planButtonByDataPlan = ".btn-green[data-plan='%s']";

    @Step("Pick {plan} plan")
    public void pickAPlan(Plans plan) {
        $(planButtonByDataPlan.formatted(plan.getDataPlan())).click();
    }
}

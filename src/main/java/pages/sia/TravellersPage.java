package pages.sia;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import utils.TravellerUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class TravellersPage extends HomePage {

    private final By addTraveller = By.className("add-traveller");
    private final By continueButton = By.cssSelector(".btn-md.btn-green");

    private final String travellerNameByIndex = "//span[@class='tcount'][text()=%d]/following::input[@placeholder='Name']";
    private final String travellerDOBByIndex = "//span[@class='tcount'][text()=%d]/following::input[@placeholder='Date of Birth']";

    @Step("Submit {travellers} travellers with ages: {ages}")
    public void submitTravellers(int travellers, int[] ages) {
        addTravellers(travellers, ages);
        goToPlansPage();
    }

    private void addTravellers(int travellers, int[] ages) {
        for (int i = 1; i <= travellers; i++) {
            if (i != 1)
                $(addTraveller).click();

            setTravellerName(i, TravellerUtils.getTravellerName());
            setTravellerDOB(i, TravellerUtils.getTravellerDOB(ages[i-1]));
        }
    }

    @Step("Set traveller {index} name: {name}")
    private void setTravellerName(int index, String name) {
        $(By.xpath(travellerNameByIndex.formatted(index))).setValue(name);
    }

    @Step("Set traveller {index} date of birth: {dob}")
    private void setTravellerDOB(int index, LocalDate dob) {
        $(By.xpath(travellerDOBByIndex.formatted(index))).setValue(dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).pressEnter();
    }

    @Step("Go to Plans page")
    public void goToPlansPage() {
        $(continueButton).click();
    }
}

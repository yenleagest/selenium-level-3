package testcases.sia;

import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.sia.AdOnsPage;
import pages.sia.HomePage;
import pages.sia.PersonalInfoPage;
import pages.sia.PlansPage;
import pages.sia.TravellersPage;
import reports.AllureManager;
import testcases.TestBase;
import testdata.SIATestData;
import utils.ExcelUtils;

import java.time.LocalDate;

public class TraverInsuranceTest extends TestBase {

    HomePage homePage;
    TravellersPage travellersPage;
    PlansPage plansPage;
    AdOnsPage adOnsPage;
    PersonalInfoPage personalInformationPage;
    LocalDate departureDate;
    LocalDate returnDate;
    String actualPrice;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        DriverUtils.openURL();
        homePage = new HomePage();
        travellersPage = new TravellersPage();
        plansPage = new PlansPage();
        adOnsPage = new AdOnsPage();
        personalInformationPage = new PersonalInfoPage();
        departureDate = LocalDate.now().plusDays(1); // set departure date to tomorrow
    }

    @Test(dataProvider = "siaTestData", groups = {"smoke", "regression"}, description = "User is able to get travel insurance with valid data")
    public void travelInsurance(SIATestData data) {
        SoftAssert softAssert = new SoftAssert();

        returnDate = departureDate.plusDays(data.getTripLong());
        homePage.submitQuote(data.getDepartureLocation(), data.getDestinationLocation(), departureDate, returnDate);

        travellersPage.submitTravellers(data.getTravellers(), data.getAges());

        plansPage.pickAPlan(data.getPlan());

        adOnsPage.selectAdOns(data.getAddOns());

        actualPrice = personalInformationPage.getTotal();
        softAssert.assertEquals(actualPrice, data.getPrice());

        ExcelUtils.recordSIAOutcome(actualPrice, data.getRowNum());

        softAssert.assertAll();
    }

    // attaches the data driven file once after all tests.
    // enabled via IAnnotationTransformer based on -DdataDrivenOutput
    // allure dropped After/BeforeSuite logging since v2.20.0
    @Test(priority = Integer.MAX_VALUE, groups = {"smoke", "regression"}, description = "siassistance-insurances.csv")
    public void attachFinalExcelReport() {
        AllureManager.attachSIADataFile();
    }
}

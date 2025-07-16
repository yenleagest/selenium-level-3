package testcases.leapfrog;

import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.leapfrog.HomePage;
import testcases.TestBase;

public class GameInfoTest extends TestBase {

    HomePage homePage;
    SoftAssert softAssert;
    int numberOfPages;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        DriverUtils.openURL();
        homePage = new HomePage();
        softAssert = new SoftAssert();
    }

    @Test(groups = {"smoke", "regression"}, description = "Game info on UI should match with the provided Excel file")
    public void gameInfo() {

        numberOfPages = homePage.getNumberOfPages();
        softAssert.assertTrue(homePage.doesGameInfoMatch(numberOfPages));

        softAssert.assertAll();
    }
}

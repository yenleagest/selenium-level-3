package testcases.agoda;

import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import testcases.TestBase;

public class TC01 extends TestBase {

    SoftAssert softAssert;

    @BeforeMethod
    public void setUp() {
        softAssert = new SoftAssert();
        DriverUtils.openURL();
    }

    @Test(description = "Failed test")
    public void failedTest() {
        softAssert.assertTrue(false);
        softAssert.assertAll();
    }

    @Test(description = "Passed test")
    public void passedTest() {
        softAssert.assertTrue(true);
        softAssert.assertAll();
    }
}

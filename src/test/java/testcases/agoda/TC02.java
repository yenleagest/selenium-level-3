package testcases.agoda;

import drivers.DriverUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import testcases.TestBase;

public class TC02 extends TestBase {

    SoftAssert softAssert;

    @BeforeMethod
    public void setUp() {
        softAssert = new SoftAssert();
        DriverUtils.openURL();
    }

    @Test(description = "Other failed test")
    public void otherFailedTest() {
        softAssert.assertTrue(false);
        softAssert.assertAll();
    }
}

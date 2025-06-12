package testcases.agoda;

import drivers.DriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import testcases.TestBase;

@Slf4j
public class TC02 extends TestBase {

    SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();
        softAssert = new SoftAssert();
    }

    @Test(groups = {"smoke", "regression"}, description = "I wont fail")
    public void otherPassTest() {
        log.info("Executing other pass test...");
        softAssert.assertTrue(true);
        softAssert.assertAll();
    }
}

package testcases.agoda;

import drivers.DriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import testcases.TestBase;

import java.util.Random;

@Slf4j
public class TC01 extends TestBase {

    SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverUtils.openURL();
        softAssert = new SoftAssert();
    }

    @Test(groups = {"regression"}, description = "I will pass")
    public void passTest() {
        log.info("Executing pass test...");
        softAssert.assertTrue(true);
        softAssert.assertAll();
    }

    @Test(groups = {"smoke", "regression"}, description = "I may pass, I may not")
    public void randomTest() {
        log.info("Executing random result test...");
        Random rand = new Random();
        softAssert.assertTrue(rand.nextBoolean());
        softAssert.assertAll();
    }
}

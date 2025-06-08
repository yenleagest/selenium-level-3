package testcases.agoda;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import testcases.TestBase;

public class TC02 extends TestBase {

    Assertion assertion;

    @BeforeMethod
    public void setUp() {
        assertion = new Assertion();
        openURL();
    }

    @Test(description = "Other failed test")
    public void otherFailedTest() {
        assertion.assertTrue(false);
    }
}

package testcases.agoda;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import testcases.TestBase;

public class TC01 extends TestBase {

    Assertion assertion;

    @BeforeMethod
    public void setUp() {
        assertion = new Assertion();
        openURL();
    }

    @Test(description = "Failed test")
    public void failedTest() {
        assertion.assertTrue(false);
    }

    @Test(description = "Passed test")
    public void passedTest() {
        assertion.assertTrue(true);
    }
}

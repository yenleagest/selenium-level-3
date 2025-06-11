package testcases;

import drivers.DriverUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;


public class TestBase {

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        DriverUtils.quitDriver();
    }
}

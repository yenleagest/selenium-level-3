package testcases;

import drivers.DriverUtils;
import org.testng.annotations.AfterMethod;


public class TestBase {

    @AfterMethod
    public void afterMethod() {
        DriverUtils.quitDriver();
    }
}

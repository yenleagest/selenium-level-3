package testcases;

import drivers.DriverUtils;
import org.testng.annotations.AfterMethod;


public class TestBase extends DriverUtils {

    @AfterMethod
    public void afterMethod() {
        quitDriver();
    }
}

package testcases;

import drivers.DriverUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import utils.YmlParser;

import java.lang.reflect.Method;


public class TestBase {

    @DataProvider(name = "dataByMethod")
    public Object[][] getAgodaTestData(Method method) {
        try {
            return YmlParser.getTestDataByMethod(method.getName());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load test data for method: " + method.getName(), e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverUtils.quitDriver();
    }
}

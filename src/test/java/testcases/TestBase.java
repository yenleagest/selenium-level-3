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
            Object[][] data = YmlParser.getAgodaTestData(method.getName());
            if (data.length == 0) {
                throw new IllegalStateException("No test data found for method: " + method.getName());
            }
            return data;
        } catch (Exception e) {
            System.err.println("Exception loading data for " + method.getName() + ": " + e.getMessage());
            throw new IllegalStateException("Failed to load test data for method: " + method.getName(), e);
        }
    }


    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverUtils.quitDriver();
    }
}

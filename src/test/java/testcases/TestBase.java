package testcases;

import data.enums.books.LocatorStrategy;
import drivers.DriverUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import testdata.AgodaTestData;
import testdata.VJTestData;
import utils.YmlParser;

import java.lang.reflect.Method;


public class TestBase {

    @DataProvider(name = "locatorStrategy")
    public Object[][] getLocatorStrategy() {
        return new Object[][] {
            { LocatorStrategy.SELENIDE },
            { LocatorStrategy.SELENIUM }
        };
    }

    @DataProvider(name = "dataByMethod")
    public Object[][] getAgodaTestData(Method method) {
        try {
            return YmlParser.getTestDataByMethod(method.getName(), AgodaTestData.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load test data for method: " + method.getName(), e);
        }
    }

    @DataProvider(name = "dataByMethodLocale")
    public Object[][] getVJTestData(Method method) {
        try {
            return YmlParser.getTestDataByMethod(method.getName(), VJTestData.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load test data for method: " + method.getName(), e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverUtils.quitDriver();
    }
}

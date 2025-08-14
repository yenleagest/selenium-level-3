package testcases;

import data.enums.books.LocatorStrategy;
import drivers.DriverUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import testdata.AgodaTestData;
import testdata.SIATestData;
import testdata.VJTestData;
import utils.ExcelUtils;
import utils.YmlParser;

import java.lang.reflect.Method;
import java.util.List;

public class TestBase {

    @DataProvider(name = "locatorStrategy", parallel = true)
    public Object[][] getLocatorStrategy() {
        return new Object[][]{
                {LocatorStrategy.SELENIDE},
                {LocatorStrategy.SELENIUM}
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

    @DataProvider(name = "siaTestData", parallel = true)
    public Object[][] provideSIATestData() {
        List<SIATestData> dataList = ExcelUtils.loadSIATestData();
        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverUtils.quitDriver();
    }
}

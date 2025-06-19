package testcases;

import drivers.DriverUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static utils.YmlParser.getTestData;


public class TestBase {

    @DataProvider(name = "dataByMethod")
    public Object[][] getTestDataFromYaml(Method method) {
        try {
            String testCaseName = method.getName();
            List<Map<String, String>> dataList = getTestData(testCaseName);
            Object[][] data = new Object[dataList.size()][1];
            for (int i = 0; i < dataList.size(); i++) {
                data[i][0] = dataList.get(i);
            }
            return data;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load test data for method: " + method.getName(), e);
        }
    }


    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverUtils.quitDriver();
    }
}

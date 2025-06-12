package listeners;

import io.qameta.allure.testng.AllureTestNg;
import lombok.extern.slf4j.Slf4j;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.Constants.MAX_RETRY;
import static common.Constants.RETRY_STRATEGY;

@Slf4j
public class SuiteListener implements ISuiteListener {

    private int retryCount = 0;

    @Override
    public void onFinish(ISuite suite) {

        if (!RETRY_STRATEGY.equalsIgnoreCase("post-suite")) {
            return;
        }

        boolean isSuiteSuccess = suite.getResults().values().stream()
                .allMatch(result -> result.getTestContext().getFailedTests().getAllResults().isEmpty());

        if (isSuiteSuccess) {
            return;
        }

        retryCount++;

        if (retryCount > MAX_RETRY) {
            return;
        }

        // get suite result and context
        ISuiteResult result = suite.getResults().values().iterator().next();
        ITestContext context = result.getTestContext();

        // create a new retry suite with the same name for consistent reporting
        XmlSuite retrySuite = new XmlSuite();
        retrySuite.setName(suite.getName());

        XmlTest xmlTest = new XmlTest(retrySuite);
        xmlTest.setName(suite.getName());

        List<XmlClass> retryClasses = new ArrayList<>();

        // group failed methods by their test class
        Map<String, List<String>> methodsByClass = new HashMap<>();

        context.getFailedTests().getAllResults().forEach(failedResult -> {
            String className = failedResult.getTestClass().getName();
            String methodName = failedResult.getMethod().getMethodName();
            methodsByClass.computeIfAbsent(className, k -> new ArrayList<>()).add(methodName);
        });

        // create XmlClass objects with included methods for each failed class
        for (Map.Entry<String, List<String>> entry : methodsByClass.entrySet()) {
            XmlClass xmlClass = new XmlClass(entry.getKey());
            List<XmlInclude> includedMethods = entry.getValue().stream()
                    .map(XmlInclude::new)
                    .toList();
            xmlClass.setIncludedMethods(includedMethods);
            retryClasses.add(xmlClass);
        }

        xmlTest.setXmlClasses(retryClasses);

        TestNG testng = new TestNG();
        testng.setXmlSuites(Collections.singletonList(retrySuite));

        // add listener to retry multiple times
        testng.addListener(this);

        // add these listeners to attach failure screenshots (if applicable)
        testng.addListener(new AllureTestNg());
        testng.addListener(new TestListener());

        log.info("Running failed tests / {} / Attempt [{}/{}]", suite.getName(), retryCount, MAX_RETRY);

        testng.run();
    }
}
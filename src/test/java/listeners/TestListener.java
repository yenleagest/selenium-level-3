package listeners;

import drivers.DriverUtils;
import io.qameta.allure.Allure;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import reports.AllureManager;
import retriers.RetryAnalyzer;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

import static common.Constants.DATA_DRIVEN_OUTPUT;


public class TestListener implements ITestListener, IAnnotationTransformer {

    @Override
    public void onStart(ITestContext context) {
        AllureManager.setupAllureReporting();
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Allure.addAttachment("screenshot", "image/png", new ByteArrayInputStream(AllureManager.takeScreenshot()), "png");
        Allure.addAttachment("Page HTML", "text/html", Objects.requireNonNull(DriverUtils.getDriver().getPageSource()), ".html");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
        if ("attachFinalExcelReport".equals(testMethod.getName()) && !DATA_DRIVEN_OUTPUT)
            // using annotation.setEnabled() will result an unknown test in the report
            annotation.setGroups(new String[]{"disabled"});
    }
}
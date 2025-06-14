package listeners;

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
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
package retriers;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static common.Constants.MAX_RETRY;
import static common.Constants.RETRY_STRATEGY;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        return RETRY_STRATEGY.equalsIgnoreCase("immediate") && retryCount++ < MAX_RETRY;
    }
}

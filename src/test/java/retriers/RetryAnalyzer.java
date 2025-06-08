package retriers;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.ConfigParser;

import static common.Constants.MAX_RETRY;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        return ConfigParser.get("retryStrategy").equalsIgnoreCase("immediate") && retryCount++ < MAX_RETRY;
    }
}
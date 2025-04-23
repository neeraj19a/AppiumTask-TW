package com.qa.runners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int MAX_RETRY_COUNT = 2; // Maximum number of retry attempts
    private int retryCount = 1;
    
    // Track current retry status for each test thread
    private static ThreadLocal<Boolean> isLastRetryAttempt = new ThreadLocal<>();
    
    static {
        isLastRetryAttempt.set(false);
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            System.out.println("Retrying Now--> " + retryCount);
            retryCount++;
            isLastRetryAttempt.set(retryCount >= MAX_RETRY_COUNT);
            return true; // Retry the test
        }
        
        // Mark that we're on the last retry
        isLastRetryAttempt.set(true);
        return false; // Do not retry the test
    }
    
    /**
     * Check if the current test is on its last retry attempt
     * @return true if the test has exhausted all retries
     */
    public static boolean isLastRetry() {
        Boolean value = isLastRetryAttempt.get();
        return value != null && value;
    }
}
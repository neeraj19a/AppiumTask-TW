package com.qa.stepdef;

import com.qa.utils.AppManager;
import com.qa.utils.DriverManager;
import com.qa.utils.ServerManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import com.qa.runners.RetryAnalyzer;

public class Hooks {
    // Track currently running test status
    private static ThreadLocal<Boolean> lastTestOfRetry = new ThreadLocal<>();

    @Before
    public void initialize() {
        // The initialization is handled in RunnerBase
        lastTestOfRetry.set(false);
        
        // Start app main activity before each test run
        AppManager.startAppMainActivity();
    }

    @After
    public void quit(Scenario scenario) throws Exception {
        try {
            // Make sure to close the app after the test
            AppManager.ensureAppClosed();
            
            if (scenario.isFailed()) {
                // Take screenshot on failure
                try {
                    byte[] screenshot = new DriverManager().getDriver().getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", scenario.getName());
                } catch (Exception e) {
                    System.out.println("Failed to take screenshot: " + e.getMessage());
                }
                
                // Get the retry status - if we're on the last retry or test passed, mark it
                if (RetryAnalyzer.isLastRetry()) {
                    lastTestOfRetry.set(true);
                    System.out.println("No more retries left - not reinitializing driver");
                } else {
                    // Only restart the driver if we have more retries to go
                    System.out.println("Reinitializing driver for next retry attempt");
                    restartDriver();
                }
            }
        } catch (Exception e) {
            System.out.println("Error in @After hook: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void restartDriver() throws Exception {
        // Restart driver to ensure clean state for next test
        DriverManager driverManager = new DriverManager();
        if (driverManager.getDriver() != null) {
            driverManager.getDriver().quit();
            driverManager.setDriver(null);
        }
        ServerManager serverManager = new ServerManager();
        if (serverManager.getServer() != null) {
            serverManager.getServer().stop();
        }
        new ServerManager().startServer();
        new DriverManager().initializeDriver();
    }
}

package com.qa.runners;

import com.qa.utils.AppManager;
import com.qa.utils.DriverManager;
import com.qa.utils.GlobalParams;
import com.qa.utils.ServerManager;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.*;

public class RunnerBase {
    private static final ThreadLocal<TestNGCucumberRunner> testNGCucumberRunner = new ThreadLocal<>();

    public static TestNGCucumberRunner getRunner() {
        return testNGCucumberRunner.get();
    }

    private static void setRunner(TestNGCucumberRunner testNGCucumberRunner1) {
        testNGCucumberRunner.set(testNGCucumberRunner1);
    }

    @Parameters({"platformName", "udid", "deviceName", "systemPort"})
    @BeforeClass(alwaysRun = true)
    public void setUpClass(String platformName, String udid, String deviceName,
                           @Optional("10000") String systemPort) throws Exception {

        ThreadContext.put("ROUTINGKEY", platformName + "_" + deviceName);

        GlobalParams params = new GlobalParams();
        params.setPlatformName(platformName);
        params.setUDID(udid);
        params.setDeviceName(deviceName);

        if ("Android".equalsIgnoreCase(platformName)) {
            params.setSystemPort(systemPort);
        }

        new ServerManager().startServer();
        new DriverManager().initializeDriver();
        
        // Navigate to app launch screen at beginning of test class
        AppManager.startAppMainActivity();

        setRunner(new TestNGCucumberRunner(this.getClass()));
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void scenario(PickleWrapper pickle, FeatureWrapper cucumberFeature) throws Throwable {
        getRunner().runScenario(pickle.getPickle());
    }

    @DataProvider
    public Object[][] scenarios() {
        return getRunner().provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        try {
            // Make sure the app is properly closed
            AppManager.ensureAppClosed();
            
            DriverManager driverManager = new DriverManager();
            if (driverManager.getDriver() != null) {
                // Quit driver
                driverManager.getDriver().quit();
                driverManager.setDriver(null);
            }
            
            ServerManager serverManager = new ServerManager();
            if (serverManager.getServer() != null) {
                serverManager.getServer().stop();
            }
            
            if (testNGCucumberRunner != null) {
                getRunner().finish();
            }
        } catch (Exception e) {
            System.out.println("Error in tearDownClass: " + e.getMessage());
        }
    }
}

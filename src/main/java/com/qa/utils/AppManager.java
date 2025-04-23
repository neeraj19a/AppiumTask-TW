package com.qa.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import com.google.common.collect.ImmutableMap;

/**
 * Utility class to manage app-specific operations like starting, clearing, and closing the app
 */
public class AppManager {
    
    // Trust Wallet app package
    public static final String APP_PACKAGE = new PropertyManager().getProps().getProperty("androidAppPackage");

    /**
     * Ensures a completely fresh state of the app by:
     * 1. Checking if it's running and terminating it
     * 2. Clearing all app data to remove any stored state
     * 3. Launching it fresh
     */
    public static void startAppMainActivity() {
        try {
            AppiumDriver driver = new DriverManager().getDriver();
            if (driver != null) {
                System.out.println("Ensuring a completely fresh app state...");
                
                // Cast to AndroidDriver to use the mobile extension methods
                if (driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) driver;
                    
                    // Check if the app is already running
                    Object appState = androidDriver.executeScript("mobile: queryAppState", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    System.out.println("Current app state: " + appState);
                    
                    // First terminate the app if it's running
                    System.out.println("Terminating app if it's running...");
                    androidDriver.executeScript("mobile: terminateApp", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    
                    // Give it a moment to fully terminate
                    Thread.sleep(500);
                    
                    // Clear app data - this is crucial to truly reset the app state
                    System.out.println("Clearing all app data to reset state...");
                    androidDriver.executeScript("mobile: clearApp", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    
                    System.out.println("App data cleared successfully");
                    
                    // Then launch the app fresh
                    System.out.println("Launching app with fresh state...");
                    androidDriver.executeScript("mobile: activateApp", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    
                    System.out.println("App launched successfully with completely fresh state");
                    
                    // Verify it's running
                    appState = androidDriver.executeScript("mobile: queryAppState", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    System.out.println("App state after launch: " + appState);
                } else {
                    System.out.println("Driver is not AndroidDriver, can't ensure fresh app state");
                    // For iOS or other platforms, we could implement alternative methods here
                }
                
                // Wait for the app to fully load
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("Error ensuring fresh app state: " + e.getMessage());
        }
    }

    /**
     * Ensures the app is completely closed.
     * This helps in clean state management between test runs.
     */
    public static void ensureAppClosed() {
        try {
            AppiumDriver driver = new DriverManager().getDriver();
            if (driver != null) {
                System.out.println("Ensuring app is closed...");
                
                // Cast to AndroidDriver to use the mobile extension methods
                if (driver instanceof AndroidDriver) {
                    AndroidDriver androidDriver = (AndroidDriver) driver;
                    
                    // Check current app state
                    Object appState = androidDriver.executeScript("mobile: queryAppState", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    System.out.println("Current app state before closing: " + appState);
                    
                    // Terminate the app
                    androidDriver.executeScript("mobile: terminateApp", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    
                    // Verify it's terminated
                    appState = androidDriver.executeScript("mobile: queryAppState", 
                        ImmutableMap.of("appId", APP_PACKAGE));
                    System.out.println("App state after closing: " + appState);
                } else {
                    System.out.println("Driver is not AndroidDriver, can't ensure app is closed");
                }
            }
        } catch (Exception e) {
            System.out.println("Error ensuring app is closed: " + e.getMessage());
        }
    }
} 
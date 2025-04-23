package com.qa.utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

public class ServerManager {
    private static final ThreadLocal<AppiumDriverLocalService> server = new ThreadLocal<>();
    // Cache the detected paths so we don't need to find them every time
    private static String cachedNodePath;
    private static String cachedAppiumJsPath;
    private static boolean isFirstRun = true;

    TestUtils utils = new TestUtils();
    PropertyManager propertyManager = new PropertyManager();
    Properties props;

    public AppiumDriverLocalService getServer() {
        return server.get();
    }

    public void startServer() throws IOException {
        props = propertyManager.getProps();
        utils.log().info("starting appium server");
        
        // Execute ADB uninstall commands before initializing the driver
        executeAdbCommand("adb uninstall io.appium.uiautomator2.server");
        executeAdbCommand("adb uninstall io.appium.uiautomator2.server.test");
        
        // Ensure no Appium processes are running
        try {
            Process process;
            while (true) {
                process = Runtime.getRuntime().exec("pgrep -f \"appium\"");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if (reader.readLine() == null) {
                    break; // No Appium processes running, exit loop
                }
                process = Runtime.getRuntime().exec("pkill -f \"appium\"");
                process.waitFor(); // Wait for process to complete
                Thread.sleep(1000); // Sleep for 1 second
            }
            process.destroy(); // Clean up the process
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Handle exceptions
        }
        utils.log().info("All Appium sessions have been terminated.");
        
        AppiumDriverLocalService server;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            server = getWindowsAppiumService();
        } else {
            server = getMacOrLinuxAppiumService();
        }
        
        server.start();
        if (server == null || !server.isRunning()) {
            utils.log().fatal("Appium server not started. ABORT!!!");
            throw new AppiumServerHasNotBeenStartedLocallyException("Appium server not started. ABORT!!!");
        }
        server.clearOutPutStreams();
        ServerManager.server.set(server);
        utils.log().info("Appium server started");

        // First run complete, next runs will use cached values
        isFirstRun = false;
    }

    private void executeAdbCommand(String command) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        // Read the output from the command
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println("Command " + command + "Output--> " + s);
        }
    }

    public AppiumDriverLocalService getWindowsAppiumService() {
        GlobalParams params = new GlobalParams();
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withLogFile(new File(params.getPlatformName() + "_"
                        + params.getDeviceName() + File.separator + "Server.log"));

        HashMap<String, String> environment = new HashMap<>();
        
        String androidHome = props.getProperty("android.home");
        if (androidHome != null) {
            environment.put("ANDROID_HOME", androidHome);
        } else {
            environment.put("ANDROID_HOME", System.getenv("ANDROID_HOME"));
        }
        
        environment.put("JAVA_HOME", System.getenv("JAVA_HOME"));
        builder.withEnvironment(environment);

        return AppiumDriverLocalService.buildService(builder);
    }

    public AppiumDriverLocalService getMacOrLinuxAppiumService() {
        GlobalParams params = new GlobalParams();
        HashMap<String, String> environment = new HashMap<>();
        
        // Add common environment variables
        String androidHome = props.getProperty("android.home");
        if (androidHome != null) {
            environment.put("ANDROID_HOME", androidHome);
        } else {
            environment.put("ANDROID_HOME", System.getenv("ANDROID_HOME"));
        }
        
        environment.put("JAVA_HOME", System.getenv("JAVA_HOME"));
        
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withEnvironment(environment)
                .withLogFile(new File(params.getPlatformName() + "_"
                        + params.getDeviceName() + File.separator + "Server.log"));
        
        // Get node executable path with caching
        String nodePath = getNodePath();
        if (nodePath != null) {
            builder = builder.usingDriverExecutable(new File(nodePath));
        }
        
        // Get Appium JS path with caching
        String appiumJsPath = getAppiumJsPath();
        if (appiumJsPath != null) {
            builder = builder.withAppiumJS(new File(appiumJsPath));
        }
        
        try {
            return AppiumDriverLocalService.buildService(builder);
        } catch (Exception e) {
            utils.log().error("Failed to build custom Appium service: " + e.getMessage());
            utils.log().info("Falling back to default Appium service");
            return AppiumDriverLocalService.buildDefaultService();
        }
    }
    
    private String getNodePath() {
        // Use cached value if not first run
        if (!isFirstRun && cachedNodePath != null) {
            utils.log().info("Using cached node path: " + cachedNodePath);
            return cachedNodePath;
        }
        
        // Try to get from config first
        String nodePath = props.getProperty("appium.node.path");
        if (nodePath != null && !nodePath.isEmpty()) {
            File nodeFile = new File(nodePath);
            if (nodeFile.exists()) {
                utils.log().info("Using node executable from config: " + nodePath);
                cachedNodePath = nodePath;
                return nodePath;
            } else {
                utils.log().info("Node path in config.properties doesn't exist.");
            }
        }
        
        // If not found in config, detect automatically
        nodePath = findNodePath();
        if (nodePath != null) {
            cachedNodePath = nodePath;
        }
        return nodePath;
    }
    
    private String getAppiumJsPath() {
        // Use cached value if not first run
        if (!isFirstRun && cachedAppiumJsPath != null) {
            utils.log().info("Using cached Appium JS path: " + cachedAppiumJsPath);
            return cachedAppiumJsPath;
        }
        
        // Try to get from config first
        String appiumJsPath = props.getProperty("appium.js.path");
        if (appiumJsPath != null && !appiumJsPath.isEmpty()) {
            File appiumJsFile = new File(appiumJsPath);
            if (appiumJsFile.exists()) {
                utils.log().info("Using Appium JS from config: " + appiumJsPath);
                cachedAppiumJsPath = appiumJsPath;
                return appiumJsPath;
            } else {
                utils.log().info("Appium JS path in config.properties doesn't exist.");
            }
        }
        
        // If not found in config, detect automatically
        appiumJsPath = findAppiumJsPath();
        if (appiumJsPath != null) {
            cachedAppiumJsPath = appiumJsPath;
        }
        return appiumJsPath;
    }
    
    private String findNodePath() {
        if (isFirstRun) {
            utils.log().info("Attempting to find node path automatically");
        }
        
        try {
            Process process = Runtime.getRuntime().exec("which node");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            if (line != null && !line.isEmpty()) {
                utils.log().info("Found node at: " + line);
                return line;
            }
        } catch (IOException e) {
            utils.log().info("Could not find node executable using 'which'");
        }
        
        // Try common locations
        String[] commonPaths = {
            "/usr/local/bin/node",
            "/usr/bin/node",
            System.getProperty("user.home") + "/.nvm/versions/node/*/bin/node"
        };
        
        for (String path : commonPaths) {
            if (path.contains("*")) {
                // Handle wildcard paths (like NVM paths)
                continue; // Skip for simplicity - expand if needed
            } else if (new File(path).exists()) {
                utils.log().info("Found node at common location: " + path);
                return path;
            }
        }
        
        utils.log().warn("Could not find node executable path");
        return null;
    }
    
    private String findAppiumJsPath() {
        if (isFirstRun) {
            utils.log().info("Attempting to find Appium main.js path");
        }
        
        // Check OS-specific common locations
        String osName = System.getProperty("os.name").toLowerCase();
        String[] commonPaths;
        
        if (osName.contains("win")) {
            // Windows paths
            String userHome = System.getProperty("user.home");
            String programFiles = System.getenv("ProgramFiles");
            String programFilesX86 = System.getenv("ProgramFiles(x86)");
            
            commonPaths = new String[] {
                userHome + "\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js",
                userHome + "\\AppData\\Roaming\\npm\\node_modules\\appium\\lib\\main.js",
                programFiles + "\\Appium\\resources\\app\\node_modules\\appium\\build\\lib\\main.js",
                programFilesX86 + "\\Appium\\resources\\app\\node_modules\\appium\\build\\lib\\main.js"
            };
        } else {
            // Mac/Linux paths
            String userHome = System.getProperty("user.home");
            commonPaths = new String[] {
                "/usr/local/lib/node_modules/appium/build/lib/main.js",
                "/usr/lib/node_modules/appium/build/lib/main.js",
                userHome + "/.nvm/versions/node/*/lib/node_modules/appium/build/lib/main.js",
                userHome + "/.nodenv/versions/*/lib/node_modules/appium/build/lib/main.js",
                userHome + "/node_modules/appium/build/lib/main.js",
                "/opt/homebrew/lib/node_modules/appium/build/lib/main.js" // Homebrew on Apple Silicon
            };
        }
        
        // Check common locations first (fastest approach)
        for (String path : commonPaths) {
            if (path.contains("*")) {
                // Handle wildcard paths (using simple approach)
                continue; 
            } else if (new File(path).exists()) {
                utils.log().info("Found Appium main.js at common location: " + path);
                return path;
            }
        }
        
        // Use appium executable to find installation
        try {
            String appiumExecCommand = osName.contains("win") ? "where appium" : "which appium";
            Process process = Runtime.getRuntime().exec(appiumExecCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String appiumPath = reader.readLine();
            reader.close();
            
            if (appiumPath != null && !appiumPath.isEmpty()) {
                utils.log().info("Found appium executable at: " + appiumPath);
                
                // Parse path based on OS
                String candidatePath = null;
                if (osName.contains("win")) {
                    // Windows path conversion logic 
                    if (appiumPath.contains("\\bin\\appium")) {
                        candidatePath = appiumPath.replace("\\bin\\appium", "\\node_modules\\appium\\build\\lib\\main.js");
                    } else if (appiumPath.contains("\\appium\\")) {
                        candidatePath = appiumPath.substring(0, appiumPath.lastIndexOf("\\")) + 
                                        "\\resources\\app\\node_modules\\appium\\build\\lib\\main.js";
                    }
                } else {
                    // Mac/Linux path conversion
                    if (appiumPath.contains("/bin/appium")) {
                        candidatePath = appiumPath.replace("/bin/appium", "/lib/node_modules/appium/build/lib/main.js");
                    }
                }
                
                if (candidatePath != null && new File(candidatePath).exists()) {
                    utils.log().info("Found Appium main.js at: " + candidatePath);
                    return candidatePath;
                }
            }
        } catch (IOException e) {
            utils.log().info("Error finding appium executable: " + e.getMessage());
        }
        
        // Try find command on Mac/Linux systems
        if (!osName.contains("win")) {
            try {
                String[] searchDirs = {
                    "/usr/local/lib",
                    "/usr/lib",
                    System.getProperty("user.home"),
                    "/opt/homebrew/lib"
                };
                
                for (String dir : searchDirs) {
                    if (!new File(dir).exists()) continue;
                    
                    Process process = Runtime.getRuntime().exec("find " + dir + " -name \"main.js\" | grep \"appium.*build/lib\" | head -1");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String path = reader.readLine();
                    reader.close();
                    
                    if (path != null && !path.isEmpty() && new File(path).exists()) {
                        utils.log().info("Found Appium main.js through find command at: " + path);
                        return path;
                    }
                }
            } catch (IOException e) {
                utils.log().debug("Error using find command: " + e.getMessage());
            }
        }
        
        utils.log().warn("Could not find Appium main.js path");
        return null;
    }
}

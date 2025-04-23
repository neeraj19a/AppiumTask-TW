# Mobile Automation Appium and Cucumber

This is a mobile automation testing project using Appium, Cucumber, and Java.

## Technologies/Tools used

- **Appium** - Mobile Automation library
- **Java** - Programming language
- **Maven** - Build automation tool
- **Cucumber** - BDD framework
- **TestNG** - Test Management library
- **Log4J** - Logging framework
- **Allure Reports** - Reporting framework

## Project Structure

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── qa
│   │   │           └── utils       # Utility classes for driver, capabilities, app management, etc.
│   │   └── resources               # Configuration files
│   └── test
│       ├── java
│       │   └── com
│       │       └── qa
│       │           ├── pages       # Page Objects
│       │           ├── runners     # TestNG runners
│       │           └── stepdef     # Step definitions for Cucumber
│       └── resources
│           ├── apps                # Place your app binaries here
│           └── features            # Cucumber feature files
```

## Prerequisites

- Java 16 or higher
- Maven
- Appium Server (2.0+)
- Android SDK (for Android testing)
- Mobile devices or emulators

## Setup and Configuration

1. **Install JDK**: Version 16 or higher
   - Set the `JAVA_HOME` environment variable

2. **Install Maven**:
   - Set `M2_HOME` and update the `PATH` environment variable

3. **Install Android Studio**:
   - Set up Android SDK and configure `ANDROID_HOME`
   - Create appropriate AVDs (Android Virtual Devices) for testing

4. **Install Appium**:
   ```bash
   npm install -g appium
   ```
   - Install required Appium drivers:
   ```bash
   appium driver install uiautomator2
   appium driver install xcuitest
   ```

5. **Appium Inspector (Optional)**:
   - Download and install for element inspection

## Configuration

Modify `src/main/resources/config.properties` to set your test environment configuration:

- Update app package and activity names
- Set proper app binary locations
- Configure test credentials

## Getting Started

1. Clone this repository
2. Install dependencies: `mvn clean install -DskipTests`
3. Place your application binary in the `src/test/resources/apps` directory
4. Configure the `config.properties` file with your app details
5. Run the tests: `mvn clean test`

## Core Framework Architecture

The framework is designed with a modular architecture that follows best practices for mobile test automation:

### Key Utility Classes

1. **AppManager**
   - Manages app lifecycle (install, launch, reset, close)
   - Ensures clean test environment between test runs
   - Handles app state using mobile:terminateApp and mobile:clearApp commands
   - Advantages: Prevents flaky tests by ensuring consistent app state

2. **DriverManager**
   - Initializes and provides access to Appium drivers (Android)
   - Uses ThreadLocal for parallel execution support
   - Acts as a singleton for driver access throughout the framework
   - Advantages: Thread-safe driver management, cleaner code, and better resource management

3. **CapabilitiesManager**
   - Configures and manages device/emulator capabilities
   - Handles platform-specific capabilities (Android)
   - Loads configurations from property files
   - Advantages: Centralized capability management, easy configuration changes

4. **ServerManager**
   - Programmatically starts and manages Appium server
   - Handles server cleanup between test runs
   - Cross-platform support (Windows/Mac/Linux)
   - Advantages: No need for external Appium server management

5. **GlobalParams**
   - Stores test execution parameters (platform, device, etc.)
   - Thread-safe implementation for parallel test execution
   - Advantages: Consistent parameter access across the framework

6. **PropertyManager**
   - Loads and provides access to configuration properties
   - Centralizes configuration management
   - Advantages: Easy configuration changes without code modifications

7. **TestUtils**
   - Provides common test utilities and constants
   - Handles logging through Log4j
   - Advantages: Reusable code and consistent logging

### How App and Driver Initialization Works

The framework follows a best practice initialization sequence:

1. **Server Initialization**:
   - ServerManager starts Appium server programmatically
   - Ensures clean server state by terminating any existing Appium processes

2. **Driver Initialization**:
   - DriverManager creates platform-specific driver (Android)
   - Capabilities are loaded from config through CapabilitiesManager
   - Driver is stored in ThreadLocal for thread safety

3. **App Management**:
   - AppManager handles app installation and state management
   - Uses mobile: commands for consistent app state between tests
   - Ensures app data is cleared when needed for test isolation

### Advantages of This Design

1. **Clean Separation of Concerns**:
   - Each utility class has a single responsibility
   - Modular design allows for easy maintenance and extensions

2. **Thread Safety**:
   - ThreadLocal variables enable parallel test execution
   - No shared state between test threads

3. **Test Isolation**:
   - Each test runs with a clean app state
   - Prevents test interdependencies and flakiness

4. **Configuration Flexibility**:
   - External property files for easy configuration
   - No hardcoded values in test code

5. **Reduced Boilerplate Code**:
   - Utility classes abstract complex Appium interactions
   - Page Objects can focus on business logic instead of technical details

## Framework Features

- **Page Object Model Design**: Clear separation of test logic and page interactions
- **Code Reusability**: Abstraction layer for UI commands (click, sendKeys, etc.)
- **Cross-Platform Support**: Works for both Android
- **Parameterization**: Using TestNG XML and properties files
- **Reporting**: Integrated with Allure for comprehensive test reports
- **Logging**: Log4J integration for detailed test logs
- **Screenshots**: Failure capturing mechanisms
- **Explicit Waits**: Robust waiting strategies for elements
- **Retry Mechanism**: For flaky tests
- **App State Management**: Optimized app launching and state resets between tests

## Extending the Framework

To add new test cases:
1. Create a new Feature file in `src/test/resources/features`
2. Add Step Definitions in `src/test/java/com/qa/stepdef`
3. Create Page Objects in `src/test/java/com/qa/pages`

## Reporting

This project uses Allure for test reporting. After test execution, generate reports with:

```bash
allure serve allure-results
```

## Detailed Installation Steps

### 1. Java Development Kit (JDK)
- Download the **JDK 21.0.1** from [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
- Install and set the `JAVA_HOME` environment variable.

**After Java installation, verify using the command:**

```bash
java -version
```

**Expected output:**
```bash
openjdk version "21.0.1" 2023-10-17
OpenJDK Runtime Environment Homebrew (build 21.0.1)
OpenJDK 64-Bit Server VM Homebrew (build 21.0.1, mixed mode, sharing)
```

### 2. Maven
- Download and install Maven **3.9.6** from [Apache Maven](https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/).
- Set `M2_HOME` and update the `PATH` environment variable.

**Verify Maven installation:**

```bash
mvn -v
```

**Expected output:**
```bash
Maven home: /usr/local/Cellar/maven/3.9.6/libexec
Java version: 21.0.1, vendor: Oracle Corporation, runtime: /Users/macbookpro2019/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home
```

### 3. IntelliJ IDEA
- Download and install IntelliJ IDEA from [JetBrains](https://www.jetbrains.com/idea/download/).
- Open IntelliJ and install the following plugins:
  - **Cucumber for Java**
  - **Gherkin**

### 4. Android Studio
- Download **Android Studio Giraffe** from [Android Developers](https://developer.android.com/studio/archive).
- Install and configure the Android SDK and set `ANDROID_HOME`.
- Create an AVD (Android Virtual Device) **Pixel4** for testing.

### 5. Appium
- Install Appium **2.5.1** via Node.js:

```bash
npm install -g appium@2.5.1
```

**Check Appium version:**

```bash
appium -v
```

### 6. Appium Inspector
- Download Appium Inspector from [Appium Inspector Releases](https://github.com/appium/appium-inspector/releases).
- Use it to inspect elements and generate selectors for your mobile app.

### 7. Framework Libraries Setup
- Clone the project repository.
- Import the project into IntelliJ IDEA.
- Open the `pom.xml` file and ensure the dependencies are included.

### 8. Configuring the Framework
- Ensure that the `testng.xml` file is configured for running test cases.
- Use Gherkin syntax for writing BDD scenarios in `.feature` files.

### 9. Adding the apk in Framework
- Add the apk under the path `src/test/resources/apps`.
- The name of the apk should be `latest.apk`.

### 10. Running the Tests
- To run tests using TestNG, execute the following command:

```bash
mvn clean test
```

### 11. App Management
For manual app data clearing (if needed for debugging):
```bash
adb shell pm clear com.wallet.crypto.trustapp
```

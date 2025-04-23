package com.qa.pages;

import com.qa.utils.DriverManager;
import com.qa.utils.GlobalParams;
import com.qa.utils.TestUtils;
import io.appium.java_client.*;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class BasePage {
    private final AppiumDriver driver;
    TestUtils utils = new TestUtils();
    public BasePage() {
        this.driver = new DriverManager().getDriver();
        PageFactory.initElements(new AppiumFieldDecorator(this.driver), this);
    }

    public boolean waitForVisibility(WebElement e) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        try {
            wait.until(ExpectedConditions.visibilityOf(e));
            return true; // Return true if visibility condition is met
        } catch (TimeoutException ex) {
            return false; // Return false if timeout exception occurs (element is not visible within the specified time)
        }
    }

    public WebElement fluentWait(final WebElement element, int timeout) {
        System.out.println("In Fluent Wait");
        WebElement foo = null;
        try {
            FluentWait<AppiumDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeout))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(NoSuchElementException.class);

            foo = wait.until((Function<WebDriver, WebElement>) driver -> element);
            return foo;
        } catch (Exception e) {
            utils.log().info("Exception-->" + e.toString());
        }

        return foo;
    }


    public void click(WebElement e, String msg) {
        waitForVisibility(e);
        utils.log().info(msg);
        e.click();
    }

    public String getAttribute(WebElement e, String attribute) {
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    public String getText(WebElement e) {
        String txt;
        switch (new GlobalParams().getPlatformName()) {
            case "Android":
                txt = getAttribute(e, "text");
                break;
            case "iOS":
                txt = getAttribute(e, "label");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + new GlobalParams().getPlatformName());
        }

        return txt;
    }

    protected WebElement findElementWithTimeout(By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public WebElement getElementDynamicXpath(String xpath, String dynamicText) {
        String dynamicXpath = String.format(xpath, dynamicText);
        return findElementWithTimeout(By.xpath(dynamicXpath), (int) TestUtils.WAIT);

    }

    public boolean isElementPresent(WebElement element, int timeout) {
        try {
            // Check if the element is present using Fluent Wait
            fluentWait(element, timeout);  // Reuse fluentWait method
            return true;
        } catch (Exception e) {
            // Element not found
            return false;
        }
    }


}

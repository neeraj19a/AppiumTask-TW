package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class WalletHomePage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='topBarWalletName']")
    private WebElement textField_WalletName;

    public WalletHomePage() {
        super();
    }

    public boolean isWalletHeaderVisible() {
        return waitForVisibility(textField_WalletName);
    }

    public String getWalletName() {
        return getText(textField_WalletName);
    }
} 
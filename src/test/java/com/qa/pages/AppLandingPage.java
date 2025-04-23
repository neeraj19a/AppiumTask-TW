package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class AppLandingPage extends BasePage {

    // Elements for initial landing page
    @AndroidFindBy(xpath = "//android.view.View[@resource-id='CreateNewWalletButton']")
    private WebElement button_CreateNewWallet;

    // Elements for wallet ready page
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'Brilliant, your wallet is ready')]")
    private WebElement text_WalletReady;

    public AppLandingPage() {
        super();
    }

    public CreateConfirmPasscodePage clickCreateNewWallet() {
        click(button_CreateNewWallet, "Clicking Create New Wallet button");
        return new CreateConfirmPasscodePage();
    }

    public boolean isWalletReady() {
        return isElementPresent(text_WalletReady, 5);
    }

}
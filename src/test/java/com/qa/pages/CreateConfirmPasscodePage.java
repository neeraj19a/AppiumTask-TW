package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class CreateConfirmPasscodePage extends BasePage {

    private String passcode;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Create passcode']")
    private WebElement header_CreatePasscodePage;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Confirm passcode']")
    private WebElement header_ConfirmPasscodePage;

    private String numberButtons = "//android.widget.TextView[@text='%s']";

    // Elements for wallet ready page
    @AndroidFindBy(xpath="//android.widget.TextView[contains(@text, 'Brilliant, your wallet is ready')]")
    private WebElement text_WalletReady;

    public CreateConfirmPasscodePage() {
        super();
    }

    public boolean isCreatePasscodeHeaderVisible() {
        return waitForVisibility(header_CreatePasscodePage);
    }

    public boolean isConfirmPasscodeHeaderVisible() {
        return waitForVisibility(header_ConfirmPasscodePage);
    }

    public void fillNewPasscode() {
        passcode = generateRandomPasscode();
        enterPasscode(passcode);
    }

    public CreateWalletPage fillExistingPasscode() {
        enterPasscode(passcode);
        return new CreateWalletPage();
    }

    private void enterPasscode(String code) {
        for (char digit : code.toCharArray()) {
            WebElement digitButton = getElementDynamicXpath(numberButtons, String.valueOf(digit));
            click(digitButton, "Entering passcode digit: " + digit);
        }
    }

    private String generateRandomPasscode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = (int) (Math.random() * 10);
            sb.append(digit);
        }
        return sb.toString();
    }

    public boolean isWalletReady() {
        return isElementPresent(text_WalletReady, 5);
    }


} 
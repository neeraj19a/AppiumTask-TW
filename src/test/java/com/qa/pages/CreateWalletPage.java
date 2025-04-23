package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class CreateWalletPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'Skip')]")
    private WebElement button_SkipNotification;

    public CreateWalletPage() {
        super();
    }

    public void clickSkipButton() {
        click(button_SkipNotification, "Clicking Skip Notification button");
    }


} 
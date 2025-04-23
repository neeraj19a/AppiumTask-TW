package com.qa.stepdef;

import com.qa.pages.AppLandingPage;
import com.qa.pages.CreateConfirmPasscodePage;
import com.qa.pages.CreateWalletPage;
import com.qa.pages.WalletHomePage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;
import org.junit.Assert;

public class TrustWalletStepDef {

    private AppLandingPage appLandingPage;
    private CreateConfirmPasscodePage createConfirmPasscodePage;
    private CreateWalletPage createWalletPage;
    private WalletHomePage walletHomePage;
    
    @Before
    public void setupPages() {
        appLandingPage = new AlwaysFreshAppLandingPage();
        createConfirmPasscodePage = new CreateConfirmPasscodePage();
        createWalletPage = new CreateWalletPage();
        walletHomePage = new WalletHomePage();
    }

    @When("I click on Create New Wallet button")
    public void iClickOnCreateNewWalletButton() {
        createConfirmPasscodePage = appLandingPage.clickCreateNewWallet();
    }

    @Then("I should see the Create Passcode page")
    public void iShouldSeeTheCreatePasscodePage() {
        Assert.assertTrue("Create Passcode page is not displayed", createConfirmPasscodePage.isCreatePasscodeHeaderVisible());
    }

    @When("I enter a new passcode")
    public void iEnterANewPasscode() {
        createConfirmPasscodePage.fillNewPasscode();
    }

    @Then("I should see the Confirm Passcode page")
    public void iShouldSeeTheConfirmPasscodePage() {
        Assert.assertTrue("Confirm Passcode page is not displayed", createConfirmPasscodePage.isConfirmPasscodeHeaderVisible());
    }

    @When("I confirm the passcode")
    public void iConfirmThePasscode() {
        createWalletPage = createConfirmPasscodePage.fillExistingPasscode();
    }
    
    @Then("I should see the if wallet is ready")
    public void iShouldSeeWalletReady() {
        Assert.assertTrue("Looks like wallet is not ready", createConfirmPasscodePage.isWalletReady());
    }

    @When("I skip the notification")
    public void iSkipTheNotification() {
        createWalletPage.clickSkipButton();
    }

    @Then("I should see the Wallet Home page")
    public void iShouldSeeTheWalletHomePage() {
        Assert.assertTrue("Wallet Home page is not displayed", walletHomePage.isWalletHeaderVisible());
    }

    @Then("the wallet name should be {string}")
    public void theWalletNameShouldBe(String expectedName) {
        String walletName = walletHomePage.getWalletName();
        Assert.assertEquals("Wallet name is not correct", expectedName, walletName);
    }
    
    // Custom subclass that forces fresh wallet creation flow
    private class AlwaysFreshAppLandingPage extends AppLandingPage {
        @Override
        public boolean isWalletReady() {
            return false;
        }
    }
} 
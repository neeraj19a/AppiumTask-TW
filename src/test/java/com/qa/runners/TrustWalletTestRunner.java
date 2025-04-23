package com.qa.runners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = {"src/test/resources/TrustWallet.feature"},
        glue = {"com.qa.stepdef"},
        dryRun = false,
        monochrome = true,
        tags = "@trustwallet",
        plugin = {
                "pretty",
                "html:target/cucumber/trustwallet-report.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        }
)
public class TrustWalletTestRunner extends RunnerBase {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
} 
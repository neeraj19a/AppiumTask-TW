Feature: TrustWallet app tests
  As a user
  I want to test the TrustWallet app functionality
  So that I can ensure it works correctly

  @trustwallet
  Scenario: Create a new secret phrase wallet
    When I click on Create New Wallet button
    Then I should see the Create Passcode page
    When I enter a new passcode
    Then I should see the Confirm Passcode page
    When I confirm the passcode
    Then I should see the if wallet is ready
    When I skip the notification
    Then I should see the Wallet Home page
    And the wallet name should be "Main Wallet 1"


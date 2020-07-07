Feature: scenario Outline Test

 # Background: LocalHost Navigation

 # @API
  Scenario Outline:Create Jira Bug
    Given The user enter  "<bug>", "<Description>" and "<name>"
    When The user goes to the LocalHost8080
    When The user click last created one
    Then click viewWorkflow
    And validate information
    Examples:
      | bug                     | Description              | name |
      | Creating a bug from API | Story created throuh API | Bug  |
      | Login Functionality     | Checking Functions       | Bug  |
      | Validation Products     | Validate exists prodocts | Bug  |
      | Regression Testing      | Regression               | Bug  |
      | Smoke Testing           | Smoke                    | Bug  |

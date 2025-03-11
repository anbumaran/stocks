Feature: Civil Service Jobs Search

  Scenario: Search for Analyst jobs in London
    Given I launch the Civil Service Jobs website
    When I search for jobs with title containing "Analyst" in "London"
    Then I should see relevant job listings

  @StretchGoal
  Scenario: Filter jobs by Department and verify job count
    Given I filter by department "Medicines and Healthcare products Regulatory Agency"
    When  I should see the number of jobs available in the job details page
    Then I select the first job listing

  @ChallengeGoal
  Scenario: Verify Civil Service Code link
  Given I click on Civil Service Code
  When I should be redirected to the Civil Service Commission Website


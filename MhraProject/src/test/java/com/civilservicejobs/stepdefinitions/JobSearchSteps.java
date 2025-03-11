package com.civilservicejobs.stepdefinitions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.civilservicejobs.pages.CivilServiceCodePage;
import com.civilservicejobs.pages.HomePage;
import com.civilservicejobs.pages.JobDetailsPage;
import com.civilservicejobs.pages.SearchResultsPage;
import com.civilservicejobs.utilities.BrowserUtils;
import com.civilservicejobs.utilities.ExtentReport;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class JobSearchSteps {

    private static final Logger logger = LogManager.getLogger(JobSearchSteps.class);

    WebDriver driver = BrowserUtils.getDriver();
    HomePage homePage = new HomePage(driver);
    SearchResultsPage searchResultsPage = new SearchResultsPage(driver);
    JobDetailsPage jobDetailsPage = new JobDetailsPage(driver);
    CivilServiceCodePage civilServiceCodePage = new CivilServiceCodePage(driver);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

    // Initialize ExtentReports and ExtentTest
    static ExtentReports extent = ExtentReport.createInstance("extent-report.html");
    ExtentTest test;

    @Given("I launch the Civil Service Jobs website")
    public void launchWebsite() {
        test = extent.createTest("Launch Civil Service Jobs Website");
        logger.info("Launching the Civil Service Jobs website");
        test.log(Status.INFO, "Launching the Civil Service Jobs website");
        driver.manage().window().maximize();
        driver.get("https://www.civilservicejobs.service.gov.uk/csr/index.cgi");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            // Check if the CAPTCHA checkbox is present
            WebElement captchaCheck = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("altcha_checkbox")));
            logger.info("CAPTCHA checkbox is present");
            test.log(Status.INFO, "CAPTCHA checkbox is present");

            // Wait for the CAPTCHA checkbox to be clickable and click it using JavaScript
            wait.until(ExpectedConditions.elementToBeClickable(captchaCheck));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", captchaCheck);
            logger.info("Clicked the CAPTCHA checkbox");
            test.log(Status.INFO, "Clicked the CAPTCHA checkbox");

            // Wait for the "Verified" text to be visible
            WebElement verifiedText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Verified']")));
            logger.info("Verified text is visible");
            test.log(Status.INFO, "Verified text is visible");
        } catch (NoSuchElementException e) {
            logger.info("CAPTCHA checkbox is not present");
            test.log(Status.INFO, "CAPTCHA checkbox is not present");
        }

        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Continue')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", continueButton);
        logger.info("Clicked the Continue button");
        test.log(Status.INFO, "Clicked the Continue button");
    }

    @When("I search for jobs with title containing {string} in {string}")
    public void searchJobs(String jobTitle, String location) {
        test = extent.createTest("Search Jobs");
        logger.info("Searching for jobs with title containing '{}' in '{}'", jobTitle, location);
        test.log(Status.INFO, "Searching for jobs with title containing '" + jobTitle + "' in '" + location + "'");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait for the job title input field to be present and enter the job title
        WebElement jobTitleInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("what")));
        jobTitleInput.sendKeys(jobTitle);

        // Wait for the location input field to be present and enter the location
        WebElement locationInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("whereselector")));
        locationInput.sendKeys(location);

        // Wait for the search button to be clickable and click it using JavaScript
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submitSearch")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
        logger.info("Clicked the search button");
        test.log(Status.INFO, "Clicked the search button");
    }

    @Then("I should see relevant job listings")
    public void validateSearchResults() {
        test = extent.createTest("Validate Search Results");
        logger.info("Validating search results");
        test.log(Status.INFO, "Validating search results");

        // Scroll down to the job listings section
        WebElement jobListingsSection = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h3[@class='search-results-job-box-title']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", jobListingsSection);

        // Wait for the search results to be visible
        List<WebElement> jobListings = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//h3[@class='search-results-job-box-title']")));

        // Verify that job listings are displayed
        Assert.assertFalse("No job listings found", jobListings.isEmpty());
        for (WebElement jobListing : jobListings) {
            logger.info("Job listing found: {}", jobListing.getText());
            test.log(Status.INFO, "Job listing found: " + jobListing.getText());
        }
    }

    @Given("I filter by department {string}")
    public void filterByDepartment(String department) {
        test = extent.createTest("Filter by Department");
        logger.info("Filtering by department '{}'", department);
        test.log(Status.INFO, "Filtering by department '" + department + "'");
        searchResultsPage.selectDepartment(department);
    }

    @When("I should see the number of jobs available in the job details page")
    public int getJobCount() {
        test = extent.createTest("Get Job Count");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@title='Job list']/li[contains(@class, 'search-results-job-box')]")));

        List<WebElement> jobListings = driver.findElements(By.xpath("//ul[@title='Job list']/li[contains(@class, 'search-results-job-box')]"));

        int jobCount = jobListings.size();
        logger.info("Total job listings found: {}", jobCount);
        test.log(Status.INFO, "Total job listings found: " + jobCount);

        if (jobCount == 0) {
            logger.error("No job listings found");
            test.log(Status.INFO, "No job listings found");
            throw new IllegalStateException("No job listings found");
        }

        System.out.println("Total job listings found: " + jobListings.size());
        return jobListings.size();
    }

    @Then("I select the first job listing")
    public void selectFirstJob() {
        test = extent.createTest("Select First Job Listing");
        logger.info("Selecting the first job listing");
        test.log(Status.INFO, "Selecting the first job listing");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"main-content\"]/div[2]/div/div[2]/ul/li/div[2]/h3/a")));
        List<WebElement> jobListings = driver.findElements(By.xpath("//*[@id=\"main-content\"]/div[2]/div/div[2]/ul/li/div[2]/h3/a"));
        if (jobListings.isEmpty()) {
            logger.error("No job listings found");
            test.log(Status.INFO, "No job listings found");
            Assert.fail("No job listings found");
        } else {
            WebElement firstJobListing = jobListings.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstJobListing);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstJobListing);
        }
    }

    @Given("I click on Civil Service Code")
    public void i_click_on_civil_service_code() {
        test = extent.createTest("Click on Civil Service Code");
        // Initialize WebDriverWait with a timeout of 30 seconds
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        logger.info("Initialized WebDriverWait with 30 seconds timeout");
        test.log(Status.INFO, "Initialized WebDriverWait with 30 seconds timeout");

        // Find the link element by its text
        WebElement link = driver.findElement(By.xpath("//a[contains(text(),'Civil Service Code')]"));
        logger.info("Located the Civil Service Code link");
        test.log(Status.INFO, "Located the Civil Service Code link");

        // Wait for the link to be present and visible
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(text(),'Civil Service Code')]")));
        logger.info("Waited for the Civil Service Code link to be present and visible");
        test.log(Status.INFO, "Waited for the Civil Service Code link to be present and visible");

        // Scroll into view and click the link using JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        logger.info("Scrolled the Civil Service Code link into view");
        test.log(Status.INFO, "Scrolled the Civil Service Code link into view");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        logger.info("Clicked on the Civil Service Code link");
        test.log(Status.INFO, "Clicked on the Civil Service Code link");
    }

    @When("I should be redirected to the Civil Service Commission Website")
    public void i_should_be_redirected_to_the_civil_service_commission_Website() {
        test = extent.createTest("Redirect to Civil Service Commission Website");
        // Get the window handle of the current window (before opening a new one)
        String currentWindowHandle = driver.getWindowHandle();
        logger.info("Stored the current window handle");
        test.log(Status.INFO, "Stored the current window handle");

        // Wait for a new window to open (this assumes the link opens in a new window)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2)); // Wait for two windows to be opened
        logger.info("Waited for a new window to open");
        test.log(Status.INFO, "Waited for a new window to open");

        // Switch to the new window
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            if (!windowHandle.equals(currentWindowHandle)) {
                driver.switchTo().window(windowHandle);
                logger.info("Switched to the new window");
                test.log(Status.INFO, "Switched to the new window");
                break;  // Switch to the new window
            }
        }

        // Wait for the URL to match the Civil Service Commission Code section
        WebDriverWait urlWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        urlWait.until(ExpectedConditions.urlContains("civilservicecommission.independent.gov.uk/code"));
        logger.info("Waited for the URL to contain 'civilservicecommission.independent.gov.uk/code'");
        test.log(Status.INFO, "Waited for the URL to contain 'civilservicecommission.independent.gov.uk/code'");

        // Get the current URL after the redirection
        String currentUrl = driver.getCurrentUrl();
        logger.info("Current URL after redirection: {}", currentUrl);
        test.log(Status.INFO, "Current URL after redirection: " + currentUrl);

        // Validate that the URL contains the 'code' path
        assertTrue("Expected redirection to the 'Code' section, but found: " + currentUrl,
                currentUrl.contains("civilservicecommission.independent.gov.uk/code"));
        logger.info("Redirected to the 'Code' section, the URL is: {}", currentUrl);
        test.log(Status.INFO, "Redirected to the 'Code' section, the URL is: " + currentUrl);
    }

    @AfterClass
    public static void tearDown() {
        ExtentReport.endTest();
    }
}
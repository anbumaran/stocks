package com.civilservicejobs.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public class SearchResultsPage {

    WebDriver driver;

    @FindBy(css = "#oselect_title_4e24")
    private WebElement departmentFilter;

    @FindBy(className = "//h3[@class='search-results-job-box-title']")
    private List<WebElement> jobListings;

    @FindBy(id = "submitSearch")
    private WebElement searchButton;

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void selectDepartment(String department) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // Click the "Department" dropdown to expand it
        WebElement departmentDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//*[text()=' Department '])[1]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", departmentDropdown);
        //logger.info("Clicked the Department dropdown");


        // Wait for the department filter input field to be present and enter the department name
        WebElement departmentInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Medicines and Healthcare Products Regulatory Agency')]/..//input")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", departmentInput);
        departmentInput.sendKeys(department);

        // Wait for the search button to be clickable and click it using JavaScript
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submitSearch")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);

    }

    public List<WebElement> getJobListings() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        //logger.info("Getting job listings");
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//ul[@title='Job list']/li[contains(@class, 'search-results-job-box')]")));
    }

    public boolean areResultsDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        //logger.info("Checking if search results are displayed");
        // Implement logic to verify if search results are displayed
        List<WebElement> jobListings = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//h3[@class='search-results-job-box-title']")));
        return !jobListings.isEmpty();
    }

    public void clickFirstJobListing() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        //logger.info("Clicking the first job listing");
        // Wait for the first job listing to be clickable and click it
        WebElement firstJobListing = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//h3[@class='search-results-job-box-title'])[1]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstJobListing);
        firstJobListing.click();
    }


}

package com.civilservicejobs.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JobDetailsPage {
    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(JobDetailsPage.class);

    public JobDetailsPage(WebDriver driver) {
        this.driver = driver;
    }

    public int getJobCount() {

        // Find all job listing elements on the page
        List<WebElement> jobListings = driver.findElements(By.xpath("//*[@class='search-results-job-box-title']"));

        // Log the number of jobs found
        int jobCount = jobListings.size();
        logger.info("Extracted job count: " + jobCount);

        // Ensure job count is not zero
        if (jobCount == 0) {
            logger.error("No job listings found");
            throw new IllegalStateException("No job listings found");
        }

        return jobCount;
    }

}
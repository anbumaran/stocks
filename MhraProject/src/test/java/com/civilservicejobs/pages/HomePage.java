package com.civilservicejobs.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    WebDriver driver;
    @FindBy(name="what")
    private WebElement jobTitleField;
    @FindBy(id="whereselector")
    private WebElement locationField;
    @FindBy(id="submitSearch")
    private WebElement searchButton;
    @FindBy(linkText = "Working for the Civil Service")
    private WebElement workingForCivilServiceLink;
    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterJobTitle(String jobTitle) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", jobTitleField);
        jobTitleField.clear();
        jobTitleField.sendKeys(jobTitle);
        jobTitleField.sendKeys(Keys.TAB);
    }

    public void enterLocation(String location) {
        jobTitleField.clear();
        locationField.sendKeys(location);
    }

    public void clickSearchButton() {
        searchButton.click();
    }

    public void clickLink(String linkText) {
        if (linkText.equals("Working for the Civil Service")) {
            workingForCivilServiceLink.click();
        }
    }
}
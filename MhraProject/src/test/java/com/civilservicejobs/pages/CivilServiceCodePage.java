package com.civilservicejobs.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CivilServiceCodePage {

    WebDriver driver;

    @FindBy(linkText = "Civil Service Code")
    private WebElement civilServiceCodeLink;

    @FindBy(id = "code")
    private WebElement codeSection;

    public CivilServiceCodePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickLink(String linkText) {
        if (linkText.equals("Civil Service Code")) {
            civilServiceCodeLink.click();
        }
    }

    public boolean isCodeSectionDisplayed() {
        return codeSection.isDisplayed();
    }
}
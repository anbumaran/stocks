package com.asapp.ui.listners;


import com.asapp.common.utils.DateTimeUtil;
import com.asapp.ui.BaseTest;
import com.asapp.ui.driver.MyWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static com.asapp.TestConstants.FAILED_SCREENSHOT_BASE_DIR;

public class MyTestWatcher extends BaseTest implements TestWatcher, TestInstancePostProcessor {

    private static final Logger LOGGER = LogManager.getLogger(MyWebDriver.class);
    WebDriver webDriver;

    MyTestWatcher() {
        this.webDriver = new MyWebDriver().getWebDriver();
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        TestWatcher.super.testDisabled(context, reason);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        TestWatcher.super.testSuccessful(context);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        TestWatcher.super.testAborted(context, cause);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        TestWatcher.super.testFailed(context, cause);
        String testDisplayName = context.getDisplayName();
        LOGGER.info("Failed Test Class - {}, Test Method - {}, Test Display Name - {}",
                context.getTestClass(), context.getTestMethod(), testDisplayName);
        captureScreenshot(testDisplayName, webDriver);
    }

    private void captureScreenshot(String testName, WebDriver driver) {

        // Capture screenshot as a file
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // Define the screenshot destination directory
        File screenshotDir = new File(FAILED_SCREENSHOT_BASE_DIR);

        if (!screenshotDir.exists()) screenshotDir.mkdir();

        String subDir = FAILED_SCREENSHOT_BASE_DIR + "/" + DateTimeUtil.getCurrentDataTimeAsString("yyyy-MMM-dd");

        File screenshotSubDir = new File(subDir);
        if (!screenshotSubDir.exists()) screenshotSubDir.mkdir();

        String timeStamp = "-TS-" + DateTimeUtil.getCurrentDataTimeAsString("yyyy-MMM-dd_HH-mm-ss");

        // Define the file pattern for the screenshot
        String screenshotPath = Paths.get(subDir, testName + timeStamp + ".png").toString();

        // Save the screenshot file
        boolean isScreenShortCreated = screenshotFile.renameTo(new File(screenshotPath));

        if (isScreenShortCreated) {
            LOGGER.info("Failed Screenshot Created at - {}", screenshotPath);
        } else {
            throw new IllegalStateException("Unable to Create Screenshot at : " + screenshotPath);
        }

    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        if (testInstance instanceof BaseTest) {
            ((BaseTest) testInstance).setDriver(webDriver);
        }
    }

}

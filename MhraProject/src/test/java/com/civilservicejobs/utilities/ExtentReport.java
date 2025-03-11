package com.civilservicejobs.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReport {

    private static ExtentReports extent;
    private static ExtentTest test;

    // Singleton pattern to ensure only one instance of ExtentReports
    public static ExtentReports createInstance(String fileName) {
        if (extent == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);

            // Custom configurations
            sparkReporter.config().setDocumentTitle("Custom Test Automation Report");
            sparkReporter.config().setReportName("Custom Civil Service Jobs Test Report");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setEncoding("utf-8");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
        }
        return extent;
    }

    // Start a test case
    public static ExtentTest startTest(String testName, String description) {
        if (extent == null) {
            throw new IllegalStateException("ExtentReports instance is not initialized. Call createInstance() first.");
        }
        test = extent.createTest(testName, description);
        return test;
    }

    // Attach logs to the report
    public static void logTestInfo(String info) {
        if (test != null) {
            test.info(info);
        } else {
            System.err.println("Test instance is not initialized. Call startTest() first.");
        }
    }

    // Attach screenshots or other media to the report
    public static void addScreenshot(String screenshotPath) {
        if (test != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                System.err.println("Failed to add screenshot: " + e.getMessage());
            }
        } else {
            System.err.println("Test instance is not initialized. Call startTest() first.");
        }
    }

    // End the test and flush the report
    public static void endTest() {
        if (extent != null) {
            extent.flush();
        }
    }
}
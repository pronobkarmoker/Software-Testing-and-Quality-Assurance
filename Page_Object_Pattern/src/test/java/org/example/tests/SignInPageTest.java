package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.example.pages.SignInPage;
import org.example.pages.DashboardPage;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignInPageTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
    private SignInPage signInPage;
    private DashboardPage dashboardPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
        signInPage = new SignInPage(driver);
        dashboardPage = new DashboardPage(driver);
        driver.manage().window().setSize(new Dimension(654, 751));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void validSignIn() {
        driver.get("http://localhost:4000/sign_in");

        signInPage.enterEmail("bsse1412@iit.du.ac.bd");
        signInPage.enterPassword("iit123");
        signInPage.clickLoginButton();

        assertEquals("Samdani Mozumder", dashboardPage.getUserName());
    }

    @Test
    public void invalidSignIn() {
        driver.get("http://localhost:4000/sign_in");

        signInPage.enterEmail("bsse1412@iit.du.ac.bd");
        signInPage.enterPassword("iit");
        signInPage.clickLoginButton();

        assertEquals("Invalid email or password", signInPage.getErrorMessage());

    }

}
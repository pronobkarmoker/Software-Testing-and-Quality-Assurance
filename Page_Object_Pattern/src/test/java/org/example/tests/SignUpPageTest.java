package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.example.pages.SignUpPage;
import org.example.pages.DashboardPage;
import java.util.*;

public class SignUpPageTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    JavascriptExecutor js;
    private SignUpPage signUpPage;
    private DashboardPage dashboardPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
        signUpPage = new SignUpPage(driver);
        dashboardPage = new DashboardPage(driver);
        driver.manage().window().setSize(new Dimension(654, 751));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void validSignUp() {
        driver.get("http://localhost:4000/sign_up");
        driver.manage().window().setSize(new Dimension(602, 781));

        signUpPage.enterFirstName("Samdani");
        signUpPage.enterLastName("Mozumder");
        signUpPage.enterEmail("bsse1413@iit.du.ac.bd");
        signUpPage.enterPassword("iit123");
        signUpPage.enterPasswordConfirmation("iit123");
        signUpPage.clickLoginButton();
        assertThat(dashboardPage.getUserName(), is("Samdani Mozumder"));
    }

    @Test
    public void invalidPasswordLength() {
        driver.get("http://localhost:4000/sign_up");

        signUpPage.enterFirstName("Samdani");
        signUpPage.enterLastName("Mozumder");
        signUpPage.enterEmail("bsse1413@iit.du.ac.bd");
        signUpPage.enterPassword("iit");
        signUpPage.enterPasswordConfirmation("iit");
        signUpPage.clickLoginButton();

        assertEquals("should be at least 5 character(s)", signUpPage.getErrorMessage());
    }
}
package org.example.tests;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.example.pages.DashboardPage;
import org.example.pages.SignInPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DashboardPageTest {
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

    public void signIn(){
        signInPage.enterEmail("bsse1412@iit.du.ac.bd");
        signInPage.enterPassword("iit123");
        signInPage.clickLoginButton();
    }

    @Test
    public void signOutButtonTest() {
        driver.get("http://localhost:4000/");
        driver.manage().window().setSize(new Dimension(602, 933));
        signIn();
        dashboardPage.clickSignoutButton();
        assertTrue(signInPage.isSignInPageDisplayed());
    }
}
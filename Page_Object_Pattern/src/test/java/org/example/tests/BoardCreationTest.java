package org.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.example.pages.DashboardPage;
import org.example.pages.SignInPage;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BoardCreationTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    private JavascriptExecutor js;
    private SignInPage signInPage;
    private DashboardPage dashboardPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
        signInPage = new SignInPage(driver);
        dashboardPage = new DashboardPage(driver);
        driver.manage().window().setSize(new Dimension(602, 971));
        vars = new HashMap<>();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    public void signIn(){
        signInPage.enterEmail("john@phoenix-trello.com");
        signInPage.enterPassword("12345678");
        signInPage.clickLoginButton();
    }

    @Test
    public void boardCreation() {
        driver.get("http://localhost:4000/");

        signIn();

        dashboardPage.clickAddNewBoard();
        dashboardPage.enterBoardName("Testing");
        dashboardPage.clickSubmitButton();

        assertThat(dashboardPage.getBoardTitle(), is("Testing"));
    }
}
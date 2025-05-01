/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.selenium;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Jonáš
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateAlbumPageTest {

    private WebDriver driver;

    @BeforeAll
    void setUp() throws Exception {
        boolean useRemoteWebDriver = false; // change to true if using container browser

        if (useRemoteWebDriver) {
            // connect to chrome container
            ChromeOptions options = new ChromeOptions();
            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
        } else {
            // use local installed Chrome
            System.setProperty("webdriver.chrome.driver",
                    "C:\\Users\\START\\Documents\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
            driver = new ChromeDriver();
        }
    }

    @Test
    void testCreateAndDeleteLoggedIn() {
        driver.get("http://localhost:8080/photo-management-app/login.xhtml");

        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys("admin");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("admin");

        WebElement submitButton = driver.findElement(By.xpath("//button[normalize-space()='Log in']"));
        submitButton.click();

        waitForPageToLoadCompletely();

        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("Photo"), "Page title should contain 'Photo' but contains " + title + " instead");

        WebElement albumsButton = driver.findElement(By.cssSelector("a[href='albums.xhtml']"));
        albumsButton.click();
        waitForPageToLoadCompletely();

        WebElement newAlbumButton = driver.findElement(By.cssSelector("input[value='New album']"));
        newAlbumButton.click();
        waitForPageToLoadCompletely();

        WebElement albumTitle = driver.findElement(By.id("create-album-form:album-name"));
        String testAlbumTitle = "Test album";
        albumTitle.sendKeys(testAlbumTitle);

        WebElement albumDescription = driver.findElement(By.id("create-album-form:album-description"));
        String testAlbumDescription = "Test album description";
        albumDescription.sendKeys(testAlbumDescription);

        WebElement createAlbumButton = driver.findElement(By.cssSelector("input[value='Create'][name='create-album-form:j_idt28']"));
        createAlbumButton.click();
        waitForPageToLoadCompletely();

        WebElement albumLink = driver.findElement(By.partialLinkText(testAlbumTitle));
        Assertions.assertNotNull(albumLink, "Link with the given album title does not exist.");

        List<WebElement> deleteAlbumButtons = driver.findElements(By.className("delete"));

        Assertions.assertTrue(deleteAlbumButtons.size() != 0);
        deleteAlbumButtons.get(deleteAlbumButtons.size() - 1).click();

        waitForPageToLoadCompletely();
        List<WebElement> confirmAlbumButtons = driver.findElements(By.className("confirm"));
        List<WebElement> cancelAlbumButtons = driver.findElements(By.className("cancel"));
        System.out.println(confirmAlbumButtons);
        System.out.println(cancelAlbumButtons);

        Assertions.assertTrue(confirmAlbumButtons.size() != 0);
        confirmAlbumButtons.get(confirmAlbumButtons.size() - 1).click();

        Assertions.assertThrows(NoSuchElementException.class,
                () -> driver.findElement(By.partialLinkText(testAlbumTitle)));
    }

    @Test
    void testCreateLoggedOut() {
        driver.get("http://localhost:8080/photo-management-app/albums.xhtml");

        waitForPageToLoadCompletely();

        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("Albums"), "Page title should contain 'Albums' but contains " + title + " instead");


        List<WebElement> newAlbumButtons = driver.findElements(By.cssSelector("input[value='New album']"));
        Assertions.assertTrue(newAlbumButtons.isEmpty());

    }

    private void waitForPageToLoadCompletely() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }


    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

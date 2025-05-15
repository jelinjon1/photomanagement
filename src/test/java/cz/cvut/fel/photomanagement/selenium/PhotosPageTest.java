/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.selenium;

import cz.cvut.fel.photomanagement.config.TestConstants;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Integration tests using Selenium to test user input on photos.xhtml.
 *
 * @author jelinjon
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhotosPageTest {

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

    private void login() {
        driver.get(TestConstants.URL.concat("login.xhtml"));

        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys(TestConstants.TEST_USERNAME);

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(TestConstants.TEST_PASSWORD);

        WebElement submitButton = driver.findElement(By.xpath("//button[normalize-space()='Log in']"));
        submitButton.click();

        waitForPageToLoadCompletely();

        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("Photo management application"),
                "Page title should contain 'Photo management application' but contains " + title + " instead");
    }

    @Test
    void addPhotosToNewAlbumTest() {
        login();

        WebElement photosLink = driver.findElement(By.id("home-navigation-form:photos-link"));
        photosLink.click();
        waitForPageToLoadCompletely();

        WebElement albumDropdownmenu = driver.findElement(By.id("photos-form:select-album-menu"));
        Select albumSelect = new Select(albumDropdownmenu);
        albumSelect.selectByValue("0");


        WebElement addToAlbumButton = driver.findElement(By.id("photos-form:addtoalbum-button"));
        addToAlbumButton.click();
        waitForPageToLoadCompletely();

        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("New album"),
                "Page title should contain 'New album' but contains " + title + " instead");

    }

    @Test
    void uploadFileTest() {
        login();

        WebElement photosLink = driver.findElement(By.id("home-navigation-form:photos-link"));
        photosLink.click();
        waitForPageToLoadCompletely();

        File uploadFile = TestConstants.TEST_FILES.get(0);
        String fileName = uploadFile.getName();

        if (!uploadFile.exists()) {
            fail("File for test upload not found on given path.");
        }

        WebElement fileInput = driver.findElement(By.id("upload-form:choose-files-button_input"));
        fileInput.sendKeys(uploadFile.getAbsolutePath());
        driver.findElement(By.id("upload-form:upload-button")).click();
        waitForPageToLoadCompletely();

        List<WebElement> images = driver.findElements(By.tagName("img"));
        if (images.size() == 0) {
            fail("Uploaded image was not rendered on the page.");
        }
        WebElement uploadedImage = images.get(images.size() - 1);
        Path imagePath = Paths.get(uploadedImage.getDomAttribute("src"));

        Assertions.assertEquals(fileName, imagePath.getFileName().toString(),
                "Filename read from last uploaded image does not match the filename of the uploaded image.");

    }
}

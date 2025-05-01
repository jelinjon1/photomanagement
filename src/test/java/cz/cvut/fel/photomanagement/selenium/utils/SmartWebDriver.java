/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.selenium.utils;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Jonáš
 */
public class SmartWebDriver implements WebDriver, JavascriptExecutor {

    private final WebDriver driver;
    private final int timeoutSeconds = 5;

    public SmartWebDriver(WebDriver driver) {
        this.driver = driver;
    }

    private void waitForPageLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
                d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete")
        );
    }

    @Override
    public void get(String url) {
        driver.get(url);
        waitForPageLoad();
    }

    @Override
    public Navigation navigate() {
        Navigation nav = driver.navigate();
        waitForPageLoad();
        return nav;
    }

    @Override
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeAsyncScript(script, args);
    }
}

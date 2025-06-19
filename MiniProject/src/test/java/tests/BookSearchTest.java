package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;
import utilities.*;

import java.time.Duration;
import java.util.List;

public class BookSearchTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    @Parameters("browser")
    public void setup(String browser) throws Exception {
        // Start browser from BrowserFactory
        driver = BrowserFactory.startApplication(browser);

        // ✅ Add this line to log which browser is being used
        System.out.println("Running on browser: " + ((HasCapabilities) driver).getCapabilities().getBrowserName());

        // Load URL from config
        driver.get(ConfigReader.getProperty("url"));

        wait = new WebDriverWait(driver,Duration.ofSeconds(15));
    }

    @Test
    public void searchBookTest() throws Exception {
        System.out.println("Starting book search test...");

        // Get search keyword from Excel
        String excelPath = ConfigReader.getProperty("excelPath");
        String sheetName = ConfigReader.getProperty("sheetName");
        
        String bookName = ExcelUtils.getCellData(excelPath, sheetName, 1, 0); // row 1, col 0

        // Step 1: Enter book name in search box
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputbar")));
        searchBox.clear();
        searchBox.sendKeys(bookName);

        // Step 2: Click on Search button
        WebElement searchButton = driver.findElement(By.id("btnTopSearch"));
        searchButton.click();

        // Step 3: Wait for result count
        WebElement resultCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".preferences-show b")));
        String resultText = resultCountElement.getText().trim();
        int resultCount = Integer.parseInt(resultText);
        System.out.println("Total search results: " + resultCount);
        Assert.assertTrue(resultCount > 10, "Expected more than 10 results");

        // Step 4: Sort by Price Low to High
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("ddlSort")));
        Select sortSelect = new Select(sortDropdown);
        sortSelect.selectByVisibleText("Price - Low to High");

        // Step 5: Wait for sorted results
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#listSearchResult .list-view-books")));

        // Step 6: Capture book titles and prices   
        List<WebElement> bookItems = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
            By.cssSelector("#listSearchResult .list-view-books"), 4));

        System.out.println("\nTop 5 books sorted by Price - Low to High:");
        for (int i = 0; i < 5; i++) {
            WebElement book = bookItems.get(i);

            String title = book.findElement(By.cssSelector(".title")).getText().trim();
            String price = book.findElement(By.cssSelector(".sell")).getText().trim();

            System.out.println("Book Title: " + title);
            System.out.println("Book Price: " + price);
            System.out.println("-------------------------");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

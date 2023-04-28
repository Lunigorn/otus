import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(Lifecycle.PER_CLASS)
public class ExecutingJS {

    protected static WebDriver driver;
    protected Actions action;

    private static final String CONSOLE_LOG = "var test = 'I am text'; console.log(test);";
    private static final String RETURN_TEXT = "return 'text'";
    private static final String RETURN_NUMBER = "return 26";
    private static final String RETURN_BOOL = "return true";
    private static final String RETURN_ELEMENT = "return document.querySelector('#text');";

    @Test
    public void takeScreenshot() throws IOException {
        driver.get("https://ya.ru");

        driver.findElement(By.cssSelector("#text")).clear();
        driver.findElement(By.cssSelector("#text")).sendKeys("Base64");
        String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        saveBase64(base64);

        driver.findElement(By.cssSelector("#text")).clear();
        driver.findElement(By.cssSelector("#text")).sendKeys("Bytes");
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        saveBytes(bytes);

        driver.findElement(By.cssSelector("#text")).clear();
        driver.findElement(By.cssSelector("#text")).sendKeys("File");
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        saveFile(file);
    }

    @Test
    public void uploadFileWithSubmit() throws IOException, URISyntaxException {
        driver.get("https://ya.ru");

        WebElement uploadInput = driver.findElement(By.cssSelector("#image_search"));
        String pathToFile = Paths.get(getClass().getClassLoader().getResource("red.jpg").toURI()).toAbsolutePath().toString();
        uploadInput.sendKeys(pathToFile);

        WebElement form = driver.findElement(By.cssSelector("form[role='search']"));
        form.submit();
        System.out.println("Done");
    }
    @Test
    public void uploadFileWithOnchange() throws IOException, URISyntaxException {
        driver.get("https://ya.ru");

        WebElement uploadInput = driver.findElement(By.cssSelector("#image_search"));
        uploadInput.sendKeys(Paths.get(getClass().getClassLoader().getResource("red.jpg").toURI()).toAbsolutePath().toString());

        ((JavascriptExecutor) driver).executeScript("arguments[0].onchange", Arrays.asList(uploadInput));

        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        saveFile(file);
    }

    @Test
    public void elementScreenshot() throws IOException {
        driver.get("https://ya.ru");

        driver.findElement(By.cssSelector("#text")).clear();
        driver.findElement(By.cssSelector("#text")).sendKeys("Base64");
        String base64 = driver.findElement(By.cssSelector("#text")).getScreenshotAs(OutputType.BASE64);
        saveBase64(base64);
    }

    @Test
    public void draw() throws IOException {
        driver.get("http://www.htmlcanvasstudio.com/");
        WebElement canvas = driver.findElement(By.cssSelector("#imageTemp"));

        Actions beforeBuild = action
                .clickAndHold(canvas)
                .moveByOffset(100, 100)
                .moveByOffset(-50, -10)
                .release();
        beforeBuild.perform();
        saveFile(canvas.getScreenshotAs(OutputType.FILE));
    }

    @Test
    public void move(){
        driver.get("https://ng-bootstrap.github.io/#/components/popover/examples");

        WebElement popover = driver.findElement(By.cssSelector("ngbd-popover-triggers > button"));

        action.moveToElement(popover).pause(400L).perform();

        String content = driver.findElement(By.cssSelector("ngb-popover-window div.popover-body")).getText();

        assertEquals("You see, I show up on hover!", content);
    }

    @Test
    public void execute(){
        driver.get("https://ya.ru");

        Object willBeNull = ((JavascriptExecutor) driver).executeScript(CONSOLE_LOG);
        String string = (String) ((JavascriptExecutor) driver).executeScript(RETURN_TEXT);
        Long number = (Long) ((JavascriptExecutor) driver).executeScript(RETURN_NUMBER);
        Boolean bool = (Boolean) ((JavascriptExecutor) driver).executeScript(RETURN_BOOL);
        WebElement element = (WebElement) ((JavascriptExecutor) driver).executeScript(RETURN_ELEMENT);
    }

    @Test
    public void hideElement() throws IOException {
        driver.get("https://ya.ru");
        ((JavascriptExecutor) driver).executeScript("$(\"#text\").hide();");
        saveFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
    }

    @Test
    public void showElementByOpacity() throws IOException {
        driver.get("https://ya.ru");
        ((JavascriptExecutor) driver).executeScript("$(\"#image_search\").css('opacity', '100');");
        saveFile(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE));
    }

    private void saveBase64(String data) throws IOException {
        File file = OutputType.FILE.convertFromBase64Png(data);
        saveFile(file);
    }

    private void saveBytes(byte[] data) throws IOException {
        File file = OutputType.FILE.convertFromPngBytes(data);
        saveFile(file);
    }

    private void saveFile(File data) throws IOException {
        String fileName = "build/img/" + System.currentTimeMillis() + ".png";
        FileUtils.copyFile(data, new File(fileName));
    }

    @BeforeAll
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(4L, TimeUnit.SECONDS);
        action = new Actions(driver);
    }
    @AfterAll
    public void setDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
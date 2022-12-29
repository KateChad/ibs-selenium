import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class Rgs {
    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://www.rgs.ru/");
        wait = new WebDriverWait(driver, 20, 10000);
    }

    @Test
    public void test() {

        //проверка прогрузлась ли страничка

        WebElement rgsHeader = driver.findElement(By.xpath("//*[@href='/_nuxt/899884925b8dc4a0739fbf18928f3cd4.svg#i-logotype']"));
        Assert.assertTrue("Страничка https://www.rgs.ru/ не загрузилась", rgsHeader.isDisplayed());

        //Кликаем по "Компаниям"

        WebElement company = driver.findElement(By.xpath("//a[contains(@href,'companies')]"));
        company.click();

        //Заходим во фрейм и закрываем его

        driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@id='fl-616371']")));
        driver.findElement(By.xpath("//div [@data-fl-track='click-close-login']")).click();
        driver.switchTo().defaultContent();

        //Проверка на клик по компаниям

        Assert.assertTrue("Клик по компаниям не был совершен", company.getAttribute("class").contains("active"));

        //Открываем "Здоровье"

        WebElement health = driver.findElement(By.xpath("//span[contains(text(),'Здоровье')]"));
        health.click();

        //Проверка на клик по здоровью

        WebElement parentHealth = health.findElement(By.xpath("./.."));
        Assert.assertTrue("Клик по здоровье не был совершен", parentHealth.getAttribute("class").contains("active"));

        //Выбираем "Добровольное медицинское страхование"

        WebElement insurance = driver.findElement(By.xpath("//a[contains(@href,'meditsinskoe-strakhovanie')]"));
        insurance.click();

        //Проверить наличие зоголовка insuranceHeader = Добровольное медицинское страхование

        WebElement insuranceHeader = driver.findElement(By.xpath("//h1[@class='title word-breaking title--h2']"));
        Assert.assertTrue("Страничка 'Добровольное медицинское страхование' не загрузилась", insuranceHeader.isDisplayed());
        String errorMassage = "Текст заголовка страницы не совпал \n";
        Assert.assertEquals(errorMassage, "Добровольное медицинское страхование", insuranceHeader.getText());

        //Клик по 'Отправить заявку'
        driver.findElement(By.xpath("//span[contains(text(), 'Отправить заявку')]")).click();

        //заполнение формы
        fullInputField(driver.findElement(By.xpath("//input[@name='userName']")), "Чадова Екатерина Эдуардовна");
        fullInputField(driver.findElement(By.xpath("//input[@name='userTel']")), "+79157446770");
        fullInputField(driver.findElement(By.xpath("//input[@name='userEmail']")), "qwertyqwerty");
        fullInputField(driver.findElement(By.xpath("//input[@class='vue-dadata__input']")), "г. Краснодар ул. Автомеханическа 2 кв 42");
        driver.findElement(By.xpath("//input[@type='checkbox']")).click();
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        //Поиск сообщения об ошибке
        WebElement emailError = driver.findElement(By.xpath("//span[@class = 'input__error text--small' and contains(text(),'почты')]"));
        Assert.assertTrue("Почта введена не корректно", emailError.isDisplayed());
    }

    //закрываем браузер

    @After
    public void after() {
        // driver.quit();
    }

    //скролл до элемета
    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    //проверка на кликабильность
    private void waitUntilElementToBeClicable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    //заполнение полей
    private void fullInputField(WebElement element, String value) {
        //scrollToElementJs(element);
        waitUntilElementToBeClicable(element);
        element.click();
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле не было заполнено", checkFlag);
    }
}

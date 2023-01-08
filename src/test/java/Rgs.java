import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


import static org.hamcrest.CoreMatchers.*;


public class Rgs extends BaseTests{

    @ParameterizedTest
    @MethodSource("RegistrationForm#data")
    public void test(String name, String userTel, String address) {

        WebElement company = driver.findElement(By.xpath("//a[contains(@href,'companies')]"));

        //проверка прогрузлась ли страничка
        Assertions.assertTrue(company.isDisplayed(), "Страничка https://www.rgs.ru/ не загрузилась");

        //Кликаем по "Компаниям"
        company.click();

        //Заходим во фрейм и закрываем его

        driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@id='fl-616371']")));
        driver.findElement(By.xpath("//div [@data-fl-track='click-close-login']")).click();
        driver.switchTo().defaultContent();

        //Проверка на клик по компаниям

        Assertions.assertTrue(company.getAttribute("class").contains("active"), "Клик по компаниям не был совершен");

        //Открываем "Здоровье"

        WebElement health = driver.findElement(By.xpath("//span[contains(text(),'Здоровье')]"));
        health.click();

        //Проверка на клик по здоровью

        WebElement parentHealth = health.findElement(By.xpath("./.."));
        Assertions.assertTrue(parentHealth.getAttribute("class").contains("active"), "Клик по здоровье не был совершен");

        //Выбираем "Добровольное медицинское страхование"

        WebElement insurance = driver.findElement(By.xpath("//a[contains(@href,'meditsinskoe-strakhovanie')]"));
        insurance.click();

        //Проверить наличие зоголовка insuranceHeader = Добровольное медицинское страхование

        WebElement insuranceHeader = driver.findElement(By.xpath("//h1[@class='title word-breaking title--h2']"));
        Assertions.assertTrue(insuranceHeader.isDisplayed(), "Страничка 'Добровольное медицинское страхование' не загрузилась");
        MatcherAssert.assertThat("Текст заголовка страницы не совпал \n",  insuranceHeader.getText(),allOf(containsString("медицинское"),
                endsWith("страхование"), startsWith("Добровольное")));


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //заполнение формы
        fullInputField(driver.findElement(By.xpath("//input[@name='userName']")), name);
        fullInputPhone(driver.findElement(By.xpath("//input[@name='userTel']")), userTel);
        equalsPhone(driver.findElement(By.xpath("//input[@name='userTel']")), userTel);
        fullInputField(driver.findElement(By.xpath("//input[@name='userEmail']")), "qwertyqwerty");
        fullInputField(driver.findElement(By.xpath("//input[@class='vue-dadata__input']")), address);
        scrollWithOffset(driver.findElement(By.xpath("//input[@type='checkbox']")), 0, 250);
        driver.findElement(By.xpath("//input[@type='checkbox']/..")).click();
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        //Поиск сообщения об ошибке
        WebElement emailError = driver.findElement(By.xpath("//input[@name='userEmail']/../../span[contains(@class, 'error')]"));
        Assertions.assertTrue(emailError.isDisplayed(), "Отсутствует сообщение об ошибке");
        MatcherAssert.assertThat("Текст ошибки не совпал \n",  emailError.getText(), containsString("Введите корректный адрес электронной почты"));
    }

    //заполнение поля телефона
    private void fullInputPhone(WebElement element, String value) {
        scrollWithOffset(element, 0, -250);
        waitUntilElementToBeVisibility(element);
        waitUntilElementToBeClicable(element);
        Actions actions = new Actions(driver);
        actions.pause(1000).moveToElement(element).pause(250).click(element).pause(250).sendKeys(value).build().perform();
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

    //проверка на видимость
    private void waitUntilElementToBeVisibility(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    //заполнение полей
    private void fullInputField(WebElement element, String value) {
        waitUntilElementToBeClicable(element);
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assertions.assertTrue(checkFlag, "Поле не было заполнено");
    }

    public WebElement scrollWithOffset(WebElement element, int x, int y) {
        String code = "window.scroll(" + (element.getLocation().x + x) + ","
                + (element.getLocation().y + y) + ");";
        ((JavascriptExecutor) driver).executeScript(code, element, x, y);
        return element;
    }

    public void equalsPhone(WebElement element, String s) {
        String phone = "+7 " + s;
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", phone));
        Assertions.assertTrue(checkFlag, "Поле не было заполнено");
    }
}

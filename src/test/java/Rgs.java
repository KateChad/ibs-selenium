import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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


import static org.hamcrest.CoreMatchers.*;
@RunWith(Parameterized.class)

public class Rgs extends BaseTests{

    @Parameterized.Parameters
    public static Iterable<Object[]> data(){
        return Arrays.asList(new Object[][]{
            {"Чадова Екатерина Эдуардовна", "(915) 744-6770", "г. Краснодар ул. Автомеханическа 2 кв 42"},
            {"Маслова Анна Николаевна", "(955) 698-7463", "г. Архангельск ул. Красная 45 кв 5"},
            {"Кривоульский Николай Инокеньтьевич", "(906) 581-3354", "г. Москва ул. Зеленая 14 кв 158"}
        });
    }

    @Parameterized.Parameter(value = 0)
    public String name;

    @Parameterized.Parameter(1)
    public String userTel;

    @Parameterized.Parameter(2)
    public String address;
    @Test
    public void test() {

        //проверка прогрузлась ли страничка (сменить хпаф)

        WebElement rgsHeader = driver.findElement(By.xpath("//*[@href='/_nuxt/899884925b8dc4a0739fbf18928f3cd4.svg#i-logotype']"));
        Assert.assertTrue("Страничка https://www.rgs.ru/ не загрузилась", rgsHeader.isDisplayed());

        //Кликаем по "Компаниям"

        WebElement company = driver.findElement(By.xpath("//a[contains(@href,'companies')]"));
        company.click();

        //Заходим во фрейм и закрываем его(сменить хпаф)

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
        MatcherAssert.assertThat("Текст заголовка страницы не совпал \n",  insuranceHeader.getText(),allOf(containsString("медицинское"),
                endsWith("страхование"), startsWith("Добровольное")));


        try {
            Thread.sleep(5000);
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
        Assert.assertTrue("Отсутствует сообщение об ошибке", emailError.isDisplayed());
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
        Assert.assertTrue("Поле не было заполнено", checkFlag);
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
        Assert.assertTrue("Поле не было заполнено", checkFlag);
    }
}

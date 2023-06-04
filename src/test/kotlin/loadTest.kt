import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

fun main() {
    val driver: WebDriver = ChromeDriver()
    var count = 0
    while (true) {
        println("Iteration ${++count}")
        driver.get("http://localhost:8080")
        Thread.sleep(200)
        // Click on a element with href "/about"
        driver.findElements(By.cssSelector("a[href='/about']")).first().click()
        Thread.sleep(200)
        // Click on a element with href "/dev"
        driver.findElements(By.cssSelector("a[href='/dev']")).first().click()
        Thread.sleep(200)
        // Click on a element with href "/join"
        driver.findElements(By.cssSelector("a[href='/join']")).first().click()
        Thread.sleep(200)
        // Click on a element with href "/faq"
        driver.findElements(By.cssSelector("a[href='/faq']")).first().click()
        Thread.sleep(200)
    }
}
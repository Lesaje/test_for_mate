package task2

import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import scala.jdk.CollectionConverters._

import java.time.Duration

object Scraper {

  def setupDriver(): WebDriver = {
    val options = new ChromeOptions()
    options.addArguments("--headless=new")
    options.addArguments("--no-sandbox")
    options.addArguments("--disable-dev-shm-usage")
    options.addArguments("--remote-allow-origins=*")
    new ChromeDriver(options)
  }

  def waitForElementPresence(driver: WebDriver, by: By, timeoutSeconds: Int = 10): WebElement = {
    val wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
    wait.until(ExpectedConditions.presenceOfElementLocated(by))
  }

  def getCourseElements(driver: WebDriver): List[WebElement] = {
    waitForElementPresence(driver, By.cssSelector("a.ProfessionCard_cardWrapper__BCg0O"))
    driver.findElements(By.cssSelector("a.ProfessionCard_cardWrapper__BCg0O")).asScala.toList
  }

  def extractCourseName(courseElement: WebElement): String = {
    courseElement.findElement(By.cssSelector("h3.ProfessionCard_title__m7uno")).getText.trim
  }

  def extractShortDescription(courseElement: WebElement): String = {
    courseElement.findElement(By.cssSelector("p.ProfessionCard_description__K8weo")).getText.trim
  }
}
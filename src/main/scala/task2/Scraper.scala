package task2

import org.openqa.selenium.{By, JavascriptExecutor, WebDriver, WebElement}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import scala.jdk.CollectionConverters.*
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

  def scrollIntoView(driver: WebDriver, element: WebElement): Unit = {
    driver.asInstanceOf[JavascriptExecutor].executeScript("arguments[0].scrollIntoView(true);", element)
  }

  def getCourseElements(driver: WebDriver): List[WebElement] = {
    waitForElementPresence(driver, By.cssSelector("a.ProfessionCard_cardWrapper__BCg0O"))
    driver.findElements(By.cssSelector("a.ProfessionCard_cardWrapper__BCg0O")).asScala.toList
  }

  def extractCourseName(driver: WebDriver, courseElement: WebElement): String = {
    scrollIntoView(driver, courseElement)
    val titleElement = courseElement.findElement(By.cssSelector("h3.ProfessionCard_title__m7uno"))
    titleElement.getAttribute("textContent").trim
  }

  def extractShortDescription(driver: WebDriver, courseElement: WebElement): String = {
    scrollIntoView(driver, courseElement)
    val descElement = courseElement.findElement(By.cssSelector("p.ProfessionCard_description__K8weo"))
    descElement.getAttribute("textContent").trim
  }

  def extractDuration(driver: WebDriver, courseElement: WebElement): String = {
    scrollIntoView(driver, courseElement)
    val durationElement = courseElement.findElement(By.cssSelector("p.ProfessionCard_duration__13PwX"))
    durationElement.getAttribute("textContent").trim
  }

  def scrapeCourses(): Unit = {
    val driver = setupDriver()

    try {
      driver.get("https://mate.academy/")

      val courseElements = getCourseElements(driver)
      println(s"Found ${courseElements.size} course cards.")

      courseElements.zipWithIndex.foreach { case (element, idx) =>
        val name = extractCourseName(driver, element)
        val shortDescription = extractShortDescription(driver, element)
        val duration = extractDuration(driver, element)
        println(s"Course #${idx + 1}: $name")
        println(s"Duration: $duration")
        println(s"Short Description: $shortDescription")
        println("--------------")
      }

    } finally {
      driver.quit()
    }
  }
}
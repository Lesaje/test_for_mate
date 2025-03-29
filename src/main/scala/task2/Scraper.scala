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

  def extractCourseName(courseElement: WebElement): String = {
    courseElement.findElement(By.cssSelector("h3.ProfessionCard_title__m7uno")).getAttribute("textContent").trim
  }

  def extractShortDescription(courseElement: WebElement): String = {
    courseElement.findElement(By.cssSelector("p.ProfessionCard_description__K8weo")).getAttribute("textContent").trim
  }

  def openCoursePage(driver: WebDriver, courseElement: WebElement): Unit = {
    scrollIntoView(driver, courseElement)
    val jsExecutor = driver.asInstanceOf[JavascriptExecutor]
    jsExecutor.executeScript("arguments[0].click();", courseElement)
    waitForElementPresence(driver, By.cssSelector("div.TableColumnsView_doubleColumnsRowWithButtons__kiM_7"))
  }

  def extractCourseFormats(driver: WebDriver): Set[CourseFormat] = {
    val wait = new WebDriverWait(driver, Duration.ofSeconds(10))
    val allContainers = wait
      .until(ExpectedConditions.presenceOfAllElementsLocatedBy(
        By.cssSelector("div.TableColumnsView_doubleColumnsRowWithButtons__kiM_7")
      )).asScala.toList

    val buttonContainer = allContainers.last
    val buttons = buttonContainer.findElements(By.tagName("a")).asScala.toList

    val formats = scala.collection.mutable.Set[CourseFormat]()
    if (buttons.exists(_.getAttribute("textContent").contains("Навчатися повний день"))) formats += FullTime
    if (buttons.exists(_.getAttribute("textContent").contains("Навчатися у вільний час"))) formats += Flex
    formats.toSet
  }

  def extractDetailedDurations(driver: WebDriver): (String, String) = {
    val durationRows = driver.findElements(By.cssSelector("div.TableColumnsView_rowBase__4C9kf")).asScala

    val durationRow = durationRows.find(_.getAttribute("textContent").contains("Тривалість")).get
    val cells = durationRow.findElements(By.cssSelector("div.TableColumnsView_tableCellGray__4hadg")).asScala

    val fullTimeDuration = if (cells.nonEmpty) cells.head.getAttribute("textContent").trim else "Not available"
    val flexDuration = if (cells.size >= 2) cells(1).getAttribute("textContent").trim else "Not available"

    (fullTimeDuration, flexDuration)
  }

  def extractModules(driver: WebDriver): List[Module] = {
    val wait = new WebDriverWait(driver, Duration.ofSeconds(10))
    val modules = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
      By.cssSelector("ul.CourseModulesList_modulesList__C86yL > li.color-dark-blue")
    )).asScala.toList

    modules.map { moduleEl =>
      val moduleName = moduleEl
        .findElement(By.cssSelector("p.CourseModulesList_topicName__7vxtk"))
        .getAttribute("textContent").trim

      val topicElements = moduleEl.findElements(By.cssSelector(
        "ul.CourseModulesList_topicsList__NJTKz > li.CourseModulesList_topicItem__8wNTG"
      )).asScala

      val topics = topicElements
        .map(_.getAttribute("textContent").trim)
        .filter(_.nonEmpty)
        .toList

      Module(moduleName, topics)
    }
  }

  def scrapeAllCourses(): List[Course] = {
    val driver = setupDriver()

    try {
      driver.get("https://mate.academy/")
      var courseElements = getCourseElements(driver)
      
      courseElements.indices.map { idx =>
        courseElements = getCourseElements(driver)
        val element = courseElements(idx)

        scrollIntoView(driver, element)

        val name = extractCourseName(element)
        val shortDescription = extractShortDescription(element)

        openCoursePage(driver, element)

        val formats = extractCourseFormats(driver)
        val (fullTimeDuration, flexDuration) = extractDetailedDurations(driver)
        val modules = extractModules(driver)

        driver.navigate().back()
        waitForElementPresence(driver, By.cssSelector("a.ProfessionCard_cardWrapper__BCg0O"))

        Course(
          name = name,
          shortDescription = shortDescription,
          fullTimeDuration = fullTimeDuration,
          flexDuration = flexDuration,
          availableFormats = formats,
          modules = modules
        )
      }.toList

    } finally {
      driver.quit()
    }
  }
}

import task2.{Course, Scraper}
import task2.Scraper.{extractCourseName, extractShortDescription, getCourseElements, setupDriver}

@main
def main(): Unit = {
  val driver = setupDriver()

  try {
    driver.get("https://mate.academy/")

    val courseElements = getCourseElements(driver)

    val coursesInfo: List[Course] = courseElements.map { courseElem =>
      val name = extractCourseName(courseElem)
      val description = extractShortDescription(courseElem)
      Course(name, description)
    }

    coursesInfo.foreach { course =>
      println(s"Course Name: ${course.name}")
      println(s"Short Description: ${course.shortDescription}")
      println("--------------")
    }
  } finally {
    driver.quit()
  }
}
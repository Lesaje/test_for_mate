import task2.Scraper

@main
def main(): Unit = {
  val courses = Scraper.scrapeAllCourses()
  println(courses(0))
}

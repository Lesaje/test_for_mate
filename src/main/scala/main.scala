import task2.{Course, Scraper}

def prettyPrintCourse(c: Course): Unit = {
  println(s"Course Name: ${c.name}")
  println(s"Short Description: ${c.shortDescription}")
  println(s"Available Formats: ${c.availableFormats.map(_.toString).mkString(", ")}")
  println(s"Full-time Duration: ${c.fullTimeDuration}")
  println(s"Flex Duration: ${c.flexDuration}")
  println(s"Number of Modules: ${c.modules.size}")
  println(s"Total Topics: ${c.modules.map(_.topics.size).sum}")
  println("\nModules:")
  c.modules.zipWithIndex.foreach { case (module, idx) =>
    println(f"  ${idx + 1}%2d. ${module.name}")
    module.topics.zipWithIndex.foreach { case (topic, tidx) =>
      println(f"      - ${topic}")
    }
  }
  println("\n" + ("=" * 50) + "\n")
}

@main
def main(): Unit = {
  val courses = Scraper.scrapeAllCourses()
  courses.foreach(c => println(prettyPrintCourse(c)))
}

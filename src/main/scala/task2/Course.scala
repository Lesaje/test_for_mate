package task2

sealed trait CourseFormat

case object FullTime extends CourseFormat

case object Flex extends CourseFormat

case class Module(name: String, topics: List[String])

case class Course(
                   name: String,
                   shortDescription: String,
                   fullTimeDuration: String,
                   flexDuration: String,
                   availableFormats: Set[CourseFormat],
                   modules: List[Module]
                 )



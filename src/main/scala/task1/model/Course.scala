package task1.model

import doobie.Meta

case class Course(id: Int,
                  slug: CourseSlug,
                  courseType: CourseType,
                  languageId: Int,
                  sort: Int)

enum CourseSlug:
  case PythonBasics, Frontend, Devops

object CourseSlug {
  def fromString(s: String): Option[CourseSlug] = s.toLowerCase match
    case "python_basics" => Some(PythonBasics)
    case "frontend"      => Some(Frontend)
    case "devops"        => Some(Devops)
    case _               => None

  given Meta[CourseSlug] = Meta[String].timap[CourseSlug](
    s => fromString(s).getOrElse(throw new IllegalArgumentException(s"Invalid CourseSlug: $s")))(_.toString)
}

enum CourseType:
  case MODULE, FULL_TIME, FLEX

object CourseType {
  def fromString(s: String): Option[CourseType] = s.toUpperCase match
    case "MODULE" => Some(MODULE)
    case "FULL_TIME" => Some(FULL_TIME)
    case "FLEX" => Some(FLEX)
    case _ => None

  given Meta[CourseType] = Meta[String].timap[CourseType](
    s => fromString(s).getOrElse(throw new IllegalArgumentException(s"Invalid CourseType: $s")))(_.toString)
}


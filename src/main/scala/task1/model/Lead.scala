package task1.model

import doobie.Meta

import java.time.OffsetDateTime

case class Lead(id: Int,
                userId: Int,
                courseId: Int,
                createdAt: OffsetDateTime,
                updatedAt: OffsetDateTime,
                status: Status,
                lostReason: Option[LostReason])

enum Status:
  case LOST, WON, NEW

object Status {
  def fromString(s: String) : Option[Status] =  Option(s).flatMap(_.toUpperCase match
    case "NEW" => Some(NEW)
    case "WON" => Some(WON)
    case "LOST" => Some(LOST)
    case _ => None)

  def toStringValue(s: Status): String = s match
    case LOST => "LOST"
    case WON => "WON"
    case NEW => "NEW"
}

enum LostReason:
  case NO_CONTACT

object LostReason {
  def fromString(s: String) : Option[LostReason] = Option(s).flatMap(_.toUpperCase match
    case "NO_CONTACT" => Some(NO_CONTACT)
    case _ => None)

  def toStringValue(s: LostReason): String = s match
    case NO_CONTACT => "NO_CONTACT"

  given Meta[LostReason] = Meta[String].timap[LostReason](
    s => fromString(s).getOrElse(throw new IllegalArgumentException(s"Invalid LostReason: $s"))
  )(toStringValue)
}

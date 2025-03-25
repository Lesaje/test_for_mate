package task1.model

import doobie.Meta

import java.time.OffsetDateTime

case class Domain(id: Int,
                  country: Country,
                  createdAt: OffsetDateTime,
                  updatedAt: OffsetDateTime,
                  active: Boolean):
  def slug: String = country.code


enum Country(val code: String, val fullName: String):
  case Ukraine extends Country("ua", "Ukraine")
  case Poland extends Country ("pl", "Poland")
//etc.

object Country {
  def fromCode(c: String): Option[Country] = Country.values.find(_.code == c.toLowerCase())
  
  given Meta[Country] =
    Meta[String].timap(code => fromCode(code).getOrElse(
        throw new IllegalArgumentException(s"Invalid Country Code: $code")))
      (_.code)
}

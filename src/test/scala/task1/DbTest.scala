package task1

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite
import doobie.*
import doobie.implicits.*
import cats.effect.unsafe.implicits.global
import db.{DbConnector, H2DbConnector, Queries}

import java.time.OffsetDateTime

class DbTest extends AnyFunSuite {

  val dbConnector: DbConnector[IO] = H2DbConnector[IO]()
  val transactor: Transactor[IO] = dbConnector.transactor

  def initSchema = {
    val createUsers =
      sql"""
        CREATE TABLE users (
          id INT PRIMARY KEY,
          email VARCHAR(255),
          first_name VARCHAR(255),
          last_name VARCHAR(255),
          phone VARCHAR(50),
          domain_id INT,
          language_id INT
        )
      """.update.run

    val createLeads =
      sql"""
        CREATE TABLE leads (
          id INT PRIMARY KEY,
          user_id INT,
          course_id INT,
          created_at TIMESTAMP,
          updated_at TIMESTAMP,
          status VARCHAR(50),
          lost_reason VARCHAR(255)
        )
      """.update.run

    val createDomains =
      sql"""
        CREATE TABLE domains (
          id INT PRIMARY KEY,
          slug VARCHAR(50),
          country_name VARCHAR(255),
          created_at TIMESTAMP,
          updated_at TIMESTAMP,
          active BOOLEAN
        )
      """.update.run

    val createCourses =
      sql"""
        CREATE TABLE courses (
          id INT PRIMARY KEY,
          slug VARCHAR(255),
          type VARCHAR(50),
          language_id INT,
          sort INT
        )
      """.update.run

    (for {
      _ <- createUsers
      _ <- createLeads
      _ <- createDomains
      _ <- createCourses
    } yield ()).transact(transactor).unsafeRunSync()
  }

  def insertData = {
    val insertUsers =
      sql"""
        INSERT INTO users (id, email, first_name, last_name, phone, domain_id, language_id)
        VALUES
        (35, 'jsmith@example.com', 'John', 'Smith', '(123) 456-7890', 1, 1),
        (47, 'ldoe@example.com', 'Laura', 'Doe', '(987) 654-3210', 1, 1),
        (51, 'mbrown@example.com', 'Michael', 'Brown', '(555) 123-4567', 4, 5)
      """.update.run

    val insertDomains =
      sql"""
        INSERT INTO domains (id, slug, country_name, created_at, updated_at, active)
        VALUES
        (1, 'ua', 'Ukraine', TIMESTAMP '2023-07-27 09:31:22.147845+00', TIMESTAMP '2024-02-26 10:21:53.046+00', TRUE),
        (3, 'pl', 'Poland', TIMESTAMP '2023-12-21 09:14:32.8806+00', TIMESTAMP '2024-02-15 11:24:51.941+00', FALSE)
      """.update.run

    val insertCourses =
      sql"""
        INSERT INTO courses (id, slug, type, language_id, sort)
        VALUES
        (12, 'python_basics', 'MODULE', 1, 3),
        (25, 'frontend', 'FULL_TIME', 1, 5),
        (27, 'devops', 'FLEX', 1, 1)
      """.update.run

    val insertLeads =
      sql"""
        INSERT INTO leads (id, user_id, course_id, created_at, updated_at, status, lost_reason)
        VALUES
        (10, 35, 25, TIMESTAMP '2024-01-14 11:17:29.664+00', TIMESTAMP '2024-02-26 17:28:13.647+00', 'LOST', 'NO_CONTACT'),
        (16, 35, 38, TIMESTAMP '2024-01-13 18:42:38.671+00', TIMESTAMP '2024-01-30 12:01:44.473+00', 'WON', NULL),
        (45, 62, 27, TIMESTAMP '2024-01-12 16:49:15.082+00', TIMESTAMP '2024-02-13 09:13:07.151+00', 'NEW', NULL),
        (50, 47, 27, TIMESTAMP '2024-02-05 12:00:00.082+00', TIMESTAMP '2024-02-05 12:00:00.151+00', 'WON', NULL),
        (46, 51, 27, TIMESTAMP '2024-07-15 10:00:00.082+00', TIMESTAMP '2024-07-15 10:00:00.151+00', 'LOST', 'NO_CONTACT'),
        (60, 35, 25, TIMESTAMP '2024-02-20 10:00:00.082+00', TIMESTAMP '2024-02-20 10:00:00.151+00', 'NEW', NULL)
      """.update.run

    (for {
      _ <- insertUsers
      _ <- insertDomains
      _ <- insertCourses
      _ <- insertLeads
    } yield ()).transact(transactor).unsafeRunSync()
  }

  test("Test queries") {
    initSchema
    insertData

    // 1.1. Leads per week grouped by course type.
    val result1 = Queries.leadsPerWeekGroupedByCourse.to[List].transact(transactor).unsafeRunSync()
    println("Query 1.1 Results (Leads per week grouped by course type):")
    result1.foreach { case (week, courseType, count) =>
      println(s"Week Start: $week, Course Type: $courseType, Count: $count")
    }

    // 1.2. WON flex leads per country created from 01.01.2024.
    val result2 = Queries.wonFlexLeadsPerCountry.to[List].transact(transactor).unsafeRunSync()
    println("\nQuery 1.2 Results (WON flex leads per country from 01.01.2024):")
    result2.foreach { case (country, count) =>
      println(s"Country: $country, WON FLEX Leads: $count")
    }

    // 1.3. User email, lead id and lost reason for lost flex leads from 01.07.2024.
    val result3 = Queries.lostFlexLeads.to[List].transact(transactor).unsafeRunSync()
    println("\nQuery 1.3 Results (Lost flex leads from 01.07.2024):")
    result3.foreach { case (email, leadId, lostReason) =>
      println(s"Email: $email, Lead ID: $leadId, Lost Reason: ${lostReason.getOrElse("N/A")}")
    }



    // Query 1.1
    assert(result1.nonEmpty, "There should be some grouped leads")
    assert(result1.size == 5, "Expected five groups for leads per week and course type")

    // Query 1.2
    assert(result2.nonEmpty, "There should be some WON flex leads")
    assert(result2.size == 1, "Expected exactly one WON flex lead from Ukraine")
    val (country, count) = result2.head
    assert(country == "Ukraine" && count == 1, s"Expected country 'Ukraine' with 1 WON flex lead, but got $country with $count")

    // Query 1.3
    assert(result3.nonEmpty, "There should be some lost flex leads")
    assert(result3.size == 1, "Expected exactly one lost flex lead from 01.07.2024")
    val (email, leadId, lostReason) = result3.head
    assert(email == "mbrown@example.com", s"Expected email 'mbrown@example.com' but got $email")
    assert(leadId == 46, s"Expected lead id 46 but got $leadId")
    assert(lostReason.contains("NO_CONTACT"), s"Expected lost reason 'NO_CONTACT' but got $lostReason")

  }
}

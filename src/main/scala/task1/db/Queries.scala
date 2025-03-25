package task1.db

import doobie.implicits.toSqlInterpolator
import task1.model.CourseType

object Queries {

  /**
   * 1.1. The number of created leads per week grouped by course type.
   *
   * This query calculates the week start (Monday) for each lead’s creation date,
   * then groups by that week (formatted as a string) and by course type, and finally counts the leads.
   */
  def leadsPerWeekGroupedByCourse =
    sql"""
      SELECT
          date_trunc('week', l.created_at) AS week,
          c.type AS course_type,
          COUNT(*) AS leads_count
      FROM
          leads l
      JOIN
          courses c ON l.course_id = c.id
      GROUP BY
          week, c.type
      ORDER BY
          week, course_type;
    """.query[(String, String, Long)]

  /**
   * 1.2. The number of WON flex leads per country created from 01.01.2024.
   *
   * Explanation of each part:
   * - FROM leads l: starting from the leads table.
   * - JOIN courses c: join on courses to obtain the course type.
   * - JOIN users u: join on users to get the associated domain.
   * - JOIN domains d: join on domains to get the country information.
   * - WHERE conditions:
   *     * l.status = 'WON' ensures only won leads are considered.
   *     * c.courseType = 'FLEX' ensures that only flex courses are included.
   *     * l.created_at >= TIMESTAMP '2024-01-01 00:00:00' limits the results to leads created from January 1, 2024.
   * - GROUP BY d.country_name groups the result by each country.
   * - COUNT(*) calculates the number of leads per country.
   */
  def wonFlexLeadsPerCountry =
    sql"""
      SELECT
          d.country_name,
          COUNT(*) AS won_flex_leads_count
      FROM
          leads l
      JOIN
          courses c ON l.course_id = c.id
      JOIN
          users u ON l.user_id = u.id
      JOIN
          domains d ON u.domain_id = d.id
      WHERE
          l.status = 'WON'
          AND c.type = 'FLEX'
          AND l.created_at >= '2024-01-01'
      GROUP BY
          d.country_name;
    """.query[(String, Long)]

  /**
   * 1.3. User email, lead id and lost reason for users who have lost flex leads from 01.07.2024.
   *
   * Explanation:
   * - Selects the user's email, the lead id, and the lost_reason from leads.
   * - Joins courses and users to ensure we filter only flex leads.
   * - WHERE conditions:
   *     * l.status = 'LOST' selects only lost leads.
   *     * c.courseType = 'FLEX' ensures the course is of FLEX type.
   *     * l.created_at >= TIMESTAMP '2024-07-01 00:00:00' limits to leads created on or after July 1, 2024.
   */
  def lostFlexLeads =
    sql"""
      SELECT
          u.email,
          l.id AS lead_id,
          l.lost_reason
      FROM
          leads l
      JOIN
          courses c ON l.course_id = c.id
      JOIN
          users u ON l.user_id = u.id
      WHERE
          l.status = 'LOST'
          AND c.type = 'FLEX'
          AND l.created_at >= '2024-07-01';
    """.query[(String, Int, Option[String])]
}
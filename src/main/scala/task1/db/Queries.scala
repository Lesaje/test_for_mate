package task1.db

import doobie.implicits.toSqlInterpolator
import task1.model.CourseType

object Queries {

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
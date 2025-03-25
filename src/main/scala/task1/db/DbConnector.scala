package task1.db

import cats.effect.{Async, IO}
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

trait DbConnector[F[_]] {
  def transactor: Transactor[F]
}

class H2DbConnector[F[_]: Async] extends DbConnector[F] {
  override def transactor: Transactor[F] =
    Transactor.fromDriverManager[F](
      "org.h2.Driver",
      "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
      "sa",
      "",
      logHandler = Some(LogHandler.jdkLogHandler)
    )
}

object H2DbConnector {
  def apply[F[_]: Async](): DbConnector[F] = new H2DbConnector[F]
}

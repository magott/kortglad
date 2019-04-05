package kortglad.resources

import cats.effect.{IO, Sync}
import cats.implicits._
import kortglad.scraper.RefereeScraper
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl._
import org.http4s.circe._


/**
  *
  */
class RefereeResource(scraper:RefereeScraper) extends Http4sDsl[IO] {

  val service = HttpRoutes.of[IO] {
    case request @ GET -> Root =>
      Ok("foooo")
    case request @ GET -> Root / IntVar(fiksId) =>
      Ok(scraper.findRefereeStats(fiksId).map(_.asJson))
  }


}

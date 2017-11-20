package kortglad.resources
import kortglad.scraper.RefereeScraper
import kortglad.services.RefereeServices
import org.http4s._
import org.http4s.dsl._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._


/**
  *
  */
object RefereeResource {

  val service = HttpService {
    case request @ GET -> Root => {
      Ok("foooo")
    }
    case request @ GET -> Root / IntVar(fiksId) => {
      val refStats = new RefereeServices().findRefereeStats(fiksId)
      Ok(refStats.asJson)
    }
  }

  import org.http4s.server.middleware._
  val corsVersion = CORS(service)

}

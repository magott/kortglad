package kortglad.server

import java.util.concurrent.Executors

import cats.data.{Kleisli, OptionT}
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import kortglad.resources.{RefereeResource, StaticResources}
import kortglad.scraper.RefereeScraper
import org.http4s.Status.Redirection
import org.http4s.{Header, HttpRoutes, Request, Status, Uri}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.headers.Location
import org.http4s.server.Router
import org.http4s.implicits._

import scala.concurrent.ExecutionContext
import org.http4s.server.blaze._
import org.http4s.server.middleware._
import org.http4s.util.CaseInsensitiveString

import scala.util.Properties

object Main extends IOApp {

  def routes(bec: ExecutionContext, client: Client[IO]) = {
    Router(
      "/" -> new StaticResources[IO](bec).service,
      "/referee" -> CORS(
        new RefereeResource(new RefereeScraper(client)).service
      )
    )
  }

  val port = Properties.envOrElse("PORT", "8080").toInt
  val resource = for {
    bec <- Resource
      .make(IO(Executors.newCachedThreadPool()))(e => IO(e.shutdown()))
      .map(ExecutionContext.fromExecutor)
    client <- BlazeClientBuilder[IO](ExecutionContext.global).resource
    r <- BlazeServerBuilder[IO]
      .bindHttp(port, "0.0.0.0")
      .withHttpApp(routes(bec, client).orNotFound)
      .resource
  } yield r

  def run(args: List[String]): IO[ExitCode] =
    resource.use(_ => IO.never).as(ExitCode.Success)

//  def httpsRedirect(service: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { req: Request[IO] =>
//    val redirectToHttps = req.headers.get(CaseInsensitiveString("X-Forwarded-Proto")).filter(_.value == "http").isDefined
//    if(redirectToHttps){
//      Kleisli(_ => OptionT.liftF(
//        Status.Redirection(Location(Uri.unsafeFromString(s"https://${req.uri.host}${req.uri.path}")))
//      )
//      )
//    } else {
//      service(req)
//    }
//  }
}

package kortglad.server

import java.util.concurrent.Executors

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import kortglad.resources.{RefereeResource, StaticResources}
import kortglad.scraper.RefereeScraper
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.Router
import org.http4s.implicits._

import scala.concurrent.ExecutionContext
import org.http4s.server.blaze._
import org.http4s.server.middleware._

object Main extends IOApp {

  def routes(bec:ExecutionContext, client:Client[IO]) = {
    Router(
      "/" -> new StaticResources[IO](bec).service,
      "/referee" -> CORS(new RefereeResource(new RefereeScraper(client)).service))
  }


  val resource = for {
    bec <- Resource.make(IO(Executors.newCachedThreadPool()))(e => IO(e.shutdown())).map(ExecutionContext.fromExecutor)
    client <- BlazeClientBuilder[IO](ExecutionContext.global).resource
    r <- BlazeServerBuilder[IO].bindHttp(8080, "0.0.0.0").withHttpApp(routes(bec, client).orNotFound).resource
  } yield r

  def run(args: List[String]): IO[ExitCode] =
    resource.use(_ => IO.never).as(ExitCode.Success)
}

package kortglad.server

import fs2.{Strategy, Stream, Task}
import kortglad.resources.{RefereeResource, StaticResources}
import org.http4s.server.ServerBuilder

import scala.concurrent.ExecutionContext
// import fs2.{Stream, Task}

import org.http4s.server.blaze._
// import org.http4s.server.blaze._

import org.http4s.util.StreamApp
// import org.http4s.util.StreamApp

object Main extends StreamApp {

  implicit val S = Strategy.fromExecutionContext(ExecutionContext.global)

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(8080, "0.0.0.0")
      .mountService(RefereeResource.corsVersion, "/referee")
        .mountService(StaticResources.service, "/")
//      .mountService(services, "/api")
      .serve
  }
}

package kortglad.resources

import java.io.File

import cats.effect.{ContextShift, IO, Sync}
import org.http4s.dsl._
import org.http4s._

import scala.concurrent.ExecutionContext
/**
  *
  */
class StaticResources[F[_] : Sync : ContextShift](bec:ExecutionContext) extends Http4sDsl[F] {

//  val service = HttpService {
//    case request @ GET -> Root / "index.html" =>
//      StaticFile.fromFile(new File("static/index.html"), Some(request))
//        .getOrElseF(NotFound())
//    case request @ GET -> Root / "kortglad.js" =>
//      StaticFile.fromFile(new File("static/kortglad.js"), Some(request))
//        .getOrElseF(NotFound())
//  }

  def static(file: String, request: Request[F]) =
    StaticFile.fromResource[F]("/static/" + file, bec, Some(request)).getOrElseF(NotFound())

  val service = HttpRoutes.of[F] {
    case request @ GET -> Root => static("index.html", request)
    case request @ GET -> Root / path if List(".js", ".css", ".map", ".html", ".webm").exists(path.endsWith) =>
      static(path, request)
  }

}

package kortglad.resources

import java.io.File

import org.http4s.dsl._
import org.http4s._
import fs2.Task
import fs2.interop.cats._
/**
  *
  */
object StaticResources {

//  val service = HttpService {
//    case request @ GET -> Root / "index.html" =>
//      StaticFile.fromFile(new File("static/index.html"), Some(request))
//        .getOrElseF(NotFound())
//    case request @ GET -> Root / "kortglad.js" =>
//      StaticFile.fromFile(new File("static/kortglad.js"), Some(request))
//        .getOrElseF(NotFound())
//  }

  def static(file: String, request: Request) =
    StaticFile.fromResource("/static/" + file, Some(request)).getOrElseF(NotFound())

  val service = HttpService {
    case request @ GET -> Root / path if List(".js", ".css", ".map", ".html", ".webm").exists(path.endsWith) =>
      static(path, request)
  }

}

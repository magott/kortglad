name:="kortglad"

scalaVersion:="2.12.4"

scalacOptions += "-target:jvm-1.8"

organization := "com.andersen-gott"

enablePlugins(JavaAppPackaging)

//enablePlugins(LinuxPlugin)


val http4sVersion = "0.17.5"
val circeVersion = "0.8.0"

libraryDependencies ++=
  Seq(
    "org.jsoup"   % "jsoup"               % "1.6.1",
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion

  )

libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
).map(_ % circeVersion)

name:="kortglad"

scalaVersion:="2.12.8"

scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-Ypartial-unification")

organization := "com.andersen-gott"

enablePlugins(JavaAppPackaging)

//enablePlugins(LinuxPlugin)


val http4sVersion = "0.20.0-M7"
val circeVersion = "0.11.1"

libraryDependencies ++=
  Seq(
    "org.jsoup"   % "jsoup"               % "1.6.1",
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion,
    "io.circe"   %% "circe-generic"       % circeVersion

  )
// shared settings across root & all subprojects

version in ThisBuild := {
  val major = 0
  val minor = 0
  val patch = 1
  s"$major.$minor.$patch"
}

scalaVersion in ThisBuild := "2.11.8"

javacOptions += "-Xlint:unchecked"

organization := "edu.psu.sagnik.research"

name := "pdffigurestosvg"

lazy val root = project
  .in(file("."))
  .aggregate(
    data,
    reader,
    writer,
    inkscapesvgs,
    pdfs
 )
  .settings(publishArtifact := false)

lazy val data = project
  .in(file("data"))

lazy val reader = project
  .in(file("reader"))
  .dependsOn(data)

lazy val writer = project
  .in(file("writer"))
  .dependsOn(data)

lazy val inkscapesvgs = project
  .in(file("inkscapesvgs"))
  .dependsOn(reader)
  .dependsOn(data)
  .dependsOn(writer)

lazy val pdfs = project
  .in(file("pdfs"))
  .dependsOn(reader)
  .dependsOn(data)

lazy val subprojects: Seq[ProjectReference] = root.aggregate


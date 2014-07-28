name := """demoRest"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

doc in Compile <<= target.map(_ / "none")

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  //anorm,
  cache,
  ws,
  "org.xerial" % "sqlite-jdbc" % "3.7.15-M1",
  "com.typesafe.slick" %% "slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick" % "0.7.0"
)

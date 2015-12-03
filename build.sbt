name := "ckit"

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq (
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" %  "bootstrap"    % "3.3.6",
  "org.webjars" %  "angularjs"    % "1.4.8",
  "org.webjars" %  "requirejs"    % "2.1.22"
)

organization in ThisBuild := "com.github.wookietreiber"

scalaVersion in ThisBuild := "2.11.7"

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "test_mate"
  )

libraryDependencies ++= Seq(

  // Start with this one
  "org.tpolecat" %% "doobie-core"      % "1.0.0-RC7",

  // And add any of these as needed
  "org.tpolecat" %% "doobie-h2"        % "1.0.0-RC7",
  "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC7",
  "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC7",
  "org.tpolecat" %% "doobie-specs2"    % "1.0.0-RC7" % "test",
  "org.tpolecat" %% "doobie-scalatest" % "1.0.0-RC7" % "test",

  "org.seleniumhq.selenium" % "selenium-java" % "4.10.0"

)

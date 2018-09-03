name := "lagom-jwt-jaas"

version := "0.1"

scalaVersion := "2.12.6"

val scalatic = "org.scalactic" %% "scalactic" % "3.0.5"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
val log4j2_api = "org.apache.logging.log4j" % "log4j-api" % "2.11.0"
val log4j2_core = "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
val log4j2_api_scala =  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
val play = "com.typesafe.play" %% "play" % "latest.integration"

lazy val `lagom-jwt-jaas` = (project in file("."))
  .aggregate(`auth`, `auth-service`, `test-service`)

lazy val `auth` = (project in file("auth"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      play,
    )
  )

lazy val `auth-service` = (project in file("auth-service"))
  .aggregate(`auth-service-api`, `auth-service-api-impl`)

lazy val `test-service` = (project in file("test-service"))
  .aggregate(`test-service-api`, `test-service-api-impl`)

lazy val `auth-service-api` = (project in file("auth-service/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      play
    )
  )

lazy val `auth-service-api-impl` = (project in file("auth-service/api-impl"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      play,
      lagomScaladslApi,
    )
  )
  .dependsOn(`auth`)

lazy val `test-service-api` = (project in file("test-service/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      play
    )
  )

lazy val `test-service-api-impl` = (project in file("test-service/api-impl"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      play,
      lagomScaladslApi,
    )
  )
  .dependsOn(`auth`)


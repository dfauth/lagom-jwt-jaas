name := "lagom-jwt-jaas"

version := "0.1"

scalaVersion := "2.12.6"

val scalatest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
val log4j2_api = "org.apache.logging.log4j" % "log4j-api" % "2.11.0"
val log4j2_core = "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
val log4j2_api_scala =  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
val play = "com.typesafe.play" %% "play" % "latest.integration"
val accord = "com.wix" %% "accord-core" % "0.6.1"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
//val base64 = "me.lessis" %% "base64" % "0.2.0"
val jwt = "com.pauldijou" %% "jwt-play-json" % "0.12.1"
val h2 = "com.h2database" % "h2" % "latest.integration"

lazy val `lagom-jwt-jaas` = (project in file("."))
  .aggregate(`auth`, `auth-service`, `session-service`, `user-service`)

lazy val `auth` = (project in file("auth"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      lagomScaladslApi,
      lagomScaladslServer,
      accord,
      jwt,
      play,
    )
  )

lazy val `auth-service` = (project in file("auth-service"))
  .aggregate(`auth-service-api`, `auth-service-api-impl`)

lazy val `session-service` = (project in file("session-service"))
  .aggregate(`session-service-api`, `session-service-api-impl`)

lazy val `user-service` = (project in file("user-service"))
  .aggregate(`user-service-api`, `user-service-api-impl`)

lazy val `auth-service-api` = (project in file("auth-service/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      play
    )
  )
  .dependsOn(`auth`)

lazy val `auth-service-api-impl` = (project in file("auth-service/api-impl"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      play,
      lagomScaladslApi,
      lagomScaladslPersistenceJdbc,
    )
  )
  .dependsOn(`auth-service-api`)
  .dependsOn(`auth`)

lazy val `session-service-api` = (project in file("session-service/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      play
    )
  )

lazy val `session-service-api-impl` = (project in file("session-service/api-impl"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      play,
      lagomScaladslApi,
      lagomScaladslPersistenceJdbc,
    )
  )
  .dependsOn(`session-service-api`)
  .dependsOn(`auth`)

lazy val `user-service-api` = (project in file("user-service/api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      play
    )
  )
  .dependsOn(`auth`)

lazy val `user-service-api-impl` = (project in file("user-service/api-impl"))
  .settings(
    libraryDependencies ++= Seq(
      scalatest,
      log4j2_api,
      log4j2_core,
      log4j2_api_scala,
      play,
      lagomScaladslApi,
      lagomScaladslPersistenceJdbc,
      lagomScaladslDevMode,
      h2,
      macwire,
    )
  )
  .dependsOn(`user-service-api`)
  .dependsOn(`auth`)


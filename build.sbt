name := """blackbird"""
organization := "yakovlevsv"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  javaJpa,
  guice,
  "org.hibernate" % "hibernate-entitymanager" % "5.4.1.Final",
  "org.mockito" % "mockito-core" % "2.25.0" % Test,
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.zaxxer" % "HikariCP" % "3.3.0",
  "com.atlassian.commonmark" % "commonmark" % "0.12.1"
)

PlayKeys.externalizeResources := false
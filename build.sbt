name := """blackbird"""
organization := "yakovlevsv"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.8"

libraryDependencies += guice

libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"
libraryDependencies += "com.zaxxer" % "HikariCP" % "3.3.0"
libraryDependencies ++= Seq(javaJpa, "org.hibernate" % "hibernate-entitymanager" % "5.4.1.Final")
PlayKeys.externalizeResources := false
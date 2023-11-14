name := """tve2023_testNoviembre"""
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.4"
libraryDependencies ++= Seq(
  jdbc,
  javaEbean,
  "org.webjars" % "bootstrap" % "3.3.7",
  //  "mysql" % "mysql-connector-java" % "5.1.46",
  //"org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  //"org.postgresql" % "postgresql" % "42.6.0",
  "com.impossibl.pgjdbc-ng" % "pgjdbc-ng" % "0.8.9",
  "org.webjars" % "font-awesome" % "5.4.1",
  "org.webjars" % "animate.css" % "3.2.5",
  "org.json" % "org.json" % "chargebee-1.0",
  "org.webjars.bower" % "moment" % "2.29.4",
  "com.sun.mail" % "javax.mail" % "1.6.2",
  "org.apache.commons" % "commons-lang3" % "3.13.0"
)
libraryDependencies += filters

lazy val root = (project in file(".")).enablePlugins(PlayJava)

play.PlayImport.PlayKeys.playDefaultPort := 8087

doc in Compile <<= target.map(_ / "none")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror", "-J-Xms128M", "-J-Xmx512m", "-J-server")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")


// Para hacer una distribucion tipo debian server    mediante play debian:packageBin

import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

import com.typesafe.sbt.packager.archetypes.ServerLoader
serverLoading in Debian := ServerLoader.Systemd

maintainer in Linux := "Eduardo Puente <epuente@ipn.mx>"
packageSummary in Linux := "Sistema de TV Educativa - Videoteca"
packageDescription := "Sistema de TV Educativa - Videoteca"



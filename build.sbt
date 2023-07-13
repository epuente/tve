name := """tve2023_dev"""
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
  "com.sun.mail" % "javax.mail" % "1.6.2"
)     

libraryDependencies += filters 
lazy val root = (project in file(".")).enablePlugins(PlayJava)
play.PlayImport.PlayKeys.playDefaultPort := 8087

doc in Compile <<= target.map(_ / "none")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")




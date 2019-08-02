import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

name := "sgfcv"

version := "0.5"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps")

lazy val `sgfcv` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice, 
  filters, 
  jdbc , 
  cacheApi ,
  ws , 
  specs2 % Test,
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "org.firebirdsql.jdbc" % "jaybird-jdk18" % "3.0.4",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "com.typesafe.play" %% "play-json-joda" % "2.6.10",
  "com.pauldijou" %% "jwt-play" % "0.16.0",
  "org.apache.commons" % "commons-email" % "1.5",
  "com.jaroop" %% "anorm-relational" % "0.3.0",
  "org.joda" % "joda-money" % "0.10.0",
  // JasperReport
  "org.olap4j" % "olap4j" % "1.2.0",
  "com.lowagie" % "itext" % "2.1.7.js6",
  "org.springframework" % "spring-beans" % "5.1.0.RELEASE",
  "net.sf.jasperreports" % "jasperreports" % "6.7.0",
  "net.sf.jasperreports" % "jasperreports-functions" % "6.7.0",
  "net.sf.jasperreports" % "jasperreports-chart-themes" % "6.7.0",
  "com.norbitltd" %% "spoiwo" % "1.4.1"  
)

// Play framework hooks for development
PlayKeys.playRunHooks += WebpackServer(file("./front"))

unmanagedResourceDirectories in Test +=  baseDirectory ( _ /"target/web/public/test" ).value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Jasper" at "https://jaspersoft.artifactoryonline.com/jaspersoft/jr-ce-releases/"
resolvers += "Jasper2" at "https://jaspersoft.jfrog.io/jaspersoft/jaspersoft-repo/"

// Production front-end build
lazy val cleanFrontEndBuild = taskKey[Unit]("Remove the old front-end build")

cleanFrontEndBuild := {
  val d = file("public/bundle")
  if (d.exists()) {
    d.listFiles.foreach(f => {
      if(f.isFile) f.delete
    })
  }
}

lazy val frontEndBuild = taskKey[Unit]("Execute the npm build command to build the front-end")

frontEndBuild := {
  println(Process(s"cmd /c npm install", file("front")).!!)
  println(Process(s"cmd /c npm run build:prod", file("front")).!!)
}

frontEndBuild := (frontEndBuild dependsOn cleanFrontEndBuild).value

dist := (dist dependsOn frontEndBuild).value

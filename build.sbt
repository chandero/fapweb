import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

packageSummary in Windows := "SIF WEB v1.0.0"
packageDescription in Windows := "Sistema de Gestión Financiera COODIN"
maintainer := "aldacm2001@gmail.com"

scalaVersion := "2.13.8"

scalacOptions += "-feature"
scalacOptions += "-language:postfixOps"

// crossScalaVersions := Seq("2.12.7", "2.12.11")

lazy val `sifweb` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  filters,
  jdbc,
  cacheApi,
  ws,
  specs2 % Test,
  "org.playframework.anorm" %% "anorm" % "2.6.10",
  "org.postgresql" % "postgresql" % "42.2.2",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.typesafe.play" %% "play-json-joda" % "2.9.2",
  "com.pauldijou" %% "jwt-play" % "4.2.0",
  "org.apache.commons" % "commons-email" % "1.5",
  // "com.jaroop" %% "anorm-relational" % "0.4.0",
  // Jasper Report
  "org.olap4j" % "olap4j" % "1.2.0",
  "com.lowagie" % "itext" % "2.1.7.js7",
  "org.springframework" % "spring-beans" % "5.1.0.RELEASE",
  "net.sf.jasperreports" % "jasperreports" % "6.13.0",
  "net.sf.jasperreports" % "jasperreports-functions" % "6.13.0",
  "net.sf.jasperreports" % "jasperreports-chart-themes" % "6.13.0",
  "com.norbitltd" %% "spoiwo" % "1.7.0",
  "org.mozilla" % "rhino" % "1.7R3",
  "com.lihaoyi" %% "requests" % "0.6.2",
  "org.joda" % "joda-money" % "1.0.1",
  "org.firebirdsql.jdbc" % "jaybird" % "4.0.0.java8",
  // JSON
  "net.liftweb" %% "lift-json" % "3.4.1",
  "net.liftweb" %% "lift-json-ext" % "3.4.1",
  // BCRYPT
  "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
  // CSV parser
  "org.apache.poi" % "poi" % "5.2.0",
  "org.apache.poi" % "poi-ooxml" % "5.2.0",
  "com.univocity" % "univocity-parsers" % "2.4.1",
  "com.beachape" %% "enumeratum" % "1.6.1",
  // Docx Files
  "org.docx4j" % "docx4j-core" % "11.3.2",
  "org.docx4j" % "docx4j-JAXB-ReferenceImpl" % "11.3.2",
  "org.docx4j" % "docx4j-JAXB-MOXy" % "11.3.2",
  "org.docx4j" % "docx4j-export-fo" % "11.3.2",
  "org.docx4j" % "docx4j-conversion-via-microsoft-graph" % "11.3.2",
  "org.plutext.graph-convert" % "graph-convert-base" % "1.0.3",
  "org.plutext.graph-convert" % "using-graph-sdk" % "1.0.3",
  "fr.opensagres.xdocreport" % "fr.opensagres.poi.xwpf.converter.pdf" % "2.0.3",
  // SLF4J
  "org.slf4j" % "slf4j-api" % "1.8.0-beta1",
  "org.slf4j" % "slf4j-simple" % "1.8.0-beta1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.qos.logback" % "logback-core" % "1.+"
)

// Play framework hooks for development
// PlayKeys.playRunHooks += WebpackServer(file("./frontend"))

unmanagedResourceDirectories in Test += baseDirectory(
  _ / "target/web/public/test"
).value

resolvers += "mvnrepository" at "https://mvnrepository.com"
resolvers += "mvnrepository2" at "https://repo1.maven.org/maven2"
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Jasper" at "https://jaspersoft.artifactoryonline.com/jaspersoft/jr-ce-releases/"
resolvers += "Jasper2" at "https://jaspersoft.jfrog.io/jaspersoft/jaspersoft-repo/"
resolvers += "Jasper3" at "https://jaspersoft.artifactoryonline.com/jaspersoft/repo/"
resolvers += "Jasper4" at "https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports"
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
// Production front-end build
lazy val cleanFrontEndBuild = taskKey[Unit]("Remove the old front-end build")

cleanFrontEndBuild := {
  val d = file("public/bundle")
  if (d.exists()) {
    d.listFiles.foreach(f => {
      if (f.isFile) f.delete
    })
  }
}

/*
lazy val frontEndBuild = taskKey[Unit]("Execute the npm build command to build the front-end")

frontEndBuild := {
  //println(Process(s"cmd /c npm install", file("frontend")).!!)
  println(Process(s"cmd /c npm run build", file("frontend")).!!)
}

frontEndBuild := (frontEndBuild dependsOn cleanFrontEndBuild).value

dist := (dist dependsOn frontEndBuild).value
 */

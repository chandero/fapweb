import com.typesafe.sbt.packager.MappingsHelper._
mappings in Universal ++= directory(baseDirectory.value / "public")

packageSummary in Windows := "FAPWEB v1.0.0"
packageDescription in Windows := "Sistema de Gestión Financiera FAP"
maintainer := "aldacm2001@gmail.com"

scalaVersion := "2.12.17"
javacOptions ++= Seq("-source", "11", "-target", "11")
scalacOptions += "-feature"
scalacOptions += "-language:postfixOps"

// crossScalaVersions := Seq("2.12.7", "2.12.11")

lazy val `fapweb` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  filters,
  jdbc,
  cacheApi,
  ws,
  specs2 % Test,
  "ch.qos.logback" % "logback-classic" % "1.4.4",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.36",
  "org.slf4j" % "slf4j-simple" % "1.7.36" % Provided,
  "javax.xml.bind" % "jaxb-api" % "2.3.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.4",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.1",
//  "com.typesafe" %% "jse" % "1.2.4" exclude("org.slf4j", "slf4j-simple"),
  "org.playframework.anorm" %% "anorm" % "2.7.0",
  "com.jaroop" %% "anorm-relational" % "0.4.0",
  "org.firebirdsql.jdbc" % "jaybird" % "4.0.8.java11",
  "com.typesafe.play" %% "play-json" % "2.6.10",
  "com.typesafe.play" %% "play-json-joda" % "2.6.10",
  "com.pauldijou" %% "jwt-play" % "4.2.0",
  //
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  // "com.github.maricn" % "logback-slack-appender" % "1.6.1",
  //
  "org.apache.commons" % "commons-email" % "1.5",
  // Jasper Report
  "org.olap4j" % "olap4j" % "1.2.0",
  "com.lowagie" % "itext" % "2.1.7.js7",
  "org.springframework" % "spring-beans" % "5.1.0.RELEASE",
  "net.sf.jasperreports" % "jasperreports" % "6.13.0",
  "net.sf.jasperreports" % "jasperreports-functions" % "6.13.0",
  "net.sf.jasperreports" % "jasperreports-chart-themes" % "6.13.0",
  "com.norbitltd" %% "spoiwo" % "1.7.0",
  "org.mozilla" % "rhino" % "1.7R3",
  // Otras
  "org.joda" % "joda-money" % "1.0.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.lihaoyi" %% "requests" % "0.7.0",
  // JSON
  "net.liftweb" %% "lift-json" % "3.4.1",
  "net.liftweb" %% "lift-json-ext" % "3.4.1",
  // CSV parser
  "org.apache.poi" % "poi" % "5.2.0",
  "org.apache.poi" % "poi-ooxml" % "5.2.0",
  "com.deepoove" % "poi-tl" % "1.12.0",
  "com.univocity" % "univocity-parsers" % "2.4.1",
  //
  "org.apache.httpcomponents" % "httpcore" % "4.4.13",
  //
  "com.beachape" %% "enumeratum" % "1.6.1",
  // Pdf Generator
  "com.hhandoko" %% "play28-scala-pdf" % "4.3.0",
  //
  "net.codingwell" %% "scala-guice" % "4.2.11",
  "commons-io" % "commons-io" % "2.4",
  // Docx Files
  "org.docx4j" % "docx4j-core" % "11.3.2",
  "org.docx4j" % "docx4j-JAXB-ReferenceImpl" % "11.3.2",
  "org.docx4j" % "docx4j-JAXB-MOXy" % "11.3.2",
  "org.docx4j" % "docx4j-export-fo" % "11.3.2",
  "org.docx4j" % "docx4j-conversion-via-microsoft-graph" % "11.3.2",
  "org.plutext.graph-convert" % "graph-convert-base" % "1.0.3",
  "org.plutext.graph-convert" % "using-graph-sdk" % "1.0.3",
  "fr.opensagres.xdocreport" % "fr.opensagres.poi.xwpf.converter.pdf" % "2.0.3",
  // to Pdf
  "antlr" % "antlr" % "2.7.7",
  "org.antlr" % "antlr-runtime" % "3.5.2",
  "org.apache.avalon.framework" % "avalon-framework-api" % "4.3.1",
  "org.apache.avalon.framework" % "avalon-framework-impl" % "4.3.1",
  "org.apache.xmlgraphics" % "batik-anim" % "1.14",
  "org.apache.xmlgraphics" % "batik-awt-util" % "1.14",
  "org.apache.xmlgraphics" % "batik-bridge" % "1.14",
  "org.apache.xmlgraphics" % "batik-constants" % "1.14",
  "org.apache.xmlgraphics" % "batik-css" % "1.14",
  "org.apache.xmlgraphics" % "batik-dom" % "1.14",
  "org.apache.xmlgraphics" % "batik-ext" % "1.14",
  "org.apache.xmlgraphics" % "batik-extension" % "1.14",
  "org.apache.xmlgraphics" % "batik-gvt" % "1.14",
  "org.apache.xmlgraphics" % "batik-i18n" % "1.14",
  "org.apache.xmlgraphics" % "batik-parser" % "1.14",
  "org.apache.xmlgraphics" % "batik-script" % "1.14",
  "org.apache.xmlgraphics" % "batik-svg-dom" % "1.14",
  "org.apache.xmlgraphics" % "batik-svggen" % "1.14",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.14",
  // Pdf Box
  "org.apache.pdfbox" % "pdfbox" % "3.0.0-RC1",
)

// Play framework hooks for development
// PlayKeys.playRunHooks += WebpackServer(file("./frontend"))

unmanagedResourceDirectories in Test += baseDirectory(
  _ / "target/web/public/test"
).value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Jasper" at "https://jaspersoft.artifactoryonline.com/jaspersoft/jr-ce-releases/"
resolvers += "Jasper2" at "https://jaspersoft.jfrog.io/jaspersoft/jaspersoft-repo/"
resolvers += "Jasper3" at "https://jaspersoft.artifactoryonline.com/jaspersoft/repo/"
resolvers += "Jasper4" at "https://mvnrepository.com/artifact/net.sf.jasperreports/jasperreports"

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

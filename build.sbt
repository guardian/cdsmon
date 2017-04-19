name := "cdsmon"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"

val circeVersion = "0.7.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.5"

import com.typesafe.sbt.packager.archetypes.ServerLoader.Systemd
serverLoading in Debian := Systemd

lazy val `cdsmon` = (project in file(".")).enablePlugins(PlayScala, RiffRaffArtifact, JDebPackaging)
  .settings(Defaults.coreDefaultSettings: _*)
  .settings(
    name in Universal := normalizedName.value,
    topLevelDirectory := Some(normalizedName.value),
    riffRaffPackageName := "cds-logging",
    riffRaffManifestProjectName := s"multimedia:cds-logging",
    riffRaffBuildIdentifier :=  Option(System.getenv("BUILD_NUMBER")).getOrElse("DEV"),
    riffRaffUploadArtifactBucket := Option("riffraff-artifact"),
    riffRaffUploadManifestBucket := Option("riffraff-builds"),
    riffRaffManifestBranch := Option(System.getenv("BRANCH_NAME")).getOrElse("unknown_branch"),

    riffRaffPackageType := (packageBin in Debian).value,

    debianPackageDependencies := Seq("openjdk-8-jre-headless"),
    maintainer := "Andyn Gallagher <andy.gallagher@guardian.co.uk>",
    packageSummary := "CDSMonitor (aka cds-logging)",
    packageDescription := """Simple monitoring interface for the Content Delivery System""",

//    riffRaffArtifactResources ++= Seq(
//      baseDirectory.value / "cloudformation" / "AtomWorkshop.yml" -> s"packages/cloudformation/AtomWorkshop.yml"
//    ),
    javaOptions in Universal ++= Seq(
      "-Dpidfile.path=/dev/null"
    )
  )
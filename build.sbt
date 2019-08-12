import com.typesafe.sbt.packager.docker
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{dockerExposedPorts, dockerPermissionStrategy, dockerUsername}
import com.typesafe.sbt.packager.docker.{Cmd, DockerPermissionStrategy}

name := "cdsmon"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq( jdbc  , ws , guice , specs2 % Test )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.42"

val circeVersion = "0.9.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.5"


val jacksonVersion = "2.9.6"
//update vulnerable jackson-databind
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion

debianPackageDependencies := Seq("openjdk-8-jre-headless")
serverLoading in Debian := Some(ServerLoader.Systemd)
serviceAutostart in Debian := false

lazy val `cdsmon` = (project in file(".")).enablePlugins(PlayScala, DockerPlugin, AshScriptPlugin)
  .settings(Defaults.coreDefaultSettings: _*)
  .settings(
    name in Universal := normalizedName.value,
    topLevelDirectory := Some(normalizedName.value),

    debianPackageDependencies := Seq("openjdk-8-jre-headless"),
    maintainer := "Andy Gallagher <andy.gallagher@guardian.co.uk>",
    packageSummary := "CDSMonitor (aka cds-logging)",
    packageDescription := """Simple monitoring interface for the Content Delivery System""",

    dockerPermissionStrategy := DockerPermissionStrategy.Run,
    daemonUserUid in Docker := None,
    daemonUser in Docker := "daemon",
    dockerUsername  := sys.props.get("docker.username"),
    dockerRepository := Some("guardianmultimedia"),
    packageName in Docker := "guardianmultimedia/cdsmon",
    packageName := "cdsmon",
    dockerBaseImage := "openjdk:8-jdk-alpine",
    dockerAlias := docker.DockerAlias(None, Some("guardianmultimedia"),"cdsmon",Some(sys.props.getOrElse("build.number","DEV"))),
    dockerCommands ++= Seq(
      Cmd("USER","root"), //fix the permissions in the built docker image
      Cmd("RUN", "chown daemon /opt/docker"),
      Cmd("RUN", "chmod u+w /opt/docker"),
      Cmd("RUN", "chmod -R a+x /opt/docker"),
      Cmd("USER", "daemon")
    ),

    javaOptions in Universal ++= Seq(
      "-Dpidfile.path=/dev/null"
    )
  )
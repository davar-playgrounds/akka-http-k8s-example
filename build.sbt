import scala.sys.process._

lazy val akkaHttpVersion = "10.1.4"
lazy val akkaVersion    = "2.5.15"
lazy val akkaManagementVersion = "0.13.1"

lazy val root = (project in file(".")).
  enablePlugins(JavaServerAppPackaging, DockerPlugin).
  settings(
      inThisBuild(List(
      organization    := "thor",
      scalaVersion    := "2.12.6",
      version         := "0.0.1-SNAPSHOT"
    )),
    name := "akka-http-k8s-example",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster"           % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding"  % akkaVersion,

      "com.lightbend.akka.discovery"  %% "akka-discovery-kubernetes-api"      % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management"                    % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management-cluster-http"       % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap"  % akkaManagementVersion,
      "com.lightbend.akka.discovery"  %% "akka-discovery-dns"                 % akkaManagementVersion,

      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test
    ),
    dockerBaseImage := "openjdk:8",
    dockerUsername := Some("thor"),

    certLocation  := s"${target.value}/generatedCert",
    certSubject   := "akka-nginx"
)

//
// Certificate Generation
//

val certSubject         = settingKey[String]("subject to use in generated cert")
val certLocation        = settingKey[String]("Where to place the generated file")

resourceGenerators in Compile += Def.task {
  generateCertificate(
    file((certLocation in Compile).value),
    (certSubject in Compile).value
  )
}.taskValue

def generateCertificate(location: File, subject: String): Seq[File] = {
  val keyFile = file(s"$location.key")
  val certFile = file(s"$location.crt")

  if (keyFile.exists() && certFile.exists()) {
    println(s"certs present at in $location")
    Seq(location)
  } else {
    println(s"generating certs in $location")
    val cmd = Seq(
      "openssl",
      "req", "-x509",
      "-nodes",
      "-days", "365",
      "-newkey", "rsa:2048",
      "-keyout", keyFile.absolutePath,
      "-out", certFile.absolutePath,
      "-subj", s"/CN=$subject/O=$subject")
    println(cmd !!)
    Seq(keyFile, certFile)
  }
}


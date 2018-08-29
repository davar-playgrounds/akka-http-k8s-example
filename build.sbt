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
    dockerUsername := Some("thor")
  )


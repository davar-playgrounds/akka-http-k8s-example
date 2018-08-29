package com.thor

//#quick-start-server
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.{ ActorRef, ActorSystem }
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.management.AkkaManagement
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.server.Directives._

object QuickstartServer extends App with UserRoutes {

  lazy val config = ConfigFactory.load()
  implicit val system = ActorSystem("akka-simple-cluster")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val cluster = Cluster(system)

  AkkaManagement(system).start()
  ClusterBootstrap(system).start()

  val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")

  lazy val routes: Route = userRoutes
  val hostname = config.getString("application.api.host")
  val port = config.getInt("application.api.port")
  //Http().bindAndHandle(complete(config.getString("application.api.hello-message")), config.getString("application.api.host"), config.getInt("application.api.port"))
  Http().bindAndHandle(routes, hostname, port)

  println(s"Server online at http://${hostname}:${port}/")

  Await.result(system.whenTerminated, Duration.Inf)
}

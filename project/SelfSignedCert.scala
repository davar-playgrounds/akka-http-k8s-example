import scala.sys.process._
import sbt.Keys._
import sbt._
import sbt.{Def, File, file, settingKey}

object SelfSignedCert extends AutoPlugin {

  object autoImport {
    val certSubject = settingKey[String]("subject to use in generated cert")
    val certLocation = settingKey[String]("Where to place the generated file")
    //val generateCertificate = taskKey[Seq[File]]("Generates cert")
  }

  import autoImport._

  override def projectSettings = Seq(

    resourceGenerators in Compile += Def.task {
      generateCertificate(
        file((certLocation in Compile).value),
        (certSubject in Compile).value
      )
    }.taskValue
  )

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
        "-keyout", keyFile.getAbsolutePath,
        "-out", certFile.getAbsolutePath,
        "-subj", s"/CN=$subject/O=$subject")
      (cmd !)
      Seq(keyFile, certFile)
    }
  }
}
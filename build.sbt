name := "chisel3-dsp-module"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"))

val defaultVersions = Map(
  "firrtl" -> "1.1-SNAPSHOT",
  "dsptools" -> "1.0",
  "chisel3" -> "3.1-SNAPSHOT",
  "chisel-iotesters" -> "1.2-SNAPSHOT")

libraryDependencies ++= (Seq("chisel3","chisel-iotesters","dsptools","firrtl").map {
  dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep)) })

libraryDependencies += "org.spire-math" %% "spire" % "0.11.0"

libraryDependencies += "org.scalanlp" %% "breeze" % "0.12"

libraryDependencies += "co.theasi" %% "plotly" % "0.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.5",
  "org.scalacheck" %% "scalacheck" % "1.12.4")

lazy val recordHash = TaskKey[Unit]("record-chisel3dsp-dependency-hash")

recordHash := {
    import java.io._
    try {
      val hash = sys.env("Chisel3DSPDependenciesCommit")
      val pw = new PrintWriter(new File("Chisel3DSPDependencies.hash"))
      pw.write(hash.toString)
      pw.close
    } catch {
      case e: Exception => println("Run source setenv.bash in your Chisel3DSPDependencies directory!")
      System.exit(0)
    } 
}

compile in Compile <<= (compile in Compile).dependsOn(recordHash)
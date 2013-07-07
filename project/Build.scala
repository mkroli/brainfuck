import sbt._
import sbt.Keys._
import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._
import sbtbuildinfo.Plugin._

object Build extends sbt.Build {
  lazy val projectSettings = Seq(
    name := "brainfuck",
    organization := "com.github.mkroli",
    scalaVersion := "2.10.2")

  lazy val projectDependencies = Seq(
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "2.1.0"))

  lazy val projectAssemblySettings = Seq(
    jarName in assembly <<= (name, version) { (name, version) =>
      "%s-%s.jar".format(name, version)
    })

  lazy val projectBuildInfoSettings = Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.github.mkroli.brainfuck.build")

  lazy val projectReleaseSettings = Seq(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion))

  lazy val brainfuck = Project(
    id = "brainfuck",
    base = file("."),
    settings = Defaults.defaultSettings ++
      projectSettings ++
      projectDependencies ++
      assemblySettings ++
      projectAssemblySettings ++
      buildInfoSettings ++
      projectBuildInfoSettings ++
      releaseSettings ++
      projectReleaseSettings)
}

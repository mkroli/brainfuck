name := "brainfuck"

organization := "com.github.mkroli"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.3.0")

jarName in assembly <<= (name, version) map { (name, version) =>
  "%s-%s.jar".format(name, version)
}

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version)

buildInfoPackage := "com.github.mkroli.brainfuck.build"

releaseSettings

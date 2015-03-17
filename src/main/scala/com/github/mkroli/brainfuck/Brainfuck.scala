/*
 * Copyright 2013-2015 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mkroli.brainfuck

import scala.io.Source

import com.github.mkroli.brainfuck.build.BuildInfo

import scopt.OptionParser

object Brainfuck extends App {
  object Action extends Enumeration {
    val RunBrainfuck, RunOok, ConvertBrainfuck, ConvertOok = Value
  }

  case class Config(
    actions: List[Action.Value] = Nil,
    debug: Boolean = false,
    filename: String = "")

  val parser = new OptionParser[Config](BuildInfo.name) {
    head(BuildInfo.name, BuildInfo.version)

    help("help") abbr "h" text "Display help message"

    private def addAction(action: Action.Value)(u: Unit, config: Config) =
      config.copy(actions = action :: config.actions)

    opt[Unit]('b', "run-brainfuck") text "Run Brainfuck Code" action addAction(Action.RunBrainfuck)
    opt[Unit]('o', "run-ook") text "Run Ook! Code" action addAction(Action.RunOok)
    opt[Unit]('B', "convert-brainfuck") text "Convert Brainfuck Code" action addAction(Action.ConvertBrainfuck)
    opt[Unit]('O', "convert-ook") text "Convert Ook! Code" action addAction(Action.ConvertOok)
    opt[Unit]('d', "debug") text "Enable debugging output" action { (_, c) =>
      c.copy(debug = true)
    }

    arg[String]("<file>") text "The file to process" action { (f, c) =>
      c.copy(filename = f)
    }
  }

  parser.parse(args, Config()).map { config =>
    lazy val sourceChars = Source.fromFile(config.filename).toSeq
    lazy val source = sourceChars.mkString

    config.actions.foreach {
      case Action.RunBrainfuck =>
        BrainfuckMachine.brainfuck(source, config.debug)
      case Action.RunOok =>
        BrainfuckMachine.ook(source, config.debug)
      case Action.ConvertBrainfuck =>
        println(Ook.toOok(sourceChars))
      case Action.ConvertOok =>
        println(Ook.fromOok(source))
    }
  }
}

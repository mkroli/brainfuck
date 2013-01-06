/*
 * Copyright 2013 Michael Krolikowski
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

import scopt.immutable.OptionParser

object Brainfuck extends App {
  object Action extends Enumeration {
    val RunBrainfuck, RunOok, ConvertBrainfuck, ConvertOok = Value
  }

  case class Config(
    actions: List[Action.Value] = Nil,
    debug: Boolean = false,
    filename: String = "")

  val parser = new OptionParser[Config](BuildInfo.name, BuildInfo.version) {
    def options = Seq(
      help("h", "help", "Display help message"),
      flag("b", "run-brainfuck", "Run Brainfuck Code") { c =>
        c.copy(actions = Action.RunBrainfuck :: c.actions)
      },
      flag("o", "run-ook", "Run Ook! Code") { c =>
        c.copy(actions = Action.RunOok :: c.actions)
      },
      flag("B", "convert-brainfuck", "Convert Brainfuck Code") { c =>
        c.copy(actions = Action.ConvertBrainfuck :: c.actions)
      },
      flag("O", "convert-ook", "Convert Ook! Code") { c =>
        c.copy(actions = Action.ConvertOok :: c.actions)
      },
      flag("d", "debug", "Enable debugging output") { c =>
        c.copy(debug = true)
      },
      arg("file", "The file to process") { (filename, c) =>
        c.copy(filename = filename)
      })
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
        println(Ook.toOok(sourceChars), config.debug)
      case Action.ConvertOok =>
        println(Ook.fromOok(source), config.debug)
    }
  }
}

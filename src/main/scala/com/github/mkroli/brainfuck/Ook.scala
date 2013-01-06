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

import scala.collection.GenSeq

object Ook {
  private lazy val brainfuckToOok = Map(
    '+' -> "Ook. Ook.",
    '-' -> "Ook! Ook!",
    '>' -> "Ook. Ook?",
    '<' -> "Ook? Ook.",
    '[' -> "Ook! Ook?",
    ']' -> "Ook? Ook!",
    '.' -> "Ook! Ook.",
    ',' -> "Ook. Ook!")

  private lazy val ookToBrainfuck = brainfuckToOok.map {
    case (k, v) => v -> k
  }

  def fromOok(ook: String): GenSeq[Char] = {
    ook.split("\\s+").toList.grouped(2).map {
      case a :: b :: Nil => "%s %s".format(a, b)
      case _ => throw new RuntimeException
    }.map(ookToBrainfuck).toSeq
  }

  def toOok(instructions: GenSeq[Char]): String = {
    instructions.flatMap(brainfuckToOok.get).toList.grouped(8).map(_.mkString(" ")).mkString("\n")
  }
}

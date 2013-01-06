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

import scala.annotation.tailrec
import scala.collection.immutable.Stack

class BrainfuckMachine private (
  instructions: List[Char],
  debug: Boolean,
  instructionPointer: Int,
  markers: Stack[Int],
  tape: Tape) {
  def this(instructions: List[Char], debug: Boolean) =
    this(instructions, debug, 0, new Stack(), new Tape)

  def next = instructions.lift(instructionPointer) match {
    case Some(i) => Some(i match {
      case '>' => new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape > 1)
      case '<' => new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape < 1)
      case '+' => new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape + 1)
      case '-' => new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape - 1)
      case '.' => {
        print(tape())
        new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape)
      }
      case ',' => new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape(System.in.read().toChar))
      case '[' => {
        @tailrec
        def codeAfterLoop(instructions: List[Char], start: Int, innerLoops: Int, pos: Int): Int = instructions match {
          case Nil => throw new RuntimeException
          case ']' :: _ if pos > start && innerLoops == 0 => pos + 1
          case ']' :: tail if pos > start => codeAfterLoop(tail, start, innerLoops - 1, pos + 1)
          case '[' :: tail if pos > start => codeAfterLoop(tail, start, innerLoops + 1, pos + 1)
          case _ :: tail => codeAfterLoop(tail, start, innerLoops, pos + 1)
        }
        if (tape() == 0)
          new BrainfuckMachine(instructions, debug, codeAfterLoop(instructions, instructionPointer, 0, 0), markers, tape)
        else
          new BrainfuckMachine(instructions, debug, instructionPointer + 1, instructionPointer +: markers, tape)
      }
      case ']' => {
        val (ptr, m) = markers.pop2
        new BrainfuckMachine(instructions, debug, ptr, m, tape)
      }
      case '#' if debug => {
        println(tape.debugString)
        new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape)
      }
      case _ => new BrainfuckMachine(instructions, debug, instructionPointer + 1, markers, tape)
    })
    case None => None
  }
}

object BrainfuckMachine {
  @tailrec
  private def run(bfm: BrainfuckMachine): Unit = bfm.next match {
    case Some(bfm) => run(bfm)
    case None => Unit
  }

  def apply(instructions: List[Char], debug: Boolean = false) =
    run(new BrainfuckMachine(instructions, debug))

  def brainfuck(instructions: String, debug: Boolean = false) =
    BrainfuckMachine(instructions.toCharArray.toList, debug)

  def ook(instructions: String, debug: Boolean = false) =
    BrainfuckMachine(Ook.fromOok(instructions).toList, debug)
}

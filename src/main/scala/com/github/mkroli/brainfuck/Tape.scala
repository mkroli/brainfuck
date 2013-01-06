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

class Tape private (i: Int, t: Map[Int, Int]) {
  def this() = this(0, Map().withDefaultValue(0))

  def >(c: Int) = new Tape(i + c, t)

  def <(c: Int) = new Tape(i - c, t)

  private def adjustMap[A, B](m: Map[A, B], key: A)(f: B => B) =
    m + (key -> f(m(key)))

  def +(c: Int) = new Tape(i, adjustMap(t, i)(_ + c))

  def -(c: Int) = new Tape(i, adjustMap(t, i)(_ - c))

  def apply() = t(i).toChar

  def apply(c: Int) = new Tape(i, t.updated(i, c.toInt))

  def debugString = {
    val (min, max) = t.foldLeft(0, 0) {
      case ((min, max), (i, _)) if i < min => (i, max)
      case ((min, max), (i, _)) if i > max => (min, i)
      case ((min, max), (_, _)) => (min, max)
    }
    (min to max).map(t).mkString(" ")
  }
}

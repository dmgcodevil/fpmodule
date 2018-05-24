package com.dmgcodevil.fpmodule

import scalaz.Kleisli
import scalaz.std.option._
import scalaz.syntax.bind._


// final case class Kleisli[M[_], A, B](run: A => M[B])
// where M is a context
// A input type
// B output type
// example:
//  Kleisli { String => Option[Int] } produces Kleisli[Option, String, Int]

//source https://blog.ssanj.net/posts/2017-06-07-composing-monadic-functions-with-kleisli-arrows.html
object KleisliComposition {

}

// composition of contextual results, introduction
object CompositionExample {
  def stringToNonEmptyString: String => Option[String] = value =>
    if (value.nonEmpty) Option(value) else None

  def stringToNumber: String => Option[Int] = value =>
    if (value.matches("-?[0-9]+")) Option(value.toInt) else None

  // below function doesn't compile because expected type is String but given Option[String]
  //def composed : String => Option[Int] = stringToNumber compose stringToNonEmptyString

  // these fucntions can be composed using Kleisli arrow
  def composed: String => Option[Int] = Kleisli(stringToNumber) <=< Kleisli(stringToNonEmptyString)

  // we can compose pure monads without Kleisli
  def composedMonads: String => Option[Int] = Option(_) >>= stringToNonEmptyString >>= stringToNumber

  def main(args: Array[String]): Unit = {
    println(composed("1")) // will print Some(1)
    println(composedMonads("2")) // will print Some(2)
  }

}
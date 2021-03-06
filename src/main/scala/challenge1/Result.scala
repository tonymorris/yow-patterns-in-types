package challenge1

import core._, Syntax._


/*
 * Handling errors without exceptions....
 * ======================================
 */

/*
 * A well-typed set of errors that can occur.
 */
sealed trait Error
case class Explosion(exception: Throwable) extends Error
case object NotFound extends Error
case object InvalidRequest extends Error
case object InvalidMethod extends Error
case object Unauthorized extends Error

object Error {
  implicit def ErrorEqual =
    Equal.derived[Error]
}

/*
 * A result type that represents one of our errors or a success.
 */
case class Fail[A](error: Error) extends Result[A]
case class Ok[A](value: A) extends Result[A]

sealed trait Result[A] {
  /*
   * Exercise 1.1:
   *
   * Implement the catamorphism for Result[A].
   *
   * Hint: Try using pattern matching.
   *
   * scala> Ok(1).fold(_ => 0, x => x)
   *  = 1
   *
   * scala> Fail(NotFound).fold(_ => 0, x => x)
   *  = 0
   */
  def fold[X](
    fail: Error => X,
    ok: A => X
  ): X = ???

  /*
   * Exercise 1.2:
   *
   * Implement map for Result[A].
   *
   * The following laws must hold:
   *  1) r.map(z => z) == r
   *  2) r.map(z => f(g(z))) == r.map(g).map(f)
   *
   * scala> Ok(1).map(x => x + 10)
   *  = Ok(11)
   *
   * scala> Fail(NotFound).map(x => x + 10)
   *  = Fail(NotFound)
   *
   * Advanced: Try using flatMap.
   */
  def map[B](f: A => B): Result[B] =
    ???


  /*
   * Exercise 1.3:
   *
   * Implement flatMap (a.k.a. bind, a.k.a. >>=).
   *
   * The following law must hold:
   *   r.flatMap(f).flatMap(g) == r.flatMap(z => f(z).flatMap(g))
   *
   * scala> Ok(1).flatMap(x => Ok(x + 10))
   *  = Ok(11)
   *
   * scala> Ok(1).flatMap(x => Fail[Int](Unauthorized))
   *  = Fail(Unauthorized)
   *
   * scala> Fail(NotFound).map(x => Ok(x + 10))
   *  = Fail(NotFound)
   *
   * scala> Fail(NotFound).map(x => Fail(Unauthorized))
   *  = Fail(NotFound)
   *
   * Advanced: Try using fold.
   */
  def flatMap[B](f: A => Result[B]): Result[B] =
    ???


  /*
   * Exercise 1.4:
   *
   * Extract the value if it is success case otherwise use default value.
   *
   *
   * scala> Ok(1).getOrElse(10)
   *  = 1
   *
   * scala> Fail(NotFound).getOrElse(10)
   *  = 10
   */
  def getOrElse(otherwise: => A): A =
    ???


  /*
   * Exercise 1.4:
   *
   * Implement choice, take this result if successful otherwise take
   * the alternative.
   *
   * scala> Ok(1) ||| Ok(10)
   *  = Ok(1)
   *
   * scala> Ok(1) ||| Fail[Int](Unauthorized)
   *  = Ok(1)
   *
   * scala> Fail[Int](NotFound) ||| Ok(10)
   *  = Ok(10)
   *
   * scala> Fail[Int](NotFound) ||| Fail[Int](Unauthorized)
   *  = Fail(Unauthorized)
   */
  def |||(alternative: => Result[A]): Result[A] =
    ???
}

object Result {
  def fail[A](error: Error): Result[A] =
    Fail(error)

  def ok[A](value: A): Result[A] =
    Ok(value)

  implicit def ResultMonad: Monad[Result] = new Monad[Result] {
    def point[A](a: => A) = ok(a)
    def bind[A, B](a: Result[A])(f: A => Result[B]) = a flatMap f
  }

  implicit def ResultEqual[A: Equal] =
    Equal.from[Result[A]]((a, b) => a.fold(
      e => b.fold(_ === e, _ => false),
      a => b.fold(_ => false, _ === a)
    ))
}


/*
 * *Challenge* Exercise 1.5:
 *
 * Given three methods which may succeed or fail:
 *  - data
 *  - count
 *  - user
 *
 * If the user is valid:
 *  - produce the string representing the user.
 * Otherwise, if the data and count are ok:
 *  - produce a string of the data and count concatenated together
 * Otherwise:
 *  - produce the string "bogus"
 */
object Example {

  case class Input(path: String, data: String, count: Int, user: String, auth: Boolean)

  /* Extract data if path is valid */
  def data(input: Input): Result[String] =
    if (input.path == "/valid") Result.ok(input.data) else Result.fail(NotFound)

  /* Extract count iff we have greater than 0 */
  def count(input: Input): Result[Int] =
    if (input.count > 10) Result.ok(input.count) else Result.fail(InvalidRequest)

  /* Extract user if it authorized */
  def user(input: Input): Result[String] =
    if (input.auth) Result.ok(input.user) else Result.fail(Unauthorized)

  def answer(input: Input) =
    ???
}

/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.test

import org.scalatest.{OptionValues, WordSpecLike, Matchers}
import play.api.mvc.Result
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global

trait UnitSpec extends WordSpecLike with Matchers with OptionValues {

  import scala.concurrent.{Await, Future}
  import scala.concurrent.duration._

  implicit val defaultTimeout = 5 seconds

  implicit def extractAwait[A](future: Future[A]) = await[A](future)

  def await[A](future: Future[A])(implicit timeout: Duration) = Await.result(future, timeout)

  // Convenience to avoid having to wrap andThen() parameters in Future.successful
  implicit def liftFuture[A](v: A) = Future.successful(v)

  def status(of: Result) = of.header.status

  def status(of: Future[Result])(implicit timeout: Duration): Int = status(Await.result(of, timeout))

  def jsonBodyOf(result: Result): JsValue = {
    Json.parse(bodyOf(result))
  }

  def jsonBodyOf(resultF:Future[Result]) :Future[JsValue] = {
    resultF.map(jsonBodyOf)
  }

  def bodyOf(result: Result) : String = {
    new String(result.body.run(Iteratee.consume[Array[Byte]]()))
  }

  def bodyOf(resultF: Future[Result]) : Future[String] = {
    resultF.map(bodyOf)
  }
}

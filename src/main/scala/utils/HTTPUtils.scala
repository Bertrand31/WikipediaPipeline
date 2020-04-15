package utils

import scala.util.{Failure, Success}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import cats.effect._
import sttp.client._
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend

case class HTTPException(url: String, error: String) extends Exception {
  override def toString: String = s"Failed to call $url: $error"
}

object HTTPUtils {

  implicit val cs: ContextShift[IO] = IO.contextShift( scala.concurrent.ExecutionContext.global )

  private val DefaultHTTPTimeout: FiniteDuration = 5.minutes

  def get(url: String, timeout: FiniteDuration = DefaultHTTPTimeout): TryT[IO, String] = TryT {
    AsyncHttpClientCatsBackend[IO]().flatMap { implicit backend =>
      basicRequest
        .header("User-Agent", "wikipedia-pipeline")
        .contentType("application/octet-stream")
        .readTimeout(timeout)
        .get(uri"$url")
        .send
        .map(_.body match {
          case Left(err)   => Failure(HTTPException(url, err))
          case Right(json) => Success(json)
        })
    }
  }
}

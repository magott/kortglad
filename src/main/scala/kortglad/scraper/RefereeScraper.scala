package kortglad.scraper

import java.net.URI
import java.text.DecimalFormat
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import cats.Parallel
import cats.effect.{ContextShift, IO, Sync}
import cats.implicits._
import kortglad.data.{CardStat, MatchStat, RefereeStats}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
  *
  */
class RefereeScraper(client:Client[IO])(implicit shift:ContextShift[IO]) extends Http4sClientDsl[IO]{
  val fotballBaseUrl = Uri.uri("https://www.fotball.no")
  def refereeTemplate(fiksId:Int) = (fotballBaseUrl / "fotballdata" / "person"/"dommeroppdrag").+?("fiksId", fiksId)

  def scrapeMatch(matchUri: Uri):IO[MatchStat] =
    client.expect[String](matchUri).map{ doc =>
      RefereeScraper.parseMatch(matchUri, Jsoup.parse(doc))
    }

  def scrapeMatches(fiksId:Int) =
    client.expect[String](refereeTemplate(fiksId)).map{ doc =>
      val (refName, urls) = RefereeScraper.parseMatches(fiksId, Jsoup.parse(doc))
      (refName, urls.map(x => fotballBaseUrl.withPath(x)))
    }

  def findRefereeStats(fiksId: Int) : IO[RefereeStats] = {
    scrapeMatches(fiksId).flatMap{
      case (uri, matches) =>
        matches.parTraverse(scrapeMatch).map{ fetched =>
          val result = fetched.filter(_.tidspunkt.isBefore(LocalDateTime.now)) match {
            case h :: t => h :: t.takeWhile(_.tidspunkt.getYear == LocalDate.now.getYear)
            case x => x
          }
          RefereeStats(result,uri)
        }
    }
  }

}

object RefereeScraper {
  val log:Logger = LoggerFactory.getLogger(getClass)

  def parseMatches(fiksId:Int, document:Document) : (String, List[String]) = {
    val body = document.body()
    val refName = body.select(".fiks-header--person").select("h1 > a").text()
    val dommerRader = body.select("tr").asScala.filter(_.select("td").asScala.exists(_.text() == "Dommer"))
    val urls = dommerRader.flatMap(_.select("td > a").asScala.map(_.attr("href")))
      .filter(_.contains("/kamp/")).toList
    log.info(s"Scraping referee named $refName")
    (refName, urls)
  }

  def parseMatch(matchUri:Uri, kampDoc:Document) = {
    val lag = kampDoc.select("span.match__teamname-img").asScala.map(_.nextElementSibling().text())
    val home = lag.head
    val away = lag.drop(1).head
    val dateText = kampDoc.select("span.match__arenainfo-date").text()
    val tidspunkt = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm"))
    val matchEvents = kampDoc.select("ul.match__events")
    val yellows = matchEvents.select("span.icon-yellow-card--events")
    val yellowReds = matchEvents.select("span.icon-yellow-red-card--events")
    val reds = matchEvents.select("span.icon-red-card--events")
    MatchStat(matchUri, tidspunkt, home, away, CardStat(yellows.size(),yellowReds.size(),reds.size()))
  }


}

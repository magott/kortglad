package kortglad.scraper

import java.net.URI
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kortglad.data.{CardStat, MatchStat}
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
/**
  *
  */
object RefereeScraper {

  val fotballBaseUrl = "https://www.fotball.no"
  val refereeTemplate = s"${fotballBaseUrl}/fotballdata/person/dommeroppdrag/?fiksId=%s"

  def scrapeMatches(fiksId:Int) : (String, List[URI]) = {
    val document = Jsoup.connect(refereeTemplate.format(fiksId)).get()

    val body = document.body()
    val refName = body.select(".fiks-header--person").select("h1 > a").text()
    val dommerRader = body.select("tr").asScala.filter(_.select("td").asScala.exists(_.text() == "Dommer"))
    val urls = dommerRader.flatMap(_.select("td > a").asScala.map(_.attr("href")))
      .filter(_.contains("/kamp/")).toList

    (refName, urls.map(x => URI.create(fotballBaseUrl + x)))
  }

  def scrapeMatch(matchUri: URI) = {

    val kampDoc = Jsoup.connect(matchUri.toString).get()
    val lag = kampDoc.select("span.match__teamname-img").asScala.map(_.nextElementSibling().text())
    val home = lag.head
    val away = lag.drop(1).head
    val dateText = kampDoc.select("span.match__arenainfo-date").text()
    val tidspunkt = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    val matchEvents = kampDoc.select("ul.match__events")
    val yellows = matchEvents.select("span.icon-yellow-card--events")
    val yellowReds = matchEvents.select("span.icon-yellow-red-card--events")
    val reds = matchEvents.select("span.icon-red-card--events")
    MatchStat(matchUri, tidspunkt, home, away, CardStat(yellows.size(),yellowReds.size(),reds.size()));
  }


}

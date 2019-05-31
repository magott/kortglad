package scraper

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import kortglad.data.{CardStat, RefereeStats}
import kortglad.scraper.RefereeScraper
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger

import scala.concurrent.ExecutionContext

/**
  *
  */
object RefereeScraperTest extends IOApp{

  private val morten = 2245443
  private val skjevis = 2552240
  private val gjermshus = 2316320
  private val trædal = 2922378

  def dump(stats:RefereeStats) = IO{
    println("Totalt: " +stats.cardTotals.pretty)
    println(stats.cardAverages.snittPretty)
    println(s"Antall kamper: ${stats.matches.length}")
  }

  def log(s:String) = IO(println(s))

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
      val scraper = new RefereeScraper(Logger(true, true, logAction = Some(log _))(client))
      scraper.findRefereeStats(morten).flatMap(dump).as(ExitCode.Success)
    }


//  println(RefereeScraper.scrapeMatch("/fotballdata/kamp/?fiksId=6653689"))
}

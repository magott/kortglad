package scraper

import kortglad.data.{CardStat, RefereeStats}
import kortglad.scraper.RefereeScraper

/**
  *
  */
object RefereeScraperTest extends App{

  private val morten = 2245443
  private val skjevis = 2552240
  private val gjermshus = 2316320
  private val trædal = 2922378

  private val matches = RefereeScraper.scrapeMatches(morten)
  private val matchStats = matches._2.par.map(RefereeScraper.scrapeMatch)
  private val totalt = matchStats.toList
  private val refereeStats = new RefereeStats(totalt,"")
  println("Totalt: " +refereeStats.cardTotals.pretty)
  println(refereeStats.cardAverages.snittPretty)
  println(s"Antall kamper: ${matchStats.length}")

//  println(RefereeScraper.scrapeMatch("/fotballdata/kamp/?fiksId=6653689"))
}

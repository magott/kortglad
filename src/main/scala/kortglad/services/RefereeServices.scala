package kortglad.services

import java.net.URI

import kortglad.data.RefereeStats
import kortglad.scraper.RefereeScraper

/**
  *
  */
class RefereeServices {

  def findRefereeMatches(fiksId: Int) : (String, List[URI]) = {
    RefereeScraper.scrapeMatches(fiksId)
  }

  def findRefereeStats(fiksId: Int) : RefereeStats = {
    val refereeMatches = findRefereeMatches(fiksId)

    val matches = refereeMatches._2.par.map(RefereeScraper.scrapeMatch)
    new RefereeStats(matches.toList,refereeMatches._1)
  }

}

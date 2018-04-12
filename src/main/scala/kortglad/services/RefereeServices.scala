package kortglad.services

import java.net.URI
import java.time.{LocalDate, LocalDateTime}

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
      .filter(_.tidspunkt.isBefore(LocalDateTime.now)).toList match {
      case h :: t => h :: t.takeWhile(_.tidspunkt.getYear == LocalDate.now.getYear)
      case x => x
    }

    new RefereeStats(matches,refereeMatches._1)
  }

}

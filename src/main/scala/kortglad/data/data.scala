package kortglad.data

import java.net.URI
import java.text.DecimalFormat
import java.time.{LocalDate, LocalDateTime, Month, Year}

import io.circe.Encoder
import io.circe.generic.JsonCodec
import org.http4s.Uri

case class RefereeStats(matches:List[MatchStat], refereeName:String){
  val cardTotals:CardStat = matches.map(_.cards).foldLeft(CardStat.empty)(_+_)
  val cardAverages:CardAvarages = CardAvarages.from(cardTotals, matches.size)
  val totalMatches = matches.size

}

object RefereeStats {
  import io.circe.Encoder
  import io.circe.generic.auto._

  implicit val f: Encoder[RefereeStats] =
    Encoder.forProduct5("refereeName", "totals", "averages", "numMatches", "matches")(rs => (rs.refereeName, rs.cardTotals, rs.cardAverages,rs.totalMatches, rs.matches))
}

case class MatchStat(url:Uri, tidspunkt: LocalDateTime, home:String, away:String, cards:CardStat){
  def inCurrentSeason : Boolean = MatchStat.thisSeason(tidspunkt)
}
object MatchStat{
  implicit val matchStatEncoder: Encoder[MatchStat] =
    Encoder.forProduct5("kickoff", "home", "away", "url", "cardStats")(rs => (rs.tidspunkt.toString, rs.home, rs.away, rs.url.toString, rs.cards))

  def seasonYear = if(LocalDateTime.now().isBefore(LocalDateTime.now().withMonth(Month.MARCH.getValue))) Year.now().minusYears(1) else Year.now()
  def seasonStart = seasonYear.atMonth(Month.MARCH).atDay(1).atStartOfDay()
  def seasonEnd = seasonYear.plusYears(1).atMonth(Month.MARCH).atDay(1).atStartOfDay()
  def thisSeason: LocalDateTime => Boolean = d => d.isBefore(seasonEnd) && d.isAfter(seasonStart)

}


//@JsonCodec
case class CardAvarages(yellow:Double, yellowToRed:Double, red:Double){
  val formatter = new DecimalFormat("0.00")
  def snittPretty = s"Snitt: Gule ${formatter.format(yellow)}, Gult nr 2: ${formatter.format(yellowToRed)}, Røde ${formatter.format(red)}"
}

object CardAvarages{
  def from(totals: CardStat, matches:Int) = {
    import totals._
    CardAvarages(yellow.toDouble/matches, yellowToRed.toDouble/matches, red.toDouble/matches)
  }

}
//@JsonCodec
case class CardStat(yellow:Int, yellowToRed:Int, red:Int){

  def pretty = s"Gule $yellow, Gult nr 2: $yellowToRed, Røde $red"

  def + (cardStat: CardStat): CardStat ={
    CardStat(yellow+cardStat.yellow, yellowToRed+cardStat.yellowToRed, red+cardStat.red)
  }

}
object CardStat{
  def empty = new CardStat(0,0,0)
  def totals(cardStats: List[CardStat]) = cardStats.foldLeft(empty)(_+_)
  implicit val cardStatEncoder: Encoder[CardStat] =
    Encoder.forProduct3("yellow", "yellowToRed", "red")(c => (c.yellow, c.yellowToRed, c.red))

}


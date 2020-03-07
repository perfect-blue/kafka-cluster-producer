import br.com.thiagomoreira.foursquare.Foursquare
import br.com.thiagomoreira.foursquare.model.Venue

object ForsquareProducer {

  def writeFromForSquare()={
    val clientId:String=""
    val clientSecret:String=""

    val forsquare:Foursquare = new Foursquare(clientId,clientSecret)
    val venue=forsquare.getVenue("4ef0e7cf7beb5932d5bdeb4e")

    println(venue.getId)
    println(venue.getName)

  }
}

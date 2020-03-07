import br.com.thiagomoreira.foursquare.Foursquare
import br.com.thiagomoreira.foursquare.model.Venue

object ForsquareProducer {

  def writeFromForSquare()={
    val clientId:String="3E1JPAVQJDCBX0SF3BMKNSS1SS1OAZYAWVZ4QYNK2TLLDZK3"
    val clientSecret:String="IIL0SCGN53GSJDE2CZDZ0J3KV0S1MN15QMSKYGGM2EQISVMV"

    val forsquare:Foursquare = new Foursquare(clientId,clientSecret)
    val venue=forsquare.getVenue("4ef0e7cf7beb5932d5bdeb4e")

    println(venue.getId)
    println(venue.getName)

  }
}

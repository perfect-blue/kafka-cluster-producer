import facebook4j.Post.Property
import facebook4j.{Facebook, FacebookFactory}

object FacebookProducer {

  def facebookConfiguration(property: Property, topic:String)={
    val appId=""
    val appSecret=""
    val permission=""
    val token=""

    val facebook:Facebook = new FacebookFactory().getInstance()
    facebook.setOAuthAppId(appId,appSecret)

    val posts = facebook.searchPosts("trump")
    val places = facebook.searchPlaces("coffee")

  }


}

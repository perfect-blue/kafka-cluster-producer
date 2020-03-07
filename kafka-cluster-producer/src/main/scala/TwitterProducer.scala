import java.util
import java.util.Properties
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue, TimeUnit}

import com.google.common.collect.Lists
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.{Client, Constants, Hosts, HttpHosts}
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.{Authentication, OAuth1}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConverters._

object TwitterProducer {
  def twitterSetup(terms: util.List[String],msgQueue:BlockingQueue[String]):Client={
    val consumerKey: String = ""
    val consumerSecret: String = ""
    val token: String = ""
    val secret: String = ""

    val twitterHosts:Hosts = new HttpHosts(Constants.STREAM_HOST)
    val twitterEndpoint = new StatusesFilterEndpoint()
    // Optional: set up some followings and track terms

    val followings= Lists.newArrayList(1234L,566788L)

    twitterEndpoint.followings(followings.asScala.map(long2Long).asJava)
    twitterEndpoint.trackTerms(terms)

    val twitterAuth:Authentication = new OAuth1(consumerKey,consumerSecret,token,secret)

    val builder: ClientBuilder = new ClientBuilder()
      .name("twitter-client-01")
      .hosts(twitterHosts)
      .authentication(twitterAuth)
      .endpoint(twitterEndpoint)
      .processor(new StringDelimitedProcessor(msgQueue))

    val twitterClient : Client = builder.build()

    twitterClient
  }

  def writeFromTwitter(property:Properties,topic:String):Unit={
    val msgQueue = new LinkedBlockingQueue[String](100000)
    val terms: util.List[String] = Lists.newArrayList("trump","america")

    //create twitter client
    val client: Client = twitterSetup(terms,msgQueue)
    client.connect()

    //create producer
    val producer = new KafkaProducer[String,String](property)

    while(!client.isDone){
      var msg:String = null
      try msg = msgQueue.poll(5, TimeUnit.SECONDS)
      catch {
        case e: InterruptedException =>
          e.printStackTrace()
          client.stop()
      }

      if(msg!=null){
        producer.send(new ProducerRecord[String,String](topic,msg))
      }
    }
  }


}

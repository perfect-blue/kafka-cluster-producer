import java.util
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue, TimeUnit}
import java.util.{List, Properties}

import com.google.common.collect.Lists
import org.apache.kafka.clients.producer._

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.core.{Client, Constants, Hosts, HttpHosts}
import com.twitter.hbc.httpclient.auth.{Authentication, OAuth1}

import scala.collection.JavaConverters._

object KafkaBasicProducer {


  def main(args: Array[String]): Unit = {
    //create producer properties
    val bootstrapserver:String = "127.0.0.1:9092"
    val keySerializer:String="org.apache.kafka.common.serialization.StringSerializer"
    val valueSerializer:String="org.apache.kafka.common.serialization.StringSerializer"

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapserver)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,keySerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,valueSerializer)

    //create safe producer
    props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true")
    props.setProperty(ProducerConfig.ACKS_CONFIG, "all")
    props.setProperty(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE))
    props.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5")

    //high throughput producer(at the expense of a bit of latency and CPU usage
    props.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy")
    props.setProperty(ProducerConfig.LINGER_MS_CONFIG,"20")
    props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024))

    //writeToKafka(props,"scala-first-topic")

    writeFromTwitter(props,"scala-twitter")

  }


  def writeToKafka(property:Properties,topic:String):Unit={

    //create producer
    val producer:KafkaProducer[String,String]= new KafkaProducer[String,String](property)

    for(i<-0 to 10){
      //create producer record
      val value:String="one perfect souls "+Integer.toString(10-i)
      val key: String="id_" +Integer.toString(i)
      val record:ProducerRecord[String,String] = new ProducerRecord[String,String](topic,value)

      //send data
      producer.send(record)

    }


    //flush and close data
    producer.flush()
    producer.close()

  }

  def twitterSetup(terms: util.List[String],msgQueue:BlockingQueue[String]):Client={
    val consumerKey: String = "DjBtGDEVWtU29OcMieDUN8L1r"
    val consumerSecret: String = "tVUGYm5j6PzrsOaMO0kVHHajOxlwwDxiWFPbbTLvkowcwfXQ7P"
    val token: String = "3321061040-OXVrtcLVilqBQUIMDnvlMs0wh4bO2MWRdthcAA9"
    val secret: String = "E86TWY1XNoVpecpld15lN6hEXIGQjeTF5FBJdQK36mlhg"

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
    val terms: util.List[String] = Lists.newArrayList("oscars")

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

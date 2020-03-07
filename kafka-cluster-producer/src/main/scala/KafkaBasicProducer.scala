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

import ForsquareProducer._
import scala.collection.JavaConverters._
import StockProducer._
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

    //writeFromTwitter(props,"scala-twitter")
    //writeFrromFacebook(props,"facebook-topics")

    //writeTimeSeriesDaily(props,"time_series")
    writeFromForSquare()

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



}

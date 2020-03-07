import java.util
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.patriques.input.timeseries.{Interval, OutputSize}
import org.patriques.output.AlphaVantageException
import org.patriques.output.timeseries.IntraDay
import org.patriques.output.timeseries.data.StockData
import org.patriques.{AlphaVantageConnector, TimeSeries}
import scala.collection.JavaConversions._

object StockProducer {

  val apiKey=""


  //Time Series Daily
  def writeTimeSeriesDaily(property:Properties,topic:String):Unit={
    val timeout:Int = 50000000

    val apiConnector:AlphaVantageConnector = new AlphaVantageConnector(apiKey,timeout)
    val stockTimeSeries:TimeSeries = new TimeSeries(apiConnector)
    val producer:KafkaProducer[String,String]= new KafkaProducer[String,String](property)

    try{
      val response_daily = stockTimeSeries.daily("MSFT")
      val metadata =response_daily.getMetaData()
      println("Information: "+metadata.get("1. Information"))
      println("Stock: "+metadata.get("2.Symbol"))


      val listData = response_daily.getStockData
      val stockData = listData.toSeq

      for(stock <-stockData){
        println("date: "+stock.getDateTime)
        println("open: "+stock.getOpen)
        println("high: "+stock.getHigh)
        println("low: "+stock.getLow)
        println("close: "+stock.getClose)
        println("volume: "+stock.getVolume)
        var value=""+stock.getDateTime+" "+
          stock.getOpen+" "+
          stock.getHigh+" "+
          stock.getLow+" "+
          stock.getClose+" "+
          stock.getVolume
        val record:ProducerRecord[String,String] = new ProducerRecord[String,String](topic,value)
        producer.send(record)
      }

      producer.flush()
      producer.close()

    }catch{
      case e: Exception => println("Something went wrong")
    }
  }
  //Foreign Exchange

  //Crypto curencies

  //Technical Indicator

  //sector performance

  //writeToKafka

}

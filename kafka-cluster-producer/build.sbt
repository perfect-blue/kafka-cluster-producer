name := "kafka-cluster-producer"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.3.0",
  "com.twitter" % "hbc-core" % "2.2.0",
  "org.slf4j" % "slf4j-simple" % "1.7.26",
  "io.github.mainstringargs" % "alpha-vantage-scraper" % "1.1",
  "org.facebook4j" % "facebook4j-core" % "2.4.10",
  "br.com.thiagomoreira.foursquare" % "foursquare4j" % "1.0.0"

)

package com.atm.service.streaming.handlers
import java.util.Properties
import java.util.concurrent.CountDownLatch
import java.util.logging.Logger

import com.atm.service.streaming.AppConstant
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.processor.{Processor, ProcessorSupplier}
import org.apache.kafka.streams.{
  KafkaStreams,
  StreamsBuilder,
  StreamsConfig,
  Topology
}

object TransactionHandler {
  private val LOGGER =
    Logger.getLogger(classOf[TransactionHandler].getSimpleName)
}

/***
  * It is handler which setup the Kafka Streaming topology. It means topic to processor mapping.
  * It defines routing of the kafka message.
  */
final class TransactionHandler {

  def startPipeline(): KafkaStreams = {
    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "atm-app")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(
      StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
      Serdes.String.getClass
    )
    props.put(
      StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
      Serdes.String.getClass
    )
    val topology = processingTopologyBuilder
    val streams = new KafkaStreams(topology, props)
    val latch = new CountDownLatch(1)
    Runtime.getRuntime.addShutdownHook(new Thread("atm-app-shutdown-hook") {
      override def run(): Unit = {
        streams.close()
        latch.countDown()
      }
    })
    try {
      streams.start()
      latch.await()
    } catch {
      case e: Throwable =>
        TransactionHandler.LOGGER.warning(
          "Unable to start Kafka Streams.. exiting"
        )
        System.exit(1)
    }
    streams
  }
  private def processingTopologyBuilder: Topology = {
    val builder = new StreamsBuilder
    val source: KStream[String, String] = builder.stream(AppConstant.TOPIC_NAME)
    source.process(new ProcessorSupplier[String, String]() {
      override def get: Processor[String, String] = new TransactionProcessor
    })
    TransactionHandler.LOGGER.info("Kafka streams processing topology ready")
    builder.build
  }
}

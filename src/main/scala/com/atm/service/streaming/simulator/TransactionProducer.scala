package com.atm.service.streaming.simulator
import java.util.{Properties, Random}
import java.util.logging.{Level, Logger}

import com.atm.service.streaming.AppConstant
import com.atm.service.streaming.model.TRANSACTION_TYPES.{CREDIT, DEBIT}
import com.atm.service.streaming.model.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.clients.producer._

object TransactionProducer {
  private val LOGGER = Logger.getLogger(classOf[Producer[_, _]].getName)
  def main(args: Array[String]): Unit = {
    System.out.println("Starting producer ...")
    new Thread(new TransactionProducer).start()
    System.out.println("Started producer")
  }
}

/***
  * It is simulator which generates debit and credit transactions for configured accounts.
  * It can also generate load for testing.
  */
class TransactionProducer() extends Runnable {

  final private val KAFKA_CLUSTER_ENV_VAR_NAME = "KAFKA_CLUSTER"
  val kafkaProps = new Properties
  val defaultClusterValue = "localhost:9092"
  TransactionProducer.LOGGER.log(Level.INFO, "Kafka cluster {0}", kafkaCluster)
  kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster)
  kafkaProps.put(
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer"
  )
  kafkaProps.put(
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer"
  )
  kafkaProps.put(ProducerConfig.ACKS_CONFIG, "0")
  val kafkaCluster: String =
    System.getProperty(KAFKA_CLUSTER_ENV_VAR_NAME, defaultClusterValue)
  val kafkaProducer = new KafkaProducer[String, String](kafkaProps)
  val jsonMapper = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    m
  }

  override def run(): Unit = {
    try produce()
    catch {
      case e: Exception =>
        TransactionProducer.LOGGER.log(Level.SEVERE, e.getMessage, e)
    }
  }

  /**
    * produce messages
    *
    * @throws Exception
    */ @throws[Exception]
  private def produce(): Unit = {
    try {
      val rnd = new Random
      val accounts = Array("acc-1", "acc-2", "acc-3", "acc-4", "acc-5")
      var isCredit = true
      val creditAmount = 100d
      val debitAmount = 50d
      while ({ true }) {
        for (acc <- accounts) {
          // Kafka topic key is account number, so that all transactions of that account go to same partition.
          // WIth above kafka functionality, we can easily maintained the order of transactions. Kafka provides the message ordering by default.
          val key = acc
          val transaction = Transaction(
            "accholder-" + acc,
            acc,
            if (isCredit) CREDIT else DEBIT,
            if (isCredit) creditAmount
            else debitAmount,
            System.currentTimeMillis
          )
          val strTransaction = jsonMapper.writeValueAsString(transaction)
          System.out.println("Transaction:" + strTransaction)
          val record = new ProducerRecord[String, String](
            AppConstant.TOPIC_NAME,
            key,
            strTransaction
          )
          kafkaProducer.send(
            record,
            new Callback() {
              override def onCompletion(
                  rm: RecordMetadata,
                  excptn: Exception
              ): Unit = {
                if (excptn != null)
                  TransactionProducer.LOGGER.log(
                    Level.WARNING,
                    "Error sending message with key {0}\n{1}",
                    Array[AnyRef](key, excptn.getMessage)
                  )
                else
                  TransactionProducer.LOGGER.log(
                    Level.INFO,
                    s"Partition: ${rm.partition} for " +
                      s"partition key: ${key} value: ${strTransaction}"
                  )
              }
            }
          )
          Thread.sleep(5000)
        }
        isCredit = !isCredit
      }
    } catch {
      case e: Exception =>
        TransactionProducer.LOGGER
          .log(Level.SEVERE, "Producer thread was interrupted")
    } finally {
      kafkaProducer.close()
      TransactionProducer.LOGGER.log(Level.INFO, "Producer closed")
    }
  }
}

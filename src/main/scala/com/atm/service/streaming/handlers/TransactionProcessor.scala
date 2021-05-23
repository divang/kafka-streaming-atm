package com.atm.service.streaming.handlers
import java.util.logging.{Level, Logger}

import com.atm.service.streaming.db.MockedDataBase
import com.atm.service.streaming.model.TRANSACTION_TYPES.{CREDIT, DEBIT}
import com.atm.service.streaming.model.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.kafka.streams.processor.{Processor, ProcessorContext}

object TransactionProcessor {
  private val PROC_LOGGER =
    Logger.getLogger(classOf[TransactionProcessor].getSimpleName)
}

/***
  * It is processor which takes care of transactions processing.
  */
class TransactionProcessor() extends Processor[String, String] {
  val jsonMapper = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
    m
  }
  private val dataBaseInstance = new MockedDataBase
  override def init(pc: ProcessorContext): Unit = {
    TransactionProcessor.PROC_LOGGER.info("Processor initialized")
  }

  /**
    * Kafka topic key is account number, so that all transactions of that account go to same partition.
    * With above kafka functionality, we can easily maintained the order of transactions. Kafka
    * provides the message ordering by default.
    *
    * @param accountNumber
    * @param transaction
    */
  override def process(accountNumber: String, transaction: String): Unit = {
    TransactionProcessor.PROC_LOGGER.log(
      Level.INFO,
      "Processing transaction: {0}",
      transaction
    )
    try {
      val t = jsonMapper.readValue(transaction, classOf[Transaction])

      t.transactionType match {
        case CREDIT =>
          dataBaseInstance.credit(t.accountNo, t.amount)
        case DEBIT =>
          dataBaseInstance.debit(t.accountNo, t.amount)
      }
      TransactionProcessor.PROC_LOGGER.log(
        Level.INFO,
        s"Account number:${t.accountNo} Balance " +
          s": ${dataBaseInstance.getBalance(t.accountNo)}"
      )
    } catch {
      case e: Exception =>
        TransactionProcessor.PROC_LOGGER.log(
          Level.SEVERE,
          "Error while processing trasaction: ",
          e
        )
    }
  }
  override def close(): Unit = {
    TransactionProcessor.PROC_LOGGER.warning("Closing processor...")
  }
}

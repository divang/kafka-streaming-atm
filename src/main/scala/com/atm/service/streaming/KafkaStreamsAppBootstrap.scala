package com.atm.service.streaming
import java.io.IOException
import java.util.logging.Logger

import com.atm.service.streaming.handlers.TransactionHandler

/***
  * It is driver class which invokes the Kafka Stream building.
  */
object KafkaStreamsAppBootstrap {
  private val LOGGER =
    Logger.getLogger(KafkaStreamsAppBootstrap.getClass.getName)

  /**
    * Entry point
    *
    * @param args
    * @throws Exception
    */ @throws[Exception]
  def main(args: Array[String]): Unit = { bootstrap() }

  @throws[IOException]
  private def bootstrap(): Unit = {
    val theStream = new TransactionHandler().startPipeline
    GlobalAppState(theStream)
  }
}

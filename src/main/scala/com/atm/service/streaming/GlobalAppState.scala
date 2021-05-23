package com.atm.service.streaming
import org.apache.kafka.streams.KafkaStreams

/***
  * It holds kafka stream instance
  * @param ks
  */
case class GlobalAppState(ks: KafkaStreams)

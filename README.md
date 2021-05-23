# kafka-streaming-atm


- Setup kafka via https://kafka.apache.org/quickstart
- Git clone this repo
- cd kafka-streaming-atm
- Build repo: sbt clean assembly 
- Run local ATM Streaming App: java -cp target/scala-2.13/atm-1.0.jar com.atm.service.streaming.KafkaStreamsAppBootstrap
- Run Producer: java -cp target/scala-2.13/atm-1.0.jar com.atm.service.streaming.simulator.TransactionProducer

- Build docker image: docker build -t atm-scala/1.0 .
- Run ATM Streaming App in docker container: docker run --rm --name consumer-atm-1 -it -P -e KAFKA_BROKER=192.168.101.3:9092 -e ZOOKEEPER=192.168.101.3:2181 atm-scala/1.0

Doc id si iv* olanlar kafka topic e yazilacak

### Create input and output topics
```bash
bin/kafka-topics --bootstrap-server localhost:9092 --create --topic flink_input --partitions 1 --replication-factor 1
```
```bash
bin/kafka-topics --bootstrap-server localhost:9092 --create --topic flink_output --partitions 1 --replication-factor 1
```
### Alter input topic and observe `partition.discovery.interval.ms` property
to discover new partitions per 10 seconds. Without this config, if new partition is added, 
data on that partition can not be read.
```bash
bin/kafka-topics --bootstrap-server localhost:9092 --topic flink_input --alter --partitions 2
```
### Send and consume data from input and output topics
```bash
bin/kafka-console-producer --broker-list localhost:9092 --topic flink_input --producer-property max.request.size=10485880
```
```bash
bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic flink_output --from-beginning
```
### CHECK
1.) I could not see consumer offset in the __consumer_offsets topic. Although below line, it always reads from beginning
.setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
flink checkpoint?

2.) For more than one partition, is ti requied to do more like using setParallelism? 

3.) Boundedness



### REFERENCES
https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/#deserializer

https://www.baeldung.com/kafka-flink-data-pipeline

https://medium.com/@vndhya/apache-flink-streaming-kafka-events-in-json-format-complete-sample-code-in-java-70372d62f61
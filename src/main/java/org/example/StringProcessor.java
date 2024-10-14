package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 *
 */
public class StringProcessor
{
    private static final Logger log = LoggerFactory.getLogger(StringProcessor.class);
    public static void main( String[] args ) throws Exception {

        System.out.println("sdcs");
        log.info("dsd");
        capitalize();
    }
    public static void capitalize() throws Exception {
        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        String consumerGroup = "my-flink-example";
        String address = "152.70.188.57:9092";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
                .getExecutionEnvironment();
        KafkaSource<String> flinkKafkaConsumer = createStringConsumerForTopic(
                inputTopic, address, consumerGroup);
        DataStreamSource<String> stringInputStream = environment.fromSource(flinkKafkaConsumer, WatermarkStrategy.noWatermarks(), "kafka-input");

        KafkaSink<String> flinkKafkaProducer = createStringProducer(
                outputTopic, address);

        System.out.println("Start processing");
        stringInputStream
                .map(new WordsCapitalizer())
                .sinkTo(flinkKafkaProducer)
                .name("kafka-output");

        environment.execute();
    }
    public static KafkaSource<String> createStringConsumerForTopic(
            String topic, String kafkaAddress, String kafkaGroup ) {

        return KafkaSource.<String>builder()
                .setBootstrapServers(kafkaAddress)
                .setTopics(topic)
                .setGroupId(kafkaGroup)
                //.setStartingOffsets(OffsetsInitializer.earliest())
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                //.setValueOnlyDeserializer(new SimpleStringSchema())
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class))
                .setProperty("partition.discovery.interval.ms", "10000")
                .build();

    }
    public static KafkaSink<String> createStringProducer(
            String topic, String kafkaAddress){

        return KafkaSink.<String>builder()
                .setBootstrapServers(kafkaAddress)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}

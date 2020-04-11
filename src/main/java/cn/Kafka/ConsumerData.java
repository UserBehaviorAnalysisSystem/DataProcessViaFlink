package cn.Kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

public class ConsumerData {
    private Properties properties;
    private Consumer<String, String> consumer = null;

    public ConsumerData(){
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "localhost:9092");
        prop.put("group.id", "group-1");
        prop.put("enable.auto.commit", "true");
        prop.put("auto.commit.interval.ms", "1000");
        prop.put("auto.offset.reset", "earliest");
        //prop.put("session.timeout.ms", "30000");
        prop.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.properties = prop;
    }

    public void pullData() throws Exception{
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("test"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, value = %s", record.offset(), record.value());
                System.out.println();
            }
        }
    }

    // return the first string value
    public String pullDataForTest() throws Exception{
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("test"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if(records.count() > 0){
                Iterator<ConsumerRecord<String, String>> it = records.iterator();
                ConsumerRecord<String, String> record = it.next();
                return record.value();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        ConsumerData consumer = new ConsumerData();
        consumer.pullData();
    }
}

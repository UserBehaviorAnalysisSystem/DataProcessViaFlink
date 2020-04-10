package cn.Kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerData {
    private Properties properties;
    private Producer<String, String> producer = null;
    private static final String topic = "test";

    public ProducerData() throws Exception{
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "localhost:9092");
        prop.put("acks", "all");
        prop.put("retries", 0);
        prop.put("batch.size", 16384);
        prop.put("linger.ms", 1);
        /*prop.put("buffer.memory", 33554432);*/
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.properties = prop;
    }


    public void sendMessage(String message){
        // create a new producer
        producer = new KafkaProducer<String, String>(this.properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        // send message
        producer.send(record);

        producer.close();
    }

    public static void main(String[] args) throws Exception{
        ProducerData driver = new ProducerData();
        driver.sendMessage("zztttt1");
        driver.sendMessage("zztttt2");
        driver.sendMessage("zztttt3");
    }
}

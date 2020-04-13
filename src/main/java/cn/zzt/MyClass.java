package cn.zzt;

import cn.Kafka.JsonHelper;
import cn.Kafka.SingleMessage;
import cn.WatermarkFunction.assignTimestampsAndWatermarks;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;


import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.Properties;

public class MyClass {
    public StreamExecutionEnvironment env = null;
    //public DataStream<UserBehavior> dataSource = null;

    public MyClass() throws Exception{
        init();
        //createCsvDataSource();
    }

    private void init(){
        env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // set checkpoint config
        CheckpointConfig config = env.getCheckpointConfig();
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.enableCheckpointing(12000);// ms
        //env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        // set EventTime
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    }

    public DataStream<UserBehavior> createCsvDataSource() throws Exception{
        // create data source
        //URL fileUrl = MyClass.class.getClassLoader().getResource("UserBehavior.csv");
        URL fileUrl = MyClass.class.getClassLoader().getResource("out/out1.csv");
        Path filePath = Path.fromLocalFile(new File(fileUrl.toURI()));

        // 抽取 UserBehavior 的 TypeInformation，是一个 PojoTypeInfo
        PojoTypeInfo<UserBehavior> pojoType = (PojoTypeInfo<UserBehavior>) TypeExtractor.createTypeInfo(UserBehavior.class);

        // 由于 Java 反射抽取出的字段顺序是不确定的，需要显式指定下文件中字段的顺序
        String[] fieldOrder = new String[]{"userId", "itemId", "categoryId", "behavior", "timestamp"};

        // 创建 PojoCsvInputFormat
        PojoCsvInputFormat<UserBehavior> csvInput = new PojoCsvInputFormat<>(filePath, pojoType, fieldOrder);

        DataStreamSource<UserBehavior> data = env.createInput(csvInput, pojoType);

        DataStream<UserBehavior> timedData = data.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
            @Override
            public long extractAscendingTimestamp(UserBehavior userBehavior) {
                // 原始数据单位秒，将其转成毫秒
                return userBehavior.timestamp * 1000;
            }
        });

        return timedData;
    }

    public DataStreamSource<String> createKafkaDataSource() throws Exception{
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "flink-group");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("testForFlink5", new SimpleStringSchema(), props);
        consumer.setStartFromLatest();
        // add watermark
        consumer.assignTimestampsAndWatermarks(new assignTimestampsAndWatermarks());

        DataStreamSource<String> dataStreamSource = this.env.addSource(consumer);
        /*dataStreamSource
                .map((MapFunction<String, SingleMessage>) s -> JsonHelper.parse(s))
                .print();
        */
        return dataStreamSource;
    }

    public DataStream<UserBehavior> filterPv() throws Exception{
        return createCsvDataSource().filter(new FilterFunction<UserBehavior>() {
            @Override
            public boolean filter(UserBehavior userBehavior) throws Exception {
                // 过滤出只有点击的数据
                return userBehavior.behavior.equals("pv");
            }
        });
    }

    public void printAll() throws Exception {
        createCsvDataSource().keyBy("userId")
                .map((MapFunction<UserBehavior, String>) ele -> ele.toString())
                .print();
        env.execute("print");
    }

    public static void main(String[] args) throws Exception{
        MyClass m = new MyClass();
        DataStreamSource<String> dataStreamSource = m.createKafkaDataSource();
        dataStreamSource
                .map((MapFunction<String, SingleMessage>) s -> JsonHelper.parse(s))
                .print();
        m.env.execute("flink kafka consumer");
    }
}

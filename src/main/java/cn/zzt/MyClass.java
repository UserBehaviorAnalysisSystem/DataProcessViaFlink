package cn.zzt;

import cn.Kafka.JsonHelper;
import cn.Kafka.SingleMessage;
import cn.Kafka.StreamingJob;
import cn.WatermarkFunction.assignSingleMessageTimestampsAndWatermarks;
import cn.WatermarkFunction.assignUserBehaviorTimestampAndWatermarks;
import cn.WindowFunction.MyclassProcessWindowFunction;
import cn.WindowFunction.ProcessCountUser;
import cn.tmp.HotItems;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;


import java.io.File;
import java.net.URL;
import java.util.HashSet;
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

    public DataStream<UserBehavior> createCsvDataSource(String src) throws Exception{
        // create data source
        //URL fileUrl = MyClass.class.getClassLoader().getResource("UserBehavior.csv");
        URL fileUrl = MyClass.class.getClassLoader().getResource("final\\rawData.csv");
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
                return userBehavior.getTimestamp() * 1000;
            }
        });

        return timedData;
    }

    public DataStreamSource<String> createKafkaDataSource() throws Exception{
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "flink-group");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("testForFlink9", new SimpleStringSchema(), props);
        consumer.setStartFromLatest();
        // add watermark
        //consumer.assignTimestampsAndWatermarks(new assignSingleMessageTimestampsAndWatermarks());
        consumer.assignTimestampsAndWatermarks(new assignUserBehaviorTimestampAndWatermarks());

        DataStreamSource<String> dataStreamSource = this.env.addSource(consumer);
        /*dataStreamSource
                .map((MapFunction<String, SingleMessage>) s -> JsonHelper.parse(s))
                .print();
        */
        return dataStreamSource;
    }

    public DataStream<UserBehavior> filterPv() throws Exception{
        return createCsvDataSource("UserBehavior.csv").filter(new FilterFunction<UserBehavior>() {
            @Override
            public boolean filter(UserBehavior userBehavior) throws Exception {
                // 过滤出只有点击的数据
                return userBehavior.getBehavior().equals("pv");
            }
        });
    }

    public void printAll() throws Exception {
        createCsvDataSource("UserBehavior.csv").keyBy("userId")
                .map((MapFunction<UserBehavior, String>) ele -> ele.toString())
                .print();
        env.execute("print");
    }
    /* WindowFunction<IN, OUT, KEY, W extends Window> */
    public static class windowFunction implements WindowFunction<Tuple2<String, Long>, Tuple2<String, Long>, Tuple, TimeWindow>{
        @Override
        public void apply(Tuple tuple,
                          TimeWindow window,
                          Iterable<Tuple2<String, Long>> input,
                          Collector<Tuple2<String, Long>> out) throws Exception{
            long sum = 0L;
            // just one element
            Tuple2<String, Long> ret = input.iterator().next();

            for(Tuple2<String, Long> cur: input){
                sum += cur.f1;
            }
            ret.f1 = sum;
            out.collect(ret);
        }
    }
    public static void main(String[] args) throws Exception{
        MyClass m = new MyClass();
        DataStreamSource<String> dataStreamSource = m.createKafkaDataSource();
        dataStreamSource
                /*.flatMap((FlatMapFunction<String, Tuple2<String, Long>>) (s, collector) -> {
                    UserBehavior userBehavior = UserBehavior.parse(s);
                    if(userBehavior != null){
                        collector.collect(new Tuple2<>(String.valueOf(userBehavior.getUserId()), 1L));
                    }
                })
                .returns(TypeInformation.of(new TypeHint<Tuple2<String, Long>>(){}))
                .keyBy(0)
                .timeWindow(Time.seconds(5), Time.seconds(5))
                .apply(new MyClass.windowFunction())
                .print();*/

                //.timeWindowAll(Time.seconds(5), Time.seconds(2))
                //.process(new MyclassProcessWindowFunction())
                //.print();
                /*.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<String>() {
                    @Override
                    public long extractAscendingTimestamp(String s) {
                        // 原始数据单位秒
                        UserBehavior userBehavior = UserBehavior.parse(s);
                        return userBehavior.getTimestamp();
                    }
                })*/
                .map((MapFunction<String, UserBehavior>) s -> UserBehavior.parse(s))
                .timeWindowAll(Time.seconds(5), Time.seconds(2))
                .process(new ProcessCountUser())
                .print();
        m.env.execute("flink kafka consumer");
    }
}

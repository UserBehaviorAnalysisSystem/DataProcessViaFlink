package cn.Kafka;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;
import java.util.Properties;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000); // 要设置启动检查点
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "flink-group");

        //数据源配置，是一个kafka消息的消费者
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>("testForFlink4", new SimpleStringSchema(), props);
        //consumer.setStartFromGroupOffsets();
        consumer.setStartFromEarliest();
        //consumer.setStartFromLatest();

        //增加时间水位设置类
        consumer.assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<String>(){
            @Override
            public long extractTimestamp(String element, long previousElementTimestamp) {
                return JsonHelper.getTimeLongFromRawMessage(element);
            }

            @Nullable
            @Override
            public Watermark checkAndGetNextWatermark(String lastElement, long extractedTimestamp) {
                if (lastElement != null) {
                    return new Watermark(JsonHelper.getTimeLongFromRawMessage(lastElement));
                }
                return null;
            }
        });

        env.addSource(consumer)
                //将原始消息转成Tuple2对象，保留用户名称和访问次数(每个消息访问次数为1)
                .flatMap((FlatMapFunction<String, Tuple2<String, Long>>) (s, collector) -> {
                    SingleMessage singleMessage = JsonHelper.parse(s);

                    if (singleMessage != null) {
                        collector.collect(new Tuple2<>(singleMessage.getName(), 1L));
                    }
                })
                .returns(TypeInformation.of(new TypeHint<Tuple2<String, Long>>(){}))
                //以用户名为key
                .keyBy(0)
                //时间窗口为2秒
                .timeWindow(Time.seconds(5), Time.seconds(5))
                //将每个用户访问次数累加起来
                .apply(new windowFunction())
                //输出方式是STDOUT
                .print();


        env.execute("Flink-Kafka demo");
    }
    /* WindowFunction<IN, OUT, KEY, W extends Window> */
    public static class windowFunction implements WindowFunction<Tuple2<String, Long>, Tuple2<String, Long>, Tuple, TimeWindow>{
        @Override
        public void apply(Tuple tuple,
                          TimeWindow window,
                          Iterable<Tuple2<String, Long>> input,
                          Collector<Tuple2<String, Long>> out) throws Exception{
            long sum = 0L;
            for (Tuple2<String, Long> record: input) {
                sum += record.f1;
            }

            Tuple2<String, Long> result = input.iterator().next();
            result.f1 = sum;
            out.collect(result);
            System.out.println("=========================================================");
        }
    }
}

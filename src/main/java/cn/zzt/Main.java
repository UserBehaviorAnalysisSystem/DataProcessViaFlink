package cn.zzt;
import cn.Bp.CreatLineChart;
import cn.SinkFunction.SinkToCSV;
import cn.WindowFunction.ProcessCountUser;
import cn.csv.CsvOp;
import cn.zzt.MyClass;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.windowing.time.Time;
import cn.AggregateFunction.CountSpecificKey;
import cn.WindowFunction.WindowResultFunction;
import cn.KeyedProcessFunction.TopNHotItems;

public class Main {
    public void runDemo() throws Exception{
        // generate dataSet
        CsvOp c = new CsvOp();
        //String src = "D:\\学习\\毕设\\Project\\src\\main\\resources\\UserBehaviorSmall.csv";
        //String dst = "D:\\学习\\毕设\\Project\\src\\main\\resources\\demo\\rawData.csv";
        String src = "D:\\学习\\毕设\\Project\\src\\main\\resources\\UserBehaviorSmall.csv";
        String dst = "D:\\学习\\毕设\\Project\\src\\main\\resources\\demo\\rawData.csv";
        c.generateDataset(src, dst, 400000);

        // preprocess
        MyClass m = new MyClass();
        m.createCsvDataSource("demo/rawData.csv")
                .timeWindowAll(Time.minutes(30), Time.minutes(15))
                .process(new ProcessCountUser())
                .addSink(new SinkToCSV());
        m.env.execute("demo");

        CreatLineChart creatLineChart = new CreatLineChart();
        creatLineChart.draw();
    }

    public void runMain() throws Exception{
        // generate dataSet
        CsvOp c = new CsvOp();
        String src = "D:\\学习\\毕设\\Project\\src\\main\\resources\\UserBehaviorLarge.csv";
        String dst = "D:\\学习\\毕设\\Project\\src\\main\\resources\\final\\rawData.csv";
        c.generateDataset(src, dst, 400000);

        // preprocess
        MyClass m = new MyClass();
        DataStreamSource<String> dataStreamSource = m.createKafkaDataSource();
        dataStreamSource
                .map((MapFunction<String, UserBehavior>) s -> UserBehavior.parse(s))
                .timeWindowAll(Time.minutes(30), Time.seconds(15))
                .process(new ProcessCountUser())
                .addSink(new SinkToCSV());
        m.env.execute("flink kafka consumer");

        // show picture
        new CreatLineChart().draw();
    }
    public static void main(String[] args) throws Exception{
        //new Main().runDemo();
        new Main().runMain();
        /*MyClass m = new MyClass();

        DataStream<UserBehavior> pv = m.filterPv();


        //通过itemid进行分组，然后用timewindow给它分配窗口，第一个参数是窗口的长度，第二个是每隔多久滑动一次
        //然后用aggregate进行聚合，第一个参数是聚合函数，第二个是格式化输出方式，这里是以Collector<ItemViewCount输出>
        DataStream<ItemViewCount> windowedData = pv
                .keyBy("itemId")
                .timeWindow(Time.minutes(60), Time.minutes(5))
                .aggregate(new CountSpecificKey(), new WindowResultFunction());

        //以windowEnd来分组，然后实现一个TopHotItem函数，并将结果格式化为字符串
        DataStream<String> topItems = windowedData
                .keyBy("windowEnd")
                .process(new TopNHotItems(4));  // 求点击量前4名的商品

        topItems.print();
        m.env.execute("Hot Items Job");*/
    }
}

package cn.zzt;
import cn.zzt.MyClass;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import cn.AggregateFunction.CountSpecificKey;
import cn.WindowFunction.WindowResultFunction;
import cn.KeyedProcessFunction.TopNHotItems;

public class Main {
    public static void main(String[] args) throws Exception{
        MyClass m = new MyClass();

        DataStream<UserBehavior> pv = m.filterPv();

        /*
         * 通过itemid进行分组，然后用timewindow给它分配窗口，第一个参数是窗口的长度，第二个是每隔多久滑动一次
         * 然后用aggregate进行聚合，第一个参数是聚合函数，第二个是格式化输出方式，这里是以Collector<ItemViewCount输出>
         */
        DataStream<ItemViewCount> windowedData = pv
                .keyBy("itemId")
                .timeWindow(Time.minutes(60), Time.minutes(5))
                .aggregate(new CountSpecificKey(), new WindowResultFunction());
        /*
         * 以windowEnd来分组，然后实现一个TopHotItem函数，并将结果格式化为字符串
         */
        DataStream<String> topItems = windowedData
                .keyBy("windowEnd")
                .process(new TopNHotItems(4));  // 求点击量前4名的商品

        topItems.print();
        m.env.execute("Hot Items Job");
    }
}

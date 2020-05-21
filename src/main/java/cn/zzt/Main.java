package cn.zzt;
import cn.Bp.Data;
import cn.Bp.MyFrame;
import cn.RFM.RFM;
import cn.SinkFunction.SinkToCSV;
import cn.WindowFunction.ProcessCountUser;
import cn.csv.CsvOp;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
    public static Lock containerLock = new ReentrantLock();
    public static SynchronousQueue<Data> queue = new SynchronousQueue<Data>();

    public static MyFrame myFrame = null;
    public static RFM rfm = new RFM();

    public static Object[] columnNames = {"TopN", "ItemId", "frequency"};
    public static Object[] columnNames2 = {"TopN", "userId", "RFM"};
    public static Object[][] rowData = {
            {1, 0, 0},
            {2, 0, 0},
            {3, 0, 0},
            {4, 0, 0},
            {5, 0, 0},
            {6, 0, 0},
            {7, 0, 0},
            {8, 0, 0},
            {9, 0, 0},
            {10, 0, 0},
            {11, 0, 0},
            {12, 0, 0},
            {13, 0, 0},
            {14, 0, 0},
            {15, 0, 0}
    };
    public static Object[][] rowData2 = {
            {1, 0, 0},
            {2, 0, 0},
            {3, 0, 0},
            {4, 0, 0},
            {5, 0, 0},
            {6, 0, 0},
            {7, 0, 0},
            {8, 0, 0},
            {9, 0, 0},
            {10, 0, 0},
            {11, 0, 0},
            {12, 0, 0},
            {13, 0, 0},
            {14, 0, 0},
            {15, 0, 0}
    };

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

        //CreatLineChart creatLineChart = new CreatLineChart();
        //creatLineChart.draw();
    }

    public void runMain() throws Exception{
        // generate dataSet
        CsvOp c = new CsvOp();
        String src = "D:\\学习\\毕设\\Project\\src\\main\\resources\\UserBehaviorLarge.csv";
        String dst = "D:\\学习\\毕设\\Project\\src\\main\\resources\\final\\rawData.csv";
        c.generateDataset(src, dst, 400000);

        // preprocess
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyClass m = new MyClass();
                    DataStreamSource<String> dataStreamSource = m.createKafkaDataSource("testForFlink10");
                    DataStreamSource<String> dataStreamSource2 = m.createKafkaDataSource("topN");
                    DataStreamSource<String> dataStreamSource3 = m.createKafkaDataSource("userTopN");
                    dataStreamSource
                            .map((MapFunction<String, UserBehavior>) s -> UserBehavior.parse(s))
                            .timeWindowAll(Time.minutes(30), Time.seconds(15))
                            .process(new ProcessCountUser())
                            .addSink(new SinkToCSV());

                    dataStreamSource2
                            .map((MapFunction<String, UserBehavior>) s -> UserBehavior.parse(s))
                            .filter(new FilterFunction<UserBehavior>() {
                                @Override
                                public boolean filter(UserBehavior userBehavior) throws Exception {
                                    return userBehavior.getBehavior().equals("pv");
                                }
                            })
                            .keyBy("itemId")
                            .timeWindow(Time.minutes(30), Time.seconds(15))
                            .aggregate(new AggregateFunction<UserBehavior, Long, Long>(){
                                @Override
                                public Long createAccumulator() {
                                    return 0L;
                                }

                                @Override
                                public Long add(UserBehavior userBehavior, Long acc) {
                                    return acc + 1;
                                }

                                @Override
                                public Long getResult(Long acc) {
                                    return acc;
                                }

                                @Override
                                public Long merge(Long acc1, Long acc2) {
                                    return acc1 + acc2;
                                }
                            }, new WindowFunction<Long, ItemViewCount, Tuple, TimeWindow>(){
                                @Override
                                public void apply(
                                        Tuple key,  // 窗口的主键，即 itemId
                                        TimeWindow window,  // 窗口
                                        Iterable<Long> aggregateResult, // 聚合函数的结果，即 count 值
                                        Collector<ItemViewCount> collector  // 输出类型为 ItemViewCount
                                ) throws Exception {
                                    Long itemId = ((Tuple1<Long>) key).f0;
                                    Long count = aggregateResult.iterator().next();
                                    collector.collect(ItemViewCount.of(itemId, window.getEnd(), count));
                                }
                            })
                            .keyBy("windowEnd")
                            .process(new KeyedProcessFunction<Tuple, ItemViewCount, String>(){
                                private final int topSize = 15;
                                private ListState<ItemViewCount> itemState;
                                @Override
                                public void open(Configuration parameters) throws Exception {
                                    super.open(parameters);
                                    ListStateDescriptor<ItemViewCount> itemsStateDesc = new ListStateDescriptor<>(
                                            "itemState-state",
                                            ItemViewCount.class);
                                    itemState = getRuntimeContext().getListState(itemsStateDesc);
                                }
                                @Override
                                public void processElement(
                                        ItemViewCount input,
                                        Context context,
                                        Collector<String> collector) throws Exception {

                                    // 每条数据都保存到状态中
                                    itemState.add(input);
                                    // 注册 windowEnd+1 的 EventTime Timer, 当触发时，说明收齐了属于windowEnd窗口的所有商品数据
                                    context.timerService().registerEventTimeTimer(input.windowEnd + 1);
                                }
                                @Override
                                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                                    // 获取收到的所有商品点击量
                                    List<ItemViewCount> allItems = new ArrayList<>();
                                    for (ItemViewCount item : itemState.get()) {
                                        allItems.add(item);
                                    }
                                    // 提前清除状态中的数据，释放空间
                                    itemState.clear();
                                    // 按照点击量从大到小排序
                                    allItems.sort(new Comparator<ItemViewCount>() {
                                        @Override
                                        public int compare(ItemViewCount o1, ItemViewCount o2) {
                                            return (int) (o2.viewCount - o1.viewCount);
                                        }
                                    });
                                    // 将排名信息格式化成 String, 便于打印
                                    // update swing form
                                    StringBuilder result = new StringBuilder();
                                    result.append("====================================\n");
                                    result.append("时间: ").append(new Timestamp(timestamp-1)).append("\n");
                                    containerLock.lock();
                                    int len = allItems.size();
                                    for (int i = 0; i < len && i < topSize; i++) {
                                        ItemViewCount currentItem = allItems.get(i);
                                        Object[] curData = rowData[i];
                                        rowData[i][0] = i + 1;
                                        rowData[i][1] = currentItem.itemId;
                                        rowData[i][2] = currentItem.viewCount;
                                        // No1:  商品ID=12224  浏览量=2413
                                        result.append("No").append(i).append(":")
                                                .append("  商品ID=").append(currentItem.itemId)
                                                .append("  浏览量=").append(currentItem.viewCount)
                                                .append("\n");
                                    }
                                    result.append("====================================\n\n");

                                    Container container = myFrame.getContentPane();
                                    container.invalidate();
                                    JPanel jPanel = (JPanel)container.getComponent(1);
                                    jPanel.remove(1);
                                    JTable jTable = new JTable(rowData, columnNames);
                                    jPanel.add(jTable, 1);
                                    container.validate();

                                    containerLock.unlock();

                                    // 控制输出频率，模拟实时滚动结果
                                    Thread.sleep(1000);

                                    //out.collect(result.toString());
                                }
                            })
                            .print();

                    dataStreamSource3
                            .map((MapFunction<String, UserBehavior>) s -> UserBehavior.parse(s))
                            .filter(new FilterFunction<UserBehavior>() {
                                @Override
                                public boolean filter(UserBehavior userBehavior) throws Exception {
                                    return userBehavior.getBehavior().equals("buy");
                                }
                            }).map(new MapFunction<UserBehavior, Long>() {
                                @Override
                                public Long map(UserBehavior userBehavior) throws Exception{
                                    long userId = userBehavior.getUserId();
                                    long timestamp = userBehavior.getTimestamp();
                                    rfm.connect();
                                    rfm.update(userId, timestamp);
                                    return userId;
                                }
                    }).print();
                    /*dataStreamSource3
                            .map((MapFunction<String, UserBehavior>) s -> UserBehavior.parse(s))
                            .filter(new FilterFunction<UserBehavior>() {
                                @Override
                                public boolean filter(UserBehavior userBehavior) throws Exception {
                                    return userBehavior.getBehavior().equals("buy");
                                }
                            })
                            .print();*/

                    m.env.execute("flink kafka consumer");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //MyClass m = new MyClass();
                    //DataStreamSource<String> dataStreamSource;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        // show picture
        new Thread(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // 创建图形
                        try {
                            myFrame = new MyFrame();
                            (new Thread(myFrame)).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

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

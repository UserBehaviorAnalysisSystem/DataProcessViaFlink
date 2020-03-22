package cn.KeyedProcessFunction;

import cn.zzt.ItemViewCount;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* KeyedProcessFunction<K, I, O> */
public class TopNHotItems extends KeyedProcessFunction<Tuple, ItemViewCount, String> {
    private final int topSize;
    // 用于存储商品与点击数的状态，待收齐同一个窗口的数据后，再触发 TopN 计算
    private ListState<ItemViewCount> itemState;

    public TopNHotItems(int topSize) {
        this.topSize = topSize;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        ListStateDescriptor<ItemViewCount> itemsStateDesc = new ListStateDescriptor<>("itemState-state", ItemViewCount.class);
        itemState = getRuntimeContext().getListState(itemsStateDesc);
    }

    /*
     * 每当收到一条数据（ItemViewCount），我们就注册一个 windowEnd+1 的定时器（Flink 框架会自动忽略同一时间的重复注册）。
     * windowEnd+1 的定时器被触发时，意味着收到了windowEnd+1的 Watermark，即收齐了该windowEnd下的所有商品窗口统计值。
     */
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

    /*
     * 在 onTimer() 中处理将收集的所有商品及点击量进行排序，选出 TopN，并将排名信息格式化成字符串后进行输出。
     */
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
            public int compare(ItemViewCount a, ItemViewCount b) {
                return (int) (b.viewCount - a.viewCount);
            }
        });
        // 将排名信息格式化成 String, 便于打印
        StringBuilder result = new StringBuilder();
        result.append("====================================\n");
        result.append("时间: ").append(new Timestamp(timestamp-1)).append("\n");
        for (int i=0; i < allItems.size() && i < topSize; i++) {
            ItemViewCount currentItem = allItems.get(i);
            // No1:  商品ID=12224  浏览量=2413
            result.append("No").append(i).append(":")
                    .append("  商品ID=").append(currentItem.itemId)
                    .append("  浏览量=").append(currentItem.viewCount)
                    .append("\n");
        }
        result.append("====================================");

        // 控制输出频率，模拟实时滚动结果
        Thread.sleep(1000);

        out.collect(result.toString());
        out.collect("my string");
    }
}

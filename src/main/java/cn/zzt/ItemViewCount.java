package cn.zzt;

public class ItemViewCount {
    public long itemId;     // 商品ID
    public long windowEnd;  // 窗口结束时间戳
    public long viewCount;  // 商品的点击量

    // constructor
    public static ItemViewCount of(long itemId, long windowEnd, long viewCount) {
        ItemViewCount result = new ItemViewCount();
        result.itemId = itemId;
        result.windowEnd = windowEnd;
        result.viewCount = viewCount;
        return result;
    }
}

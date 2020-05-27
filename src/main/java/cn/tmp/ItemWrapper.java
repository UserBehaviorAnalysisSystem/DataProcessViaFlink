package cn.tmp;


public class ItemWrapper {
    public long itemId;     // 商品ID
    public long windowEnd;  // 窗口结束时间戳
    public long viewCount;  // 商品的点击量

    // constructor
    public static ItemWrapper of(long itemId, long windowEnd, long viewCount) {
        ItemWrapper result = new ItemWrapper();
        result.itemId = itemId;
        result.windowEnd = windowEnd;
        result.viewCount = viewCount;
        return result;
    }
}

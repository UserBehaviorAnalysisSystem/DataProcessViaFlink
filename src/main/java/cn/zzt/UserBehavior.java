package cn.zzt;

public class UserBehavior {
    public long userId;         // 用户ID
    public long itemId;         // 商品ID
    public int categoryId;      // 商品类目ID
    public String behavior;     // 用户行为, 包括("pv", "buy", "cart", "fav")
    public long timestamp;      // 行为发生的时间戳，单位秒

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("userId:").append(userId)
                .append(", itemId:").append(itemId)
                .append(", categoryId:").append(categoryId)
                .append(", behavior:").append(behavior)
                .append(", timestamp:").append(timestamp);
        return ret.toString();
    }
}

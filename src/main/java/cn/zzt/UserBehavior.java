package cn.zzt;

import com.alibaba.fastjson.JSONObject;

import static java.lang.System.exit;

public class UserBehavior {
    private long userId;         // 用户ID
    private long itemId;         // 商品ID
    private int categoryId;      // 商品类目ID
    private String behavior;     // 用户行为, 包括("pv", "buy", "cart", "fav")
    private long timestamp;      // 行为发生的时间戳，单位秒

    public long getUserId() { return userId; }
    public long getItemId() { return itemId; }
    public int getCategoryId() { return categoryId; }
    public String getBehavior() { return behavior; }
    public long getTimestamp() { return timestamp; }

    public void setUserId(long userId) { this.userId = userId; }
    public void setItemId(long itemId) { this.itemId = itemId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setBehavior(String behavior) { this.behavior = behavior; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public UserBehavior() throws Exception{

    }
    public UserBehavior(long a, long b, int c, String d, long e) throws Exception{
        this.userId = a;
        this.itemId = b;
        this.categoryId = c;
        this.behavior = d;
        this.timestamp = e;
    }

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

    public static UserBehavior parse(String raw){
        try{
            UserBehavior ret = JSONObject.parseObject(raw, UserBehavior.class);
            return ret;
        }catch (Exception e){
            e.printStackTrace();
            exit(-1);
            return null;
        }
    }

    public static long  parseTimeStamp(String raw){
        try{
            UserBehavior userBehavior = parse(raw);
            return userBehavior.getTimestamp();
        }catch (Exception e){
            e.printStackTrace();
            exit(-1);
            return -1;
        }
    }
}

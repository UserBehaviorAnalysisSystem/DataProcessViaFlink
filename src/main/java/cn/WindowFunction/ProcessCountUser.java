package cn.WindowFunction;

import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;

/* ProcessWindowFunction<IN, OUT, KEY, W extends Window> */
/* ProcessAllWindowFunction<IN, OUT, W extends Window> */
public class ProcessCountUser extends ProcessAllWindowFunction<UserBehavior, HashMap<Long, Long>, TimeWindow> {
    @Override
    public void process(Context context, Iterable<UserBehavior> input, Collector<HashMap<Long, Long>> out){
        //String windowStart=new DateTime(context.window().getStart(), DateTimeZone.forID("+08:00")).toString("yyyy-MM-dd HH:mm:ss");
        //String windowEnd=new DateTime(context.window().getEnd(), DateTimeZone.forID("+08:00")).toString("yyyy-MM-dd HH:mm:ss");
        String windowTime = context.window().toString();
        System.out.println(windowTime);

        HashMap<Long, Long> ret = new HashMap<>();
        for(UserBehavior u: input){
            Long userId = u.userId;
            if(ret.containsKey(userId)){
                Long old = ret.get(userId);
                ret.put(userId, old + 1);
            }else{
                ret.put(userId, 1L);
            }
        }
        out.collect(ret);
    }
}
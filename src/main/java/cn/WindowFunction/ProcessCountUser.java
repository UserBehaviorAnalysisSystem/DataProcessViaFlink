package cn.WindowFunction;

import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.HashSet;

/* ProcessWindowFunction<IN, OUT, KEY, W extends Window> */
/* ProcessAllWindowFunction<IN, OUT, W extends Window> */
public class ProcessCountUser extends ProcessAllWindowFunction<UserBehavior, HashSet<UserBehavior>, TimeWindow> {
    @Override
    public void process(Context context, Iterable<UserBehavior> input, Collector<HashSet<UserBehavior>> out){
        //String windowStart=new DateTime(context.window().getStart(), DateTimeZone.forID("+08:00")).toString("yyyy-MM-dd HH:mm:ss");
        //String windowEnd=new DateTime(context.window().getEnd(), DateTimeZone.forID("+08:00")).toString("yyyy-MM-dd HH:mm:ss");
        String windowTime = context.window().toString();
        System.out.println("===========" + windowTime + "================");

        HashSet<UserBehavior> ret = new HashSet<>();
        for(UserBehavior u: input){
            ret.add(u);
        }
        out.collect(ret);
    }
}
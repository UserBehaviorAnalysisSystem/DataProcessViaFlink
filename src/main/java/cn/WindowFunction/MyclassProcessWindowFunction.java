package cn.WindowFunction;

import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashSet;

/* ProcessAllWindowFunction<IN, OUT, W extends Window> */
public class MyclassProcessWindowFunction extends ProcessAllWindowFunction<String, HashSet<String>, TimeWindow> {
    @Override
    public void process(Context context, Iterable<String> input, Collector<HashSet<String>> out){
        String windowTime = context.window().toString();
        System.out.println("=====" + windowTime + "============");

        HashSet<String> ret = new HashSet<>();
        for(String s: input){
            ret.add(s);
        }
        out.collect(ret);
        System.out.println("hashset size: " + ret.size());
    }
}

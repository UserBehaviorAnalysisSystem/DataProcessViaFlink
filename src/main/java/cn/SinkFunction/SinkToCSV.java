package cn.SinkFunction;

import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.HashMap;
import java.util.Map;

/* extends AbstractRichFunction */
public class SinkToCSV extends RichSinkFunction<HashMap<Long, Long>> {
    int count = 0;
    //HashMap<Long, Long> ret = new HashMap<>();
    @Override
    public void invoke(HashMap<Long, Long> map, Context context) throws Exception {
        //System.out.println("map size:" + String.valueOf(map.size()));
        //System.out.printf("count:%d\n", count++);
        for(Map.Entry<Long, Long> e: map.entrySet()){
            if(e.getValue() > 1L){
                //ret.put(e.getKey(), e.getValue());
                StringBuilder sb = new StringBuilder();
                sb.append(e.getKey()).append(":").append(e.getValue());
                System.out.println(sb.toString());
            }
        }

    }
}

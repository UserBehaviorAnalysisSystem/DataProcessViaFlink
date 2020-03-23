package cn.SinkFunction;

import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/* extends AbstractRichFunction */
public class SinkToCSV extends RichSinkFunction<HashSet<UserBehavior>> {
    int count = 0;
    //HashMap<Long, Long> ret = new HashMap<>();
    @Override
    public void invoke(HashSet<UserBehavior> set, Context context) throws Exception {

        //String windowStart = new DateTime(context.window().getStart(), DateTimeZone.forID("+08:00")).toString("yyyy-MM-dd HH:mm:ss");
        // userId - count
        HashMap<Long, Long> map = new HashMap<>();
        HashMap<Long, HashSet<String>> eachUserBehavior = new HashMap<Long, HashSet<String>>();
        // operator type - count
        HashMap<String, Long> operatorMap = new HashMap<>();

        float count = 0;
        Long windowEnd = Long.MIN_VALUE;
        for(UserBehavior u: set){
            count++;
            Long id = u.getUserId();
            String type = u.behavior;
            Long time = u.timestamp;

            if(time > windowEnd)
                windowEnd = time;

            if(map.containsKey(id)){
                Long old = map.get(id);
                // update count
                map.put(id, old + 1);
                // new operation
                if(!eachUserBehavior.get(id).contains(type)){
                    eachUserBehavior.get(id).add(type);
                }
            }else{
                map.put(id, 1L);
                eachUserBehavior.put(id, new HashSet<>());
            }

            if(operatorMap.containsKey(type)){
                Long old = operatorMap.get(type);
                operatorMap.put(type, old + 1);
            }else{
                operatorMap.put(type, 1L);
            }
        }
        // 1.
        int allUser = map.size();
        System.out.printf("PV/UV: %f\n", count / allUser);

        // 2.
        float onlyPV = 0;
        for(Map.Entry<Long, HashSet<String>> e: eachUserBehavior.entrySet()){
            if(e.getValue().size() == 1 && e.getValue().contains("pv")){
                onlyPV++;
            }
        }
        System.out.printf("onlyPV/PV:%f\n", onlyPV / count);
        // 4.
        for(Map.Entry<String, Long> e: operatorMap.entrySet()){
            Long cur = e.getValue();
            System.out.printf("%s/all: %d/%f = %f\n", e.getKey(), e.getValue(), count, e.getValue() / count);
        }


    }
}

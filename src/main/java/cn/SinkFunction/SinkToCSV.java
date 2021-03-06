package cn.SinkFunction;

import cn.Bp.BP;
import cn.Bp.Data;
import cn.csv.CsvOp;
import cn.zzt.Main;
import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/* extends AbstractRichFunction */
public class SinkToCSV extends RichSinkFunction<HashSet<UserBehavior>> {
    //HashMap<Long, Long> ret = new HashMap<>();
    private int dataCount = 0;
    private ArrayList<ArrayList<Double>> datas = new ArrayList<>();
    private ArrayList<Double> expects = new ArrayList<>();

    private ArrayList<Double> lastData = null;
    private Double lastRealExpect = 0.0;
    private Double lastPredictExpect = 0.0;
    private int truePos = 0, trueNeg = 0, falsePos = 0, falseNeg = 0;
    @Override
    public void invoke(HashSet<UserBehavior> set, Context context) throws Exception {
        //String windowStart = new DateTime(context.window().getStart(), DateTimeZone.forID("+08:00")).toString("yyyy-MM-dd HH:mm:ss");
        // userId - count
        HashMap<Long, Long> map = new HashMap<>();
        HashMap<Long, HashSet<String>> eachUserBehavior = new HashMap<Long, HashSet<String>>();
        // operator type - count
        HashMap<String, Long> operatorMap = new HashMap<>();

        Double count = 0.0;
        Long windowEnd = Long.MIN_VALUE;
        for(UserBehavior u: set){
            count++;
            Long id = u.getUserId();
            String type = u.getBehavior();
            Long time = u.getTimestamp();

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
                eachUserBehavior.get(id).add(type);
            }

            if(operatorMap.containsKey(type)){
                Long old = operatorMap.get(type);
                operatorMap.put(type, old + 1);
            }else{
                operatorMap.put(type, 1L);
            }
        }
        // 1. PV/UV
        int allUser = map.size();
        //System.out.printf("PV/UV: %f\n", count / allUser);

        // 2. onlyPV/UV
        Double onlyPV = 0.0;
        for(Map.Entry<Long, HashSet<String>> e: eachUserBehavior.entrySet()){
            if(e.getValue().size() == 1 && e.getValue().contains("pv")){
                onlyPV++;
            }
        }
        //System.out.printf("onlyPV/PV:%f\n", onlyPV / count);
        // 3. each type / all percent
        HashMap<String, Double> percent = new HashMap<>();
        percent.put("pv", 0.0);
        percent.put("fav", 0.0);
        percent.put("buy", 0.0);
        percent.put("cart", 0.0);
        for(Map.Entry<String, Long> e: operatorMap.entrySet()){
            Long cur = e.getValue();
            //System.out.printf("%s/all: %d/%f = %f\n", e.getKey(), e.getValue(), count, e.getValue() / count);
            percent.put(e.getKey(), e.getValue() / count);
        }

        StringBuilder sb = new StringBuilder();
        // format: windowEnd | pv/uv | onlyPV/UV | pv/all | fav/all | buy/all | cart/all
        //sb.append("windowEnd,").append("pv/uv,").append("onlyPV/UV,").append("pv/all,").append("fav/all,").append("buy/all,").append("cart/all\n");
        sb.append(windowEnd).append(",").append(onlyPV / count).append(",");
        sb.append(percent.get("pv")).append(",")
                .append(percent.get("fav")).append(",")
                .append(percent.get("buy")).append(",")
                .append(percent.get("cart"));
        String record = sb.toString();
        String pvuv = String.valueOf(count / allUser);
        CsvOp csvOp = new CsvOp();
        csvOp.appendWrite(record, "src/main/resources/final/data.csv");
        csvOp.appendWrite(pvuv, "src/main/resources/final/expect.csv");
        /*if(lastExpect == 0.0){
            lastExpect = count / allUser;
        }else{

        }*/


        // predict
        BP bp = bp = BP.getInstance(5, 4, 1);

        ArrayList<Double> data = new ArrayList<Double>();
        data.add(onlyPV / count);
        data.add(percent.get("pv"));
        data.add(percent.get("fav"));
        data.add(percent.get("buy"));
        data.add(percent.get("cart"));
        datas.add(data);
        expects.add(count / allUser);
        // show diff
        Double predictResult = bp.predict(data);
        //System.out.println("predict:" + predictResult + ", expect:" + count / allUser);
        StringBuilder compare = new StringBuilder();
        compare.append(predictResult).append(",").append(count / allUser);
        csvOp.appendWrite(compare.toString(), "src/main/resources/final/compare.csv");

        // analysis result
        Double curRealExpect = count / allUser;
        //System.out.println("curReal:" + curRealExpect + ", lastReal:" + lastRealExpect + ", curPred:" + predictResult + ", lastPred:" + lastPredictExpect);
        if(lastRealExpect != 0.0 && lastPredictExpect != 0.0){
            if(curRealExpect > lastRealExpect){
                // actually increase: positive
                //csvOp.appendWrite("up", "src/main/resources/final/F1result.csv");
                if(predictResult > lastRealExpect){
                    // true positive
                    //System.out.println("curPred:" + predictResult + ", lastPred:" + lastPredictExpect);
                    truePos++;
                }else{
                    // false negative
                    //System.out.println("curPred:" + predictResult + ", lastPred:" + lastPredictExpect);
                    falseNeg++;
                }
            }else{
                // actually decrease: negative
                //csvOp.appendWrite("down", "src/main/resources/final/F1result.csv");
                if(predictResult > lastRealExpect){
                    // false positive
                    falsePos++;
                }else{
                    // true negative
                    trueNeg++;
                }
            }
        }
        lastRealExpect = count / allUser;
        lastPredictExpect = predictResult;
        System.out.println("truePos:" + truePos + ", trueNeg:" + trueNeg + ", " + "falsePos:" + falsePos + ",falseNeg:" + falseNeg);
        float recall = (float)(truePos)/((float)(truePos + falseNeg)), precision = (float)truePos/((float)(truePos + falsePos));
        float f1 = 2 * (recall * precision)/(recall + precision);
        System.out.println("f1:" + f1);

        Main.queue.put(new Data(predictResult, count / allUser));
        // update bp network
        //bp.trainOne(data, curRealExpect);
        if(lastData != null){
            //System.out.println("train");
            //bp.trainOne(lastData, count / allUser);
        }

        if(lastData != null){
            // first data
            // do nothing
        }else{
            lastData = data;
            //System.out.println("not null");
        }
    }
}

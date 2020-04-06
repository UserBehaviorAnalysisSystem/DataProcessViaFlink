package cn.Bp;

import cn.Bp.BP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * data format: 14列
 * date; price; numOfBedRoom; numOfbathroom; areaOfHouse; areaOfParking; numOfFloors; grade; area; areaOfUnderroom; yearOfBuilding; yearOfRepair; 纬度； 经度
 * 第二列就是想要predict的东西！！！
 */
public class PredictHousePrice {
    private static int numOfTrain = 100;
    private static Double k = 0.0;
    private static Double b = 0.0;

    public static ArrayList<Double> readExpect() throws Exception{
        String filename = "src/main/resources/data/kc_train2.csv";
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        int n = 0;

        ArrayList<Double> ret = new ArrayList<>();
        Double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
        while((line = reader.readLine()) != null && n++ < numOfTrain){
            Double cur = Double.valueOf(line);
            if(cur > max) max = cur;
            if(cur < min) min = cur;
            ret.add(cur);
        }
        Double gap = max - min;
        PredictHousePrice.k = gap;
        PredictHousePrice.b = min;

        int len = ret.size();
        for(int i = 0; i < len; ++i){
            ret.set(i, ret.get(i) - min);
        }
        for(int i = 0; i < len; ++i){
            ret.set(i, ret.get(i) / gap);
        }
        return ret;
    }

    public static ArrayList<ArrayList<Double>> readData() throws Exception{
        String filename = "src/main/resources/data/kc_train.csv";
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        ArrayList<ArrayList<Double>> datas = new ArrayList<>();

        int n = 0;
        while((line = reader.readLine()) != null && n++ < numOfTrain){
            String[] items = line.split(",");
            ArrayList<Double> data = new ArrayList<>();
            data.add(Double.valueOf(items[6])); // grade
            data.add(Double.valueOf(items[7]) / 1000.0); // area
            datas.add(data);
        }
        return datas;
    }
    public static void show(ArrayList<ArrayList<Double>> datas, ArrayList<Double> expect) throws Exception{
        assert datas.size() == expect.size();
        int len = datas.size();
        for(int i = 0; i < len; ++i){
            System.out.println(datas.get(i).get(0) + ", " +  datas.get(i).get(1) +  " : " + expect.get(i));
        }
    }

    public static void main(String[] args) throws Exception{
        ArrayList<Double> expect = PredictHousePrice.readExpect();
        ArrayList<ArrayList<Double>> datas = PredictHousePrice.readData();
        PredictHousePrice.show(datas, expect);

        BP bp = new BP(2, 4, 1);

        bp.train(datas, expect);

        int len = datas.size();
        for(int i = 0; i < len; ++i){
            Double res = bp.feedForward(datas.get(i)).get(0) * PredictHousePrice.k + PredictHousePrice.b;
            Double exp = expect.get(i) * PredictHousePrice.k + PredictHousePrice.b;
            System.out.println("get: " + res + " ,  expect:" + exp);
        }
    }
}

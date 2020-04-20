package cn.Bp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/*
 * data format: 14列
 * date; price; numOfBedRoom; numOfbathroom; areaOfHouse; areaOfParking; numOfFloors; grade; area; areaOfUnderroom; yearOfBuilding; yearOfRepair; 纬度； 经度
 * 第二列就是想要predict的东西！！！
 */
public class PredictDemo implements PredictBase {
    public static int numOfTrain = 30;

    public Double k = 0.0;
    public Double b = 0.0;
    public ArrayList<Double> expects = null;
    public ArrayList<ArrayList<Double>> datas = null;
    public ArrayList<Double> result = new ArrayList<>();

    public BP bp = null;

    public void init() throws Exception{
        datas = readData();
        expects = readExpect("src/main/resources/data/kc_train2.csv");
        bp = BP.getInstance(2, 4, 1);
    }

    public void train() throws Exception{
        bp.train(datas, expects);
        // save result
        int len = datas.size();
        for(int i = 0; i < len; ++i){
            Double res = bp.feedForward(datas.get(i)).get(0) * k + b;
            result.add(res);
        }
    }

    public void show() throws Exception{
        assert datas.size() == expects.size();
        int len = datas.size();
        for(int i = 0; i < len; ++i){
            Double exp = expects.get(i) * k + b;
            System.out.println("get: " + result.get(i) + " ,  expect:" + exp);
        }
    }

    public ArrayList<ArrayList<Double>> export() throws Exception{
        ArrayList<ArrayList<Double>> ret = new ArrayList<>();
        ArrayList<Double> exp = new ArrayList<>();
        for(Double d: expects){
            exp.add(d * k + b);
        }
        ret.add(result);
        ret.add(exp);
        return ret;
    }
    /*-----------------------------------------------------------------------------*/
    public ArrayList<Double> readExpect(String s) throws Exception{
        String filename = s;
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
        k = gap;
        b = min;

        int len = ret.size();
        for(int i = 0; i < len; ++i){
            ret.set(i, ret.get(i) - min);
        }
        for(int i = 0; i < len; ++i){
            ret.set(i, ret.get(i) / gap);
        }
        return ret;
    }

    public ArrayList<ArrayList<Double>> readData() throws Exception{
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


    public static void main(String[] args) throws Exception{
        PredictDemo driver = new PredictDemo();

        driver.init();
        driver.train();
        driver.show();
    }
}

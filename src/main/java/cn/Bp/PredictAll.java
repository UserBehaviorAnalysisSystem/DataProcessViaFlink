package cn.Bp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class PredictAll extends PredictDemo {
    @Override
    public void init() throws Exception{
        datas = readData();
        expects = readExpect("src/main/resources/data/kc_train2.csv");
        bp = new BP(datas.get(0).size(), 4, 1);
    }

    @Override
    public ArrayList<ArrayList<Double>> readData() throws Exception{
        String filename = "src/main/resources/data/kc_train.csv";
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<ArrayList<Double>> datas = new ArrayList<>();

        String line = null;
        int n = 0;
        while((line = reader.readLine()) != null && n++ < numOfTrain){
            Item item = new Item(line);
            ArrayList<Double> data = item.toData();
            datas.add(data);
        }
        return datas;
    }

    public static void main(String[] args) throws Exception{
        PredictAll driver = new PredictAll();

        driver.init();
        driver.train();
        driver.show();
    }
}

package cn.Bp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Predict extends PredictDemo{
    @Override
    public void init() throws Exception{
        datas = readData();
        expects = readExpect("src/main/resources/final/expect.csv");
        bp = BP.getInstance(4, 4, 1);
    }

    /*--------------------------------------------------*/
    @Override
    public ArrayList<ArrayList<Double>> readData() throws Exception{
        String filename = "src/main/resources/final/data.csv";
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        ArrayList<ArrayList<Double>> datas = new ArrayList<>();

        int n = 0;
        // format: windowEnd | onlyPV/UV | pv/all | fav/all | buy/all | cart/all
        while((line = reader.readLine()) != null && n++ < numOfTrain){
            String[] items = line.split(",");
            ArrayList<Double> data = new ArrayList<>();
            data.add(Double.valueOf(items[1])); // onlyPV/UV
            data.add(Double.valueOf(items[2])); // pv/all
            data.add(Double.valueOf(items[3]));
            data.add(Double.valueOf(items[4]));
            datas.add(data);
        }
        return datas;
    }

    public static void main(String[] args) throws Exception{
        Predict driver = new Predict();

        driver.init();
        driver.train();
        driver.show();
    }
}

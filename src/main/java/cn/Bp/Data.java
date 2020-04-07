package cn.Bp;

import java.util.ArrayList;

public class Data {
    private static final long serialVersionUID = 1L;
    private String name;// 名字
    private ArrayList<Double> data;
    public Data(String s, ArrayList<Double> d) throws Exception{
        this.name = s;
        this.data = d;
    }
    public String getName() throws Exception{
        return name;
    }
    public ArrayList<Double> getData() throws Exception{
        return data;
    }
}

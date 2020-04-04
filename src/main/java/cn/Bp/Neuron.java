package cn.Bp;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

import java.util.ArrayList;

import static java.lang.Math.exp;

public class Neuron {
    public Double bias;
    //public Double diff;
    public ArrayList<Double> weight = new ArrayList<>();

    public Neuron(ArrayList<Double> w, Double b){
        bias = b;
        for(Double d: w){
            weight.add(d);
        }
    }

    public Double feedForward(ArrayList<Double> input){
        assert input.size() == weight.size();
        Double sum = calSum(input);
        return sigmoid(sum);
    }

    public Double calSum(ArrayList<Double> input){
        assert input.size() == weight.size();
        int len = input.size();
        Double ret = 0.0;
        for(int i = 0; i < len; ++i){
            ret += input.get(i) * weight.get(i);
        }
        return ret + bias;
    }

    public static void main(String[] args) throws Exception{

    }

    /*-------------------------------------------------------*/
    public static Double sigmoid(Double d){
        return 1 / (1 + exp(-d));
    }
    public static Double derivSigmoid(Double d){
        Double ret = sigmoid(d);
        return ret * (1 - ret);
    }



    // anonymity class
    public static void create1() {
        ArrayList<Double> w = new ArrayList<Double>(){{
            add(0.0); add(1.1); add(2.2);
        }};
    }

    // init arrayList
    public static ArrayList<Double> createList(Double... e){
        ArrayList<Double> list = new ArrayList<>();
        for(Double d: e){
            list.add(d);
        }
        return list;
    }
}

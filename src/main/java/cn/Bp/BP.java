package cn.Bp;

import java.util.ArrayList;

/*
 * 2 inputs, 2 hidden, 1 output
 * weight:[0, 1], bias:0
 */
public class BP {
    private int INUM;
    private int HNUM;
    private int ONUM;

    //private ArrayList<Neuron> input;
    private ArrayList<Neuron> hide = new ArrayList<>();
    private ArrayList<Neuron> output = new ArrayList<>();

    //private ArrayList<Double> expect;
    private Double learningRate;
    //Double error;
    //String funcName;

    public BP(int i, int h, int o){
        INUM = i; HNUM = h; ONUM = o;
        learningRate = 0.1;
        // for 2 inputs
        ArrayList<Double> weight = Neuron.createList(0.0, 1.0);
        Double bias = 0.0;

        hide.add(new Neuron(weight, bias));
        hide.add(new Neuron(weight, bias));

        output.add(new Neuron(weight, bias));
    }
    public ArrayList<Double> feedForward(String s, ArrayList<Double> input){
        int num = 0;
        ArrayList<Neuron> cur = null;
        if(s.equals("hidden")){
            num = HNUM;
            cur = hide;
            assert input.size() == hide.get(0).weight.size();
        }else if(s.equals("output")){
            num = ONUM;
            cur = output;
            assert input.size() == output.get(0).weight.size();
        }else{
            assert false;
        }
        ArrayList<Double> ret = new ArrayList<>();
        for(int i = 0; i < num; ++i){
            Double out = cur.get(i).feedForward(input);
            ret.add(out);
        }
        return ret;
    }
    public ArrayList<Double> feedForward(ArrayList<Double> input) {
        ArrayList<Double> hideOut = feedForward("hidden", input);
        ArrayList<Double> finalOut = feedForward("output", hideOut);
        return finalOut;
    }

    public void train(ArrayList<ArrayList<Double>> data, ArrayList<Double> expects){
        assert data.size() == expects.size();
        // number of times to loop through the entire dataset
        int epochs = 1000, len = data.size();
        for(int i = 0; i < epochs; ++i){
            for(int j = 0; j < len; ++j){
                ArrayList<Double> input = data.get(j);
                ArrayList<Double> finalOut = feedForward(input);
                ArrayList<Double> hiddenOut = feedForward("hidden", input);
                Double expect = expects.get(j), pred = finalOut.get(0);
                Double dL_dPred = -2.0 * (expect - pred);

                // update output level
                ArrayList<Double> dPred_dHs = new ArrayList<>();
                for(int k = 0; k < ONUM; ++k) {
                    Neuron cur = output.get(k);

                    Double sum = cur.calSum(hiddenOut);
                    Double dPred_dB = Neuron.derivSigmoid(sum);

                    //update output weight
                    for (int wIdx = 0; wIdx < HNUM; ++wIdx) {
                        Double hout = hiddenOut.get(wIdx);
                        Double dPred_dW = hout * Neuron.derivSigmoid(sum);
                        Double oldW = cur.weight.get(wIdx);
                        dPred_dHs.add(oldW * Neuron.derivSigmoid(sum));
                        oldW -= learningRate * dL_dPred * dPred_dW;
                        cur.weight.set(wIdx, oldW);
                    }
                    // update output bias
                    cur.bias -= learningRate * dL_dPred * dPred_dB;
                }

                // update hidden level
                for(int k = 0; k < HNUM; ++k){
                    Neuron cur = hide.get(k);

                    Double sum = cur.calSum(input);
                    Double dH_dB = Neuron.derivSigmoid(sum);

                    Double dPred_dH = dPred_dHs.get(k);
                    // update hidden weight
                    for(int wIdx = 0; wIdx < INUM; ++wIdx){
                        Double in = input.get(wIdx);
                        Double dL_dW = in * Neuron.derivSigmoid(sum);
                        Double oldW = cur.weight.get(wIdx);
                        oldW -= learningRate * dL_dPred * dPred_dH * dL_dW;
                        cur.weight.set(wIdx, oldW);
                    }
                    cur.bias -= learningRate * dL_dPred * dPred_dH * dH_dB;
                }
            }
            if(i % 10 == 0){
                ArrayList<Double> preds = new ArrayList<Double>(){{
                    for(ArrayList<Double> input: data){
                        add(feedForward(input).get(0));
                    }
                }};
                Double loss = mseLoss(preds, expects);
                System.out.printf("loss: %f\n", loss);
            }
        }
    }
    private static Double mseLoss(ArrayList<Double> a, ArrayList<Double> b){
        assert a.size() == b.size();
        int len = a.size();
        Double ret = 0.0;
        for(int i = 0; i < len; ++i){
            Double diff = a.get(i) - b.get(i);
            ret += diff * diff;
        }
        return ret / len;
    }

    public static void main(String[] args) throws Exception {

    }
}

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
    /*------------predict-------------*/
    private Double Min = 1.0;
    private Double Max = 9.0;

    public static BP bp = null;

    private BP(int i, int h, int o){
        INUM = i; HNUM = h; ONUM = o;
        learningRate = 0.1;

        // build hiddden and output weight
        ArrayList<Double> hiddenWeight = new ArrayList<>();
        ArrayList<Double> outputWeight = new ArrayList<>();
        Double base = 0.0, bias = 0.0;
        for(int k = 0; k < INUM; k++){
            hiddenWeight.add(base);
            base += 1.0;
        }
        base = 0.0;
        for(int k = 0; k < HNUM; ++k){
            outputWeight.add(base);
            base += 1.0;
        }

        // build Neuron
        for(int k = 0; k < HNUM; ++k){
            hide.add(new Neuron(hiddenWeight, bias));
        }
        for(int k = 0; k < ONUM; ++k){
            output.add(new Neuron(outputWeight, bias));
        }
    }

    public static synchronized BP getInstance(){
        if(bp == null){
            assert false;
        }
        return bp;
    }

    public static synchronized BP getInstance(int i, int h, int o){
        if(bp == null){
            bp = new BP(i, h, o);
        }
        return bp;
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

    public void NormalizeExpects(ArrayList<Double> expects) {
        Double gap = Max - Min;
        Double k = gap;
        Double b = Min;
        int len = expects.size();
        for(int i = 0; i < len; ++i){
            Double old = expects.get(i);
            expects.set(i, (old - Min) / gap);
        }
    }
    public Double NormalizeExpect(Double expect){
        Double gap = Max - Min;
        Double k = gap;
        Double b = Min;
        return (expect - Min) / gap;
    }

    public void train(ArrayList<ArrayList<Double>> data, ArrayList<Double> expects){
        assert data.size() == expects.size();
        // normalized expects data
        NormalizeExpects(expects);
        // number of times to loop through the entire dataset
        int epochs = 10000, len = data.size();
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
            if(i % 100 == 0){
                ArrayList<Double> preds = new ArrayList<Double>(){{
                    for(ArrayList<Double> input: data){
                        add(feedForward(input).get(0));
                    }
                }};
                Double loss = mseLoss(preds, expects);
                //System.out.printf("loss: %f\n", loss);
            }
        }
    }

    public void trainOne(ArrayList<Double> data, Double e) throws Exception{
        // normalized expects data
        Double expect = NormalizeExpect(e);
        // number of times to loop through the entire dataset
        int epochs = 10000;
        for(int i = 0; i < epochs; ++i){
            ArrayList<Double> input = data;
            ArrayList<Double> finalOut = feedForward(input);
            ArrayList<Double> hiddenOut = feedForward("hidden", input);
            Double pred = finalOut.get(0);
            Double dL_dPred = -2.0 * (expect - pred);

            // update output
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
            if(i % 100 == 0){
                Double predRes = feedForward(input).get(0);
                Double loss = mseLoss(predRes, expect);
                //System.out.printf("loss: %f\n", loss);
            }
        }
    }

    public Double predict(ArrayList<Double> data) {
        ArrayList<Double> ret = bp.feedForward(data);
        return ret.get(0) * (Max - Min) + Min;
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

    private static Double mseLoss(Double a, Double b){
        return (b - a)*(b - a);
    }

    public static void main(String[] args) throws Exception {

    }
}

package cn.Bp;

import java.util.ArrayList;

public class Data {
    private static final long serialVersionUID = 1L;
    private Double predict;
    private Double expect;

    public Data(Double d1, Double d2) throws Exception{
        this.predict = d1;
        this.expect = d2;
    }

    public Double getPredict() {
        return predict;
    }

    public void setPredict(Double predict) {
        this.predict = predict;
    }

    public Double getExpect() {
        return expect;
    }

    public void setExpect(Double expect) {
        this.expect = expect;
    }

}

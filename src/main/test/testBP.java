import org.junit.Before;
import org.junit.Test;

import static cn.Bp.Neuron.createList;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import cn.Bp.BP;
import cn.Bp.Neuron;

public class testBP {
    @Test
    public void testNeuron() throws Exception {
        ArrayList<Double> input = createList(2.0, 3.0);
        ArrayList<Double> weight = createList(0.0, 1.0);
        Double bias = 4.0;
        Neuron n = new Neuron(weight, bias);

        Double res = n.feedForward(input);
        // 0.9990889488055994
        assertTrue(res.equals(0.9990889488055994));
    }
    @Test
    public void testFeedForward() throws Exception{
        BP bp = new BP(2, 2, 1);
        ArrayList<Double> input = createList(2.0, 3.0);
        // { 0.7216325609518421 }
        ArrayList<Double> out = bp.feedForward(input);

        assertTrue(out.get(0).equals(0.7216325609518421));
    }
    @Test
    public void testtrain() throws Exception{
        BP bp = new BP(2, 2, 1);
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        data.add(new ArrayList<Double>(){{add(-2.0); add(-1.0);}});
        data.add(new ArrayList<Double>(){{add(25.0); add(6.0);}});
        data.add(new ArrayList<Double>(){{add(17.0); add(4.0);}});
        data.add(new ArrayList<Double>(){{add(-15.0); add(-6.0);}});
        ArrayList<Double> expects = new ArrayList<Double>(){{
            add(1.0);add(0.0);add(0.0);add(1.0);
        }};

        // train
        bp.train(data, expects);

        // test data
        ArrayList<Double> emily = new ArrayList<Double>(){{
            add(-7.0);add(-3.0);
        }};
        ArrayList<Double> frank = new ArrayList<Double>(){{
            add(20.0);add(2.0);
        }};
        // 0.9677625120477293
        ArrayList<Double> a = bp.feedForward(emily);
        // 0.03871224764189301
        ArrayList<Double> b = bp.feedForward(frank);
        assertTrue(a.get(0).equals(0.9677625120477293));
        assertTrue(b.get(0).equals(0.03871224764189301));
    }
}

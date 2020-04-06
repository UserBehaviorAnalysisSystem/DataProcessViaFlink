import org.junit.Before;
import org.junit.Test;

import static cn.Bp.Neuron.createList;
import static org.junit.Assert.*;

import java.util.ArrayList;

import cn.Bp.BP;
import cn.Bp.Neuron;

public class testBP {
    private ArrayList<ArrayList<Double>> data = new ArrayList<>();
    private ArrayList<ArrayList<Double>> data2 = new ArrayList<>();

    private ArrayList<Double> expects = new ArrayList<>();

    @Before
    public void init() throws Exception{
        data.clear();
        data.add(new ArrayList<Double>(){{add(-2.0); add(-1.0);}});
        data.add(new ArrayList<Double>(){{add(25.0); add(6.0);}});
        data.add(new ArrayList<Double>(){{add(17.0); add(4.0);}});
        data.add(new ArrayList<Double>(){{add(-15.0); add(-6.0);}});

        data2.clear();
        data2.add(new ArrayList<Double>(){{add(-2.0); add(-1.0);add(1.0);}});
        data2.add(new ArrayList<Double>(){{add(25.0); add(6.0);add(1.0);}});
        data2.add(new ArrayList<Double>(){{add(17.0); add(4.0);add(1.0);}});
        data2.add(new ArrayList<Double>(){{add(-15.0); add(-6.0);add(1.0);}});

        expects.clear();
        expects.add(1.0);
        expects.add(0.0);
        expects.add(0.0);
        expects.add(1.0);
    }

    @Test
    public void testNeuron() throws Exception {
        ArrayList<Double> input = createList(2.0, 3.0);
        ArrayList<Double> weight = createList(0.0, 1.0);
        Double bias = 4.0;
        Neuron n = new Neuron(weight, bias);

        Double res = n.feedForward(input);
        assertTrue(res.equals(0.9990889488055994));
    }
    @Test
    public void testFeedForward() throws Exception{
        BP bp = new BP(2, 2, 1);
        ArrayList<Double> input = createList(2.0, 3.0);
        ArrayList<Double> out = bp.feedForward(input);

        assertTrue(out.get(0).equals(0.7216325609518421));
    }

    /*
     * input: 2; hidden: 2; output: 1
     */
    @Test
    public void testTrain1() throws Exception{
        BP bp = new BP(2, 2, 1);
        // train
        bp.train(data, expects);
        // test data
        ArrayList<Double> emily = new ArrayList<Double>(){{
            add(-7.0);add(-3.0);
        }};
        ArrayList<Double> frank = new ArrayList<Double>(){{
            add(20.0);add(2.0);
        }};
        // use my result
        ArrayList<Double> a = bp.feedForward(emily);
        ArrayList<Double> b = bp.feedForward(frank);

        assertTrue(a.get(0).equals(0.989973300630168));
        assertTrue(b.get(0).equals(0.011499476791906249));
    }
    /*
     * input: 3; hidden: 2; output: 1
     */
    @Test
    public void testTrain2() throws Exception{
        BP bp = new BP(3, 2, 1);
        // train
        bp.train(data2, expects);
        // test my data
        ArrayList<Double> emily = new ArrayList<Double>(){{
            add(-7.0);add(-3.0);add(1.0);
        }};
        ArrayList<Double> frank = new ArrayList<Double>(){{
            add(20.0);add(2.0);add(1.0);
        }};
        // use my result
        ArrayList<Double> a = bp.feedForward(emily);
        ArrayList<Double> b = bp.feedForward(frank);

        assertTrue(a.get(0).equals(0.9898177741109315));
        assertTrue(b.get(0).equals(0.011494594860417387));
    }
    /*
     * input: 3; hidden: 6; output: 1
     */
    @Test
    public void testTrain3() throws Exception{
        BP bp = new BP(3, 6, 1);
        // train
        bp.train(data2, expects);
        ArrayList<Double> emily = new ArrayList<Double>(){{
            add(-7.0);add(-3.0);add(2.0);
        }};
        ArrayList<Double> frank = new ArrayList<Double>(){{
            add(20.0);add(2.0);add(2.0);
        }};
        // use my result
        ArrayList<Double> a = bp.feedForward(emily);
        ArrayList<Double> b = bp.feedForward(frank);

        assertTrue(a.get(0).equals(0.9999105781508126));
        assertTrue(b.get(0).equals(0.007061252715411582));
    }
}

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
}

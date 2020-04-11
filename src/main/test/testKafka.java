import cn.csv.CsvOp;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import cn.Kafka.*;

public class testKafka {
    @Test
    public void testKafka() throws Exception{
        ProducerData pro = new ProducerData();
        ConsumerData con = new ConsumerData();

        String data = "zzt1";
        pro.sendMessageForTest(data);
        String res = con.pullDataForTest();

        assertTrue(res.equals("zzt1"));
    }
}

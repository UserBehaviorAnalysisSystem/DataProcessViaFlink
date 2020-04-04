import cn.zzt.UserBehavior;
import cn.zzt.MyClass;
import cn.WindowFunction.ProcessCountUser;
import cn.SinkFunction.SinkToCSV;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Before;
import org.junit.Test;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class test {
    private MyClass m = null;
    @Before
    public void setUp() throws Exception {
        m = new MyClass();
    }
    @Test
    public void testPrint() throws Exception{
        m.print();
    }
    @Test
    public void testAll() throws Exception {
        m.dataSource.timeWindowAll(Time.seconds(2), Time.seconds(1))
                .process(new ProcessCountUser())
                .addSink(new SinkToCSV());
                //.print();

        m.env.execute("countUser");
    }
    @Test
    public void checkDuplicate() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/UserBehavior.csv"));
        String line = null;
        StringBuilder ret = new StringBuilder();
        HashMap<String, Long> map = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            String item[] = line.split(",");//一行数组
            String id = item[0];
            if(map.containsKey(id)){
                System.out.println("find duplicate: " + id + "line:" + line);
                break;
            }else{
                map.put(id, 1L);
            }
        }
        reader.close();
    }
}

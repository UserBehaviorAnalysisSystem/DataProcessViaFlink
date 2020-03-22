import cn.csv.CsvOp;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class testCsv {
    private CsvOp c = null;
    @Before
    public void before(){
        c = new CsvOp();
    }
    @Test
    public void testCsvRead() throws Exception {
        c.read(10);
    }
    @Test
    public void testCsvWrite() throws Exception {
        /*
        * a b c d
        * e f g
        * h i j k l
         */
        String s = "a,b,c,d\ne,f,g\nh,i,j,k,l";
        c.write(s);
        String now = c.read(3, "src/main/resources/out/out1.csv");
        assertTrue(now.equals(s));
    }
    @Test
    public void testCopy() throws Exception {
        c.copyFrom(300);
        String old = c.read(75);
        String now = c.read(75, "src/main/resources/out/out1.csv");
        assertTrue(old.equals(now));
    }
}

import cn.csv.CsvOp;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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
        File f = new File("src/main/resources/out/out1.csv");
        f.delete();
    }
    @Test
    public void testCopy() throws Exception {
        // generate 10000 dataset
        c.copyFrom(10000);
        String old = c.read(75);
        String now = c.read(75, "src/main/resources/out/out1.csv");
        assertTrue(old.equals(now));
        File f = new File("src/main/resources/out/out1.csv");
        f.delete();
    }
}

import org.junit.Before;
import org.junit.Test;
import cn.zzt.MyClass;

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
}

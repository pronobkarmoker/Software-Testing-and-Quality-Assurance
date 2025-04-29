import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyClassTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void div() {
        float expected = 2.0f;
        float actual = (new MyClass()).div(10,5);

        assertEquals(expected, actual, 1e-3);
    }
}
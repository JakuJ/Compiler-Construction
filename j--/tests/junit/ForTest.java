package junit;

import junit.framework.TestCase;
import pass.For;

public class ForTest extends TestCase{
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFor() {
        assertEquals(For.normalFor(5), 5);
        assertEquals(For.noTypeFor(5), 5);
        int[] a = {1,2,3};
        assertEquals(For.enhancedFor(a), 6);
        assertEquals(For.doubleFor(2), 2);
        assertEquals(For.turboFor(5), 19);
        assertEquals(For.emptyFor(5), 6);
    }
}
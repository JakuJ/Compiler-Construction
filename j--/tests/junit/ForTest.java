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
        assertEquals(5, For.normalFor(5));
        assertEquals(5, For.noTypeFor(5));
        int[] a = {1,2,3};
        assertEquals(10, For.doubleFor(2));
        assertEquals(6, For.emptyFor(5));
        assertEquals(6, For.enhancedFor(a));
    }
}
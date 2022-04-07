package junit;

import junit.framework.TestCase;
import pass.Star;

public class ForTest extends TestCase{
    private For f;

    protected void setUp() throws Exception {
        super.setUp();
        f = new For();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFor() {
        this.assertEquals(f.normalFor(5), 5);
        this.assertEquals(f.noTypeFor(5), 5);
        int[] a = {1,2,3};
        this.assertEquals(f.enhancedFor(a), 6);
        this.assertEquals(f.doubleFor(2.0), 2);
        this.assertEquals(f.turboFor(5), 19);
        this.assertEquals(f.emptyFor(5), 6);
    }
}
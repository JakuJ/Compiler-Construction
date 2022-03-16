package junit;

import junit.framework.TestCase;
import pass.Subtraction;

public class SubtractionTest extends TestCase {
    private Subtraction subtraction;

    protected void setUp() throws Exception {
        super.setUp();
        subtraction = new Subtraction();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDivide() {
        this.assertEquals(subtraction.subtract(10, 5), 5);
        this.assertEquals(subtraction.subtract(10, 15), -5);
        this.assertEquals(subtraction.subtract_assign(10, 5), 5);
        this.assertEquals(subtraction.subtract_assign(10, 15), -5);
    }
}

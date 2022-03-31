package junit;

import junit.framework.TestCase;
import pass.Star;

public class StarTest extends TestCase {
    private Star star;

    protected void setUp() throws Exception {
        super.setUp();
        star = new Star();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDivide() {
        this.assertEquals(star.multiply(10, 15), 150);
        this.assertEquals(star.multiply(10, -15), -150);
        this.assertEquals(star.multiply(10, 0), 0);
        this.assertEquals(star.multiply_assign(10, 0), 0);
        this.assertEquals(star.multiply_assign(10, 15), 150);
        this.assertEquals(star.multiply_assign(10, -15), -150);
    }
}

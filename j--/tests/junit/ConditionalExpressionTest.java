package junit;

import junit.framework.TestCase;
import pass.ConditionalExpressions;

public class ConditionalExpressionTest extends TestCase{

    private ConditionalExpressions conditionalExpressions;

    protected void setUp() throws Exception {
        super.setUp();

        conditionalExpressions = new ConditionalExpressions();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testTernary(){
        this.assertEquals(conditionalExpressions.ternaryDoubleInputIfTrue(9 > 6, 2), 4);
        this.assertEquals(conditionalExpressions.ternaryDoubleInputIfTrue(6 > 9, 3), 3);
        this.assertEquals(conditionalExpressions.ternaryDoubleInputIfTrue(false, 7), 7);
        this.assertEquals(conditionalExpressions.ternaryAssignInputIfTrue(true, 7, 1), 1);
    }
}

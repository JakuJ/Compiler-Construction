package junit;

import junit.framework.TestCase;
import pass.LogicalOr;

public class LogicalOrTest  extends TestCase{
    private LogicalOr logicalOr;

    protected void setUp() throws Exception {
        super.setUp();
        logicalOr = new LogicalOr();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLogicalOr(){
        this.assertTrue(logicalOr.logicalOr(2 > 3, 3 > 2));
        this.assertTrue(logicalOr.logicalOr(true, true));
        this.assertFalse(logicalOr.logicalOr(false,false));
    }


}

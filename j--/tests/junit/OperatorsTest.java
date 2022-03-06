package junit;

import junit.framework.TestCase;
import pass.Operators;

public class OperatorsTest extends TestCase {
    private Operators operators;

    protected void setUp() throws Exception {
        super.setUp();
        
        operators = new Operators();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperators() {

        // Shift-wise Operators
        // ----------------------------------------------------
        this.assertEquals(operators.shiftLeft(5, 1), 2);
        this.assertEquals(operators.shiftRight(5, 1), 2);
        this.assertEquals(operators.shiftRightUnsigned(5, 1), 10);

        // Bitwise Operators
        // ----------------------------------------------------
        this.assertEquals(operators.bitwiseAND(5, 3), 1);
        this.assertEquals(operators.bitwiseOR(5, 3), 7);
        this.assertEquals(operators.bitwiseXOR(5, 3), 6);
        this.assertEquals(operators.bitwiseComplement(5), -6);

        // Unary Operators
        // ----------------------------------------------------
        this.assertEquals(operators.unaryPlus(5), 5);
        this.assertEquals(operators.unaryPlus("1"), 1); // The string version of +
        
    }
}

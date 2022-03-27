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
        assertEquals(operators.shiftRight(5, 1), 2);
        assertEquals(operators.shiftLeft(5, 1), 2);
        assertEquals(operators.shiftRightUnsigned(5, 1), 10);

        // Bitwise Operators
        // ----------------------------------------------------
        assertEquals(operators.bitwiseAND(5, 3), 1);
        assertEquals(operators.bitwiseOR(5, 3), 7);
        assertEquals(operators.bitwiseXOR(5, 3), 6);
        assertEquals(operators.bitwiseComplement(5), -6);

        // Unary Operators
        // ----------------------------------------------------
        assertEquals(operators.unaryPlus(5), 5);
        
    }
}

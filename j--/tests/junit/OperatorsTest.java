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
        // Additive Operators
        // ----------------------------------------------------
        assertEquals(operators.minus(10, 5), 5);
        assertEquals(operators.minus(10, 15), -5);

        // Multiplicative Operators
        // ----------------------------------------------------
        assertEquals(2, operators.mod(2, 5));
        assertEquals(0, operators.mod(0, 42));
        assertEquals(0, operators.mod(100, 2));

        assertEquals(operators.multiply(10, 15), 150);
        assertEquals(operators.multiply(10, -15), -150);
        assertEquals(operators.multiply(10, 0), 0);

        assertEquals(operators.divide(0, 42), 0);
        assertEquals(operators.divide(42, 1), 42);
        assertEquals(operators.divide(127, 3), 42);

        // Logical Operators
        // ----------------------------------------------------

        assertTrue(operators.logicalOr(2 > 3, 3 > 2));
        assertTrue(operators.logicalOr(true, true));
        assertFalse(operators.logicalOr(false, false));


        // Shift-wise Operators
        // ----------------------------------------------------
        assertEquals(operators.shiftLeft(5, 1), 2);
        assertEquals(operators.shiftRight(5, 1), 2);
        assertEquals(operators.shiftRightUnsigned(5, 1), 2);

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

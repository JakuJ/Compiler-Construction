package junit;

import junit.framework.TestCase;
import pass.Assignments;

public class AssignmentsTest extends TestCase {
    private Assignments assignments;

    protected void setUp() throws Exception {
        super.setUp();
        assignments = new Assignments();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAssignments() {
        assertEquals(assignments.plus_assign(10, 5), 15);
        assertEquals(assignments.plus_assign(10, 15), 25);

        assertEquals(assignments.minus_assign(10, 5), 5);
        assertEquals(assignments.minus_assign(10, 15), -5);

        assertEquals(assignments.multiply_assign(10, 0), 0);
        assertEquals(assignments.multiply_assign(10, 15), 150);
        assertEquals(assignments.multiply_assign(10, -15), -150);

        assertEquals(0, assignments.mod_assign(0, 42));
        assertEquals(0, assignments.mod_assign(100, 2));

        assertEquals(assignments.divide_assign(42, 1), 42);
        assertEquals(assignments.divide_assign(127, 3), 42);
        assertEquals(assignments.divide_assign(0, 42), 0);
    }


}

package junit;

import junit.framework.TestCase;
import pass.Modulo;

public class ModuloTest extends TestCase {
    private Modulo modulo;

    protected void setUp() throws Exception {
        super.setUp();
        modulo = new Modulo();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMod() {
        assertEquals(2, modulo.mod(2, 5));
        assertEquals(0, modulo.mod(0, 42));
        assertEquals(0, modulo.mod(100, 2));
        assertEquals(0, modulo.mod_assign(0, 42));
        assertEquals(0, modulo.mod_assign(100, 2));
    }
    
}

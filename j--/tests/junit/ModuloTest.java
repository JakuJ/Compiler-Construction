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
        this.assertEquals(modulo.mod(2, 5), 2);
        this.assertEquals(modulo.mod(0, 42), 0);
        this.assertEquals(modulo.mod(100, 2), 0);
    }

}

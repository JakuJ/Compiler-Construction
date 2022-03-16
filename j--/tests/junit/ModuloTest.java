package junit;

import junit.framework.TestCase;
import pass.Modulo;

public class ModuloTest extends TestCase {
    private Modulo modulus;

    protected void setUp() throws Exception {
        super.setUp();
        modulus = new Modulo();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testModulus() {
        this.assertEquals(modulus.modulo(10, 5), 0);
        this.assertEquals(modulus.modulo(10, 15), 5);
        this.assertEquals(modulus.modulo_assign(10, 5), 0);
        this.assertEquals(modulus.modulo_assign(10, 15), 5);
    }
    
}

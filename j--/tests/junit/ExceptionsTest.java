package junit;

import junit.framework.TestCase;
import pass.Exceptions;
import java.lang.NullPointerException;

public class ExceptionsTest extends TestCase {

    public void testExceptions(){
        assertEquals(Exceptions.testTryCatch(), "Exception caught successfully!" + "Finally reached successfully!");
        try {
            Exceptions.testThrows();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            fail();
        }
    }
}

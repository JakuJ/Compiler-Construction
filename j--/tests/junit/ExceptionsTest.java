package junit;

import junit.framework.TestCase;
import pass.Exceptions;
import java.lang.NullPointerException;

public class ExceptionsTest extends TestCase {

    public void testExceptions(){
        assertEquals("Exception caught successfully!" + "Index 5 out of bounds for length 3" +  "Finally reached successfully!", Exceptions.testTryCatch());
        try {
            Exceptions.testThrows();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            fail();
        }
    }
}

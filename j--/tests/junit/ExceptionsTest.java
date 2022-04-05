package junit;

import junit.framework.TestCase;
import pass.Exception;

public class ExceptionsTest extends TestCase {

    public void testExceptions(){
        this.assertEquals(Exceptions.testTryCatch(), "Exception caught successfully!" + "Finally reached successfully!");
        this.assertEquals(Exceptions.testThrows(), NullPointerException);
    }
}

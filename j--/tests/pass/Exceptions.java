package pass;

import java.lang.StringBuilder;

public class Exceptions{

    public String testTryCatch() {

        StringBuilder message;

        try {
            char[] letters = { 'a', 'b', 'c' };

            System.out.println(letters[5]); // ArrayIndexOutOfBoundsException

        } catch (Exception e) {
            message.append("Exception caught successfully!");

        } finally {
            message.append("Finally reached successfully!");
        }

        return message.toString();
    }

    public void testThrows() throws NullPointerException {
        boolean test = true;

        if (test) {
            throw new NullPointerException();
        }
    }
}

public interface testInterface {
    public void interfaceMethod() throws Exception;
}

public class testClass implements testInterface {
    int[] list;

    public testClass(int[] list) throws NullPointerException {
        if(list != null){
            this.list = list;
        } else {
            throw new NullPointerException();
        }
    }

    public void interfaceMethod() throws Exception {
        int i = 0;
        throw new Exception();
    }
} 
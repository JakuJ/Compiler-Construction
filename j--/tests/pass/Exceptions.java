package pass;

import java.lang.StringBuilder;
import java.lang.NullPointerException;
import java.lang.Exception;
import java.lang.System;

public class Exceptions{

    public static String testTryCatch() {

        StringBuilder message = new StringBuilder();

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

    public static void testThrows() throws NullPointerException {
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
        if((Object)list != null){
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
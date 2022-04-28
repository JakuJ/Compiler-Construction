package pass;

import java.lang.System;

/**
 * Dummy class to test the scanning of the reserved words.
 * Does not include "const" and "goto".
 */
public abstract class ReservedWords extends Object implements someInterface {
    final boolean booleanField = true;
    private byte byteField;
    protected char charField;
    static double doubleField;
    float floatField;
    int intField;
    long longField;
    short shortField;
    transient int transientIntField;
    volatile int volatileInt;

    abstract void abstractMethod();

    public boolean isObject(Object o) {
        if (o instanceof Object) {
            return true;
        } else {
            return false;
        }

    }

    public void counter(int counter) {
        do {
            try {
                counter++;
            } catch (Exception e) {
                System.out.println("something went wrong");
            } finally {
                System.out.println(counter);
            }
        } while (counter != 10);
    }

    public void typePrinter(String type) {
        // TODO: SUPPORT SWITCH STATEMENTS
        // switch (type) {
        //    case "boolean":
        //        System.out.println(this.booleanField);
        //        break;
        //    case "byte":
        //        System.out.println(this.byteField);
        //    default:
        //        System.out.println("Type does not exist");
        // }
    }

    public void listPrinter() throws RuntimeException {
        String[] list = {};
        for (String i : list) {
            if (i.equals("Break here")) {
                break;
            } else if (i.equals("Don't print me")) {
                continue;
            } else if (i.equals("Error")) {
                throw new RuntimeException("Something went wrong");
            } else {
                System.out.println(i);
            }
        }
    }

    public synchronized void synchronisedMethod() {
        System.out.println("I'm synchronized");
        ;
    }

//    public native void nativeMethod();


}

strictfp class StrictFPClass {
    double n1 = 10e+102;
    double n2 = 6e+08;

    double calculate() {
        return n1 + n2;
    }
}

class Pet { // Superclass (parent)
    public void petSound() {
        System.out.println("I'm a pet");
    }
}

class Dog extends Pet { // Subclass (child)
    public void animalSound() {
        super.petSound();
    }
}


interface someInterface {
    public void interfaceMethod();
}

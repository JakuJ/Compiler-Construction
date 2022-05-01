package pass;

import java.lang.System;

public class Interfaces implements FirstInterface, SecondInterface {
    public void firstMethod() {
        System.out.println(1);
    }

    public void secondMethod() {
        System.out.println(2);
    }
}

interface FirstInterface {

    public void firstMethod();

}

interface SecondInterface {
    public void secondMethod();

}

class Main {
    public static void main(String[] args) {
        Interfaces interfaces = new Interfaces();

        interfaces.firstMethod();
        interfaces.secondMethod();
    }
}
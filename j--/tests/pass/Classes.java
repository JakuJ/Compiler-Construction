package pass;

import java.lang.System;

public class Classes {

    public void tryBlocks() {
        Blocks a = new Blocks(1);
        Blocks b = new Blocks(2);
        Blocks c = new Blocks(3);
    }

    int x = 10;

    public static String message() {
        return A.a + ", " + (new B()).b;
    }

    public static void main(String[] args) {
        System.out.println(Classes.message()); // Expect: "Hello, World!"
        
        Car car = new Car();
        car.honk();

        System.out.println(car.brand + " " + car.model); // Expect: 'Ford Mustang'
    }

}

class Vehicle {
    protected String brand = "Ford";

    public void honk(){
        System.out.println("HONK!");
    }
}

class Car extends Vehicle {
    public String model = "Mustang";
}

class A {

    public static String a = "Hello";

}

class B {

    public String b = "World!";

}

class Blocks {

    private int i;

    public Blocks(int i) {
        this.i = i;
    }

    static {
        System.out.println("I only get called once");
    }

    {
        System.out.println("I have been called multiple times!");
    }
}

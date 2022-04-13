package fail;

import java.lang.System;

public class Classes {

   
   public static void main(String[] args) {
       Car car = new Car();
       car.honk();
   }

}

final class Vehicle {
    protected String brand = "Ford";

    public void honk() {
        System.out.println("HONK!");
    }
}

class Car extends Vehicle {
    public String model = "Mustang";
}
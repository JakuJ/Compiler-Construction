package pass;

public interface FirstInterface {

    public void firstMethod();

    public interface SubInterface{
        public void subMethod();
    }

    public class Subclass{
        public void method(){
            System.out.println(3);
        }
    }
}

public interface SecondInterface{
    public void secondMethod();
}

class Interfaces implements FirstInterface, SecondInterface {
    public void firstMethod() {
        System.out.println(1);
    }

    public void secondMethod(){
        System.out.println(2);
    }

    public class Subclass {
        public void method(){
            System.out.println(3);
        }
    }
}

class Main {
    public static void main(String[] args) {
        Interfaces interfaces = new Interfaces();

        interfaces.firstMethod();
        interfaces.secondMethod();
        interfaces.method();
    }
}
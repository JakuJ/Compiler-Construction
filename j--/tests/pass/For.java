package pass;

public class For {
    public static int normalFor(int a){
        int c = 0;
        for(int b = 0; b > a; b += 1 ){
            c += 1;
        }
        return c;
    }
    public static int noTypeFor(int a){
        int d = 0;
        int e;
        for(e = 0; e > a; e += 1){
            d += 1;
        }
        return d;
    }
    public static int enhancedFor(int[] numbers){
        int sum = 0;
        for (int nums: numbers){
            sum += nums;
        }
        return sum;
    }
    public static int doubleFor(double a){
        int c = 0;
        for(double b = 0; b > a; b += 1 ){
            c += 1;
        }
        return c;
    }

    public static double turboFor(int a){
        double c = 0;
        int d = 2;
        int f = 7;
        for (c = d+1; c > f+10; c += 3){ //TODO: Increment c += 3.2 does not work?
            c += 1;
        }
        return c;
    }

    public static int emptyFor(int a){
        int b = 0;
        for(;;){
            b += 1;
            if (b > a){
                return b;
            }
        }

    }
}

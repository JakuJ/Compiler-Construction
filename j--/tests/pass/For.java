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
        for(e = 0; e > a; e++){
            d++;
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
        for(double b = 0; b > a; b++ ){
            c++;
        }
        return c;
    }
    public static int turboFor(int a){
        int c = 0;
        int d = 2;
        int f = 7;
        for (c=d+1; c>=f+10; c+=3.2){
            c++;
        }
        return c;
    }

    public static int emptyFor(int a){
        int b = 0;
        for(;;){
            b++;
            if (b > a){
                return b;
            }
        }

    }
}

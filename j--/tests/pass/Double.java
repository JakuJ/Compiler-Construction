package pass;

public class Double {
    private double field;
    private float field2;

    public double method(float arg1, double arg2) {
        double a = 3.14;
        a = 3.14d;
        float b = .14;
        b = .14f;
        double c = 0.12;
        c = 0.12D;
        float d = 1.;
        d = 1.F;
        double e = 1.000001;
        return a + b - c / d + arg1 - arg2 + e;
    }

    public static double doubleVariableAllocation(double a){
        double b = 0.0;
        int e1 = 0;
        double c = 0.0;
        int e2 = 0;
        double d = 0.0;
        int e3 = 0;
        return a + b + c + d;
    }
}

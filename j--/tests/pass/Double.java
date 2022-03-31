package pass;

public class Double {
    private double field;
    private float field2;

    public double method(float arg1, double arg2) {
        double a = 3.14;
        float b = .14;
        double c = 0.12;
        float d = 1.;
        double e = 1.000001;
        return a + b - c / d + arg1 - arg2 + e;
    }

}

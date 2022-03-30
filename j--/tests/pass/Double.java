package pass;

public class Double {
    private double field;

    public double method(double argument) {
        double a = 3.14;
        double b = .14;
        double c = 0.12;
        double d = 1.;
        double e = 1.000001;
        return a + b - c / d * (double)2 + argument + e;
    }

}

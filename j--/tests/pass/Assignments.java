package pass;

public class Assignments {
    public int plus_assign(int x, int y) {
        x += y;
        return x;
    }

    public int minus_assign(int x, int y) {
        x -= y;
        return x;
    }

    public int multiply_assign(int a, int b) {
        a *= b;
        return a;
    }

    public int divide_assign(int x, int y) {
        x /= y;
        return x;
    }

    public int mod_assign(int x, int y) {
        x %= y;
        return x;
    }

    public int shiftr_assign(int x, int y) {
        x >>= y;
        return x;
    }

    public int shiftl_assign(int x, int y) {
        x <<= y;
        return x;
    }

    public int ushiftr_assign(int x, int y) {
        x >>>= y;
        return x;
    }

    public int bit_and_assign(int x, int y) {
        x &= y;
        return x;
    }

    public int bit_or_assign(int x, int y) {
        x |= y;
        return x;
    }

    public int xor_assign(int x, int y) {
        x ^= y;
        return x;
    }
}

package pass;


public class Operators {

    // Comparison Operators
    // ----------------------------------------------------

    public boolean equal(int a, int b) {
        return a == b;
    }

    public boolean notEqual(int a, int b) {
        return a != b;
    }

    public boolean lessThan(int x, int y) {
        return x < y;
    }

    public boolean greaterThan(int x, int y) {
        return x > y;
    }

    public boolean lessThanOrEqual(int x, int y) {
        return x <= y;
    }

    public boolean greaterThanOrEqual(int x, int y) {
        return x >= y;
    }

    // Logical Operators
    // ----------------------------------------------------

    public boolean logicalOr(boolean x, boolean y) {
        return x || y;
    }

    public boolean logicalAnd(boolean x, boolean y) {
        return x && y;
    }

    // Multiplicative Operators
    // ----------------------------------------------------

    public int divide(int x, int y) {
        return x / y;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int mod(int x, int y) {
        return x % y;
    }

    // Additive Operators
    // ----------------------------------------------------

    public int plus(int x, int y) {
        return x + y;
    }

    public int minus(int x, int y) {
        return x - y;
    }

    // Shift-wise Operators
    // ---------------------------------------------------- 

    public int shiftRight(int num, int places) {
        return num >> places;
    }

    public int shiftRightUnsigned(int num, int places) {
        return num >>> places;
    }

    public int shiftLeft(int num, int places) {
        return num << places;
    }


    // Bitwise Operators 
    // ----------------------------------------------------

    public int bitwiseOR(int a, int b) {
        return a | b;
    }

    public int bitwiseXOR(int a, int b) {
        return a ^ b;
    }

    public int bitwiseAND(int a, int b) {
        return a & b;
    }


    // Unary Operators
    // ----------------------------------------------------

    public int unaryPlus(int num) {
        return +num;
    }

    public int bitwiseComplement(int num) {
        return ~num;
    }
}

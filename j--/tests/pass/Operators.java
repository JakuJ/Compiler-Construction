package pass;


/**
 * This class was made to test the operators implemented in Step 0
 * 
 * shift-wise operators:    << >> >>>
 * bit-wise operators:      - | ^ &
 * unary operators          + ~
 * 
 */
public class Operators {
    
    // Shift-wise Operators
    // ---------------------------------------------------- 

    public int shiftRight(int num, int places){
        return num >> places;
    }

    public int shiftRightUnsigned(int num, int places){
        return num >>> places;
    }

    public int shiftLeft(int num, int places){
        return num << places;
    }


    // Bitwise Operators 
    // ----------------------------------------------------

    public int bitwiseOR(int a, int b){
        return a | b ;
    }

    public int bitwiseXOR(int a, int b) {
        return a ^ b;
    }

    public int bitwiseAND(int a, int b) {
        return a & b;
    }


    // Unary Operators
    // ----------------------------------------------------
    public int unaryPlus(int num){
        return +num;
    }

    public int bitwiseComplement(int num) {
        return ~num;
    }
}

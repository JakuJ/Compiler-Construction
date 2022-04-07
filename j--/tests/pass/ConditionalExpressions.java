package pass;

public class ConditionalExpressions {
    public int ternaryDoubleInputIfTrue(boolean cond, int input){
        return cond ? input * 2 : input;
    }

    public int ternaryAssignInputIfTrueElseDouble(boolean cond, int input, int newValue){
        return cond ? input = newValue : input*2;
    }
}

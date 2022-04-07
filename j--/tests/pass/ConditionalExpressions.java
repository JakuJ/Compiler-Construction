package pass;

public class ConditionalExpressions {
    public int ternaryDoubleInputIfTrue(boolean cond, int input){
        return cond ? input * 2 : input;
    }

    public int ternaryAssignInputIfTrue(boolean cond, int input, int newValue){
        return cond ? input = newValue : input;
    }
}

package pass;

public class Modulo {
    public int mod(int x, int y){
        return x % y;
    }
    public int mod_assign(int x, int y) {
        x %= y;
        return x;
    }
}

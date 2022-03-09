package fail;

import java.security.Principal;

import javax.print.event.PrintEvent;

public class SubtractionAssign {
    public static void main(String[] args) {
        int a = 10;
        a -= 'a';
        System.out.println(a);
    }
}

package pass;

import java.lang.System;

public class ForStatements {
    public static void main(String[] args) {
        int MAX_LOOP_COUNT = 3;

        String[] words = {
            "one",
            "two",
            "three"
        };
        
        double k = 1.;
        int h = 2;
        int y = 6;

        for (int i = 0; i < MAX_LOOP_COUNT; i++) {
            System.out.println("I'm looping: loop=" + i);
        }

        for (String word : words) {
            System.out.println("I'm also looping: loop=" + word);
        }

        for(float x = k + 1.0; x <= (double)(h + 10); x += 3.2){
            System.out.println("I'm crazy looping: loop=" + x);
        }

        for (int i = 6; i > MAX_LOOP_COUNT && y >= i;) {
            i--;
            System.out.println(i);
        }

        for (;;) {
            System.out.println("What?");
        }
    }
}

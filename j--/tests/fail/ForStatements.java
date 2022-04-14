package fail;

public class ForStatements {
    public static void main(String[] args) {
        int MAX_LOOP_COUNT = 3;

        String[] words = {
            "one",
            "two",
            "three"
        };
        
        int k = 1;
        int h = 2;

        for (int i = 0: i < MAX_LOOP_COUNT; i++) {
            System.out.println("I'm looping: loop=" + i);
        }

        for (String word ; words) {
            System.out.println("I'm also looping: loop=" + word);
        }

        for (;;) {
            System.out.println("What?");
        }

        for(){
            System.out.println("Im unreachable");
        }
    }
}

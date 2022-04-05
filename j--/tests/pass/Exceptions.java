package pass;

public class Exceptions{

    public string testTryCatch() {

        StringBuilder message;

        try {
            char[] letters = { a, b, c };

            System.out.println(letters[5]); // ArrayIndexOutOfBoundsException

        } catch (Exception e) {
            message.append("Exception caught successfully!");

        } finally {
            message.append("Finally reached successfully!");
        }

        return message.toString();
    }

    public void testThrows() throws NullPointerException {
        boolean test = true;

        if (test) {
            throw new NullPointerException();
        }
    }
}

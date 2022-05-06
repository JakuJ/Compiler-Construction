import java.io.FileReader;
import java.io.FileWriter;
import java.lang.IndexOutOfBoundsException;
import java.lang.System;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Copy {
    private static final int EOF = -1; // end of file character rep .

    public static void main(String[] args) throws IOException {
        FileReader inStream = null;
        FileWriter outStream = null;
        int ch;
        try {
// open the files
            inStream = new FileReader(args[0]);
            outStream = new FileWriter(args[1]);
// copy
            while ((ch = inStream.read()) != EOF) {
                outStream.write(ch);
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println(
                    " usage : java Copy1 sourcefile targetfile ");
        } catch (FileNotFoundException e) {
            System.err.println(e.toString()); // rely on e's toString ()
        } catch (IOException e) {
            System.err.println(e.toString());
        } finally { // close the files
            inStream.close();
            outStream.close();
        }
    }

    void foo()
            throws IOException {
        throw new IOException();
    }
}
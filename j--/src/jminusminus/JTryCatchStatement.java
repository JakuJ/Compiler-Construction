package jminusminus;

import java.util.ArrayList;

/**
 * The AST node for a try-catch finally statement
 */

class JTryCatchStatement extends JStatement {

    private JBlock body_try, body_finally;

    private ArrayList<JCatchClause> catches;

    private JFormalParameter param;

    /**
     * Constructs an AST node for a try-catch-finally-statement given its line number,
     * the bodies for each of the parts (if present) and the parameters to the catch
     * statement (if catch is present).
     * 
     * @param line the line at which the try is caught
     * @param body_try mandatory contains the body to the try
     * @param catches mandatory contains the catch clauses
     * @param body_finally only optional if there is no catch body, contains the body to the finally
     */
    public JTryCatchStatement(int line, JBlock body_try, ArrayList<JCatchClause> catches, JBlock body_finally) {
        super(line);
        this.body_try = body_try;
        this.catches = catches;
        this.body_finally = body_finally;
    }


    public JWhileStatement analyze(Context context) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }


    public void codegen(CLEmitter output) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    /**
     * Formats how the try catch statement will be printed to console.
     */
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JTryCatchStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<Body>\n"); // Try Body
        p.indentRight();
        body_try.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");

        if (catches != null) {
            for (JCatchClause clause : catches) {
                clause.writeToStdOut(p);
            }
        }

        if(body_finally != null){
            p.printf("<Finally>\n"); // Finally
            p.indentRight();
            p.printf("<Body>\n");
            p.indentRight();
            body_try.writeToStdOut(p);
            p.indentLeft();
            p.printf("</Body>\n");
            p.indentLeft();
            p.printf("</Finally>\n");

        }
        
        p.indentLeft();
        p.printf("</JTryCatchStatement>\n");
    }

}

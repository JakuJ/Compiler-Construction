package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a try-catch finally statement
 */

class JTryCatchStatement extends JStatement {

    private JBlock body_try;

    private JFinallyBlock body_finally;

    private ArrayList<JCatchClause> catches;
    /**
     * Constructs an AST node for a try-catch-finally-statement given its line number,
     * the bodies for each of the parts (if present) and the parameters to the catch
     * statement (if catch is present).
     *
     * @param line         the line at which the try is caught
     * @param body_try     mandatory contains the body to the try
     * @param catches      mandatory contains the catch clauses
     * @param body_finally only optional if there is no catch body, contains the body to the finally
     */
    public JTryCatchStatement(int line, JBlock body_try, ArrayList<JCatchClause> catches, JBlock body_finally) {
        super(line);
        this.body_try = body_try;
        this.catches = catches;
        this.body_finally = new JFinallyBlock(body_finally);
    }


    public JTryCatchStatement analyze(Context context) {
        body_try = body_try.analyze(context);
        if (body_finally != null) {
            body_finally = body_finally.analyze(context);
        }
        if (catches != null) {
            for (var clause : catches) {
                clause.analyze(context);
            }
        }
        return this;
    }


    public void codegen(CLEmitter output) {
        String startLabel = output.createLabel();
        String endLabel = output.createLabel();
        String afterTryCatch = output.createLabel();

        output.addLabel(startLabel);
        body_try.codegen(output);
        output.addLabel(endLabel);

        if (body_finally != null) {
            body_finally.codegen(output);
        }
        output.addBranchInstruction(GOTO, afterTryCatch);

        if (catches != null) {
            for (JCatchClause catchClause : catches) {
                Type exceptionType = catchClause.getParam().type();
                String handleLabel = output.createLabel();
                output.addLabel(handleLabel);
                output.addExceptionHandler(startLabel, endLabel, handleLabel, exceptionType.jvmName());
                catchClause.codegen(output);
                if (body_finally != null) {
                    body_finally.codegen(output);
                }
                output.addBranchInstruction(GOTO, afterTryCatch);
            }
        }

        if (body_finally != null) {
            String handleLabel = output.createLabel();
            output.addLabel(handleLabel);
            output.addExceptionHandler(startLabel, endLabel, handleLabel, null);
            body_finally.codegenUncaught(output);
        }

        output.addLabel(afterTryCatch);
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

        if (body_finally != null) {
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

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a while-statement.
 */

class JForStatement extends JStatement {

    /** Initialization of variable  */
    private JExpression initialization;
    
    /** Termination condition  */
    private JExpression termination;

    /** Incrementation statement  */
    private JExpression increment;

    /** Identifier for collection (Only for enchanced for loop)  */
    private String name;

    /** The body. */
    private JStatement body;

    /**
     * Constructs an AST node for a for-statement given its line number, the
     * test expression, and the body.
     * 
     * @param line
     *            line in which the while-statement occurs in the source file.
     * @param initialization
     *            Initialization of variable
     * @param termination
     *            Termination condition
     * @param increment
     *            Incrementation statement
     * @param body
     *            the body.
     */

    public JForStatement(int line, JExpression initialization, JExpression termination, JExpression increment, JStatement body) {
        super(line);
        this.initialization = initialization;
        this.termination = termination;
        this.increment = increment;
        this.body = body;
    }

    public JForStatement(int line, JExpression initialization, String name, JStatement body) {
        super(line);
        this.initialization = initialization;
        this.name = name;
        this.body = body;
    }

    /**
     * Analysis involves analyzing the test, checking its type and analyzing the
     * body statement.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JWhileStatement analyze(Context context) {
        condition = condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        body = (JStatement) body.analyze(context);
        return this;
    }

    /**
     * Generates code for the while loop.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        // Need two labels
        String test = output.createLabel();
        String out = output.createLabel();

        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        condition.codegen(output, out, false);

        // Codegen body
        body.codegen(output);

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JWhileStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<TestExpression>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TestExpression>\n");
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JWhileStatement>\n");
    }

}
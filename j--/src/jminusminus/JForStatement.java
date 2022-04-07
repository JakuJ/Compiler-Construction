package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a while-statement.
 */

class JForStatement extends JStatement {

    /** Initialization of variable  */
    private JStatement initialization;

    /** Initialization of variable (used for enhanced for loop) */
    private JVariableDeclarator init;
    
    /** Termination condition  */
    private JExpression termination;

    /** Incrementation statement  */
    private JExpression increment;

    /** Collection (Only for enchanced for loop)  */
    private JExpression arr;

    /** The body. */
    private JStatement body;

    /**
     * Constructs an AST node for a for-statement given its line number, the initialization expression, 
     * termination condition, increment expression and the body
     * 
     * @param line
     *            line in which the while-statement occurs in the source file.
     * @param initialization
     *            Initialization of variable
     * @param termination
     *            Termination condition
     * @param increment
     *            Increment expression
     * @param body
     *            the body.
     */

    public JForStatement(int line, JStatement initialization, JExpression termination, JExpression increment, JStatement body) {
        super(line);
        this.initialization = initialization;
        this.termination = termination;
        this.increment = increment;
        this.body = body;
    }

    /**
     * Constructs an AST node for an enhanced for-statement given its line number, the initialization of a variable, 
     * the collection to iterate over and the body.
     * 
     * @param line
     *            line in which the for-statement occurs in the source file.
     * @param init
     *            Initialization of variable
     * @param arr
     *            Collection
     * @param body
     *            the body.
     */
    public JForStatement(int line, JVariableDeclarator init, JExpression arr, JStatement body) {
        super(line);
        this.init = init;
        this.arr = arr;
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

    public JForStatement analyze(Context context) {
        

        termination = termination.analyze(context);
        termination.type().mustMatchExpected(line(), Type.BOOLEAN);

        increment = increment.analyze(context);
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
        //TODO
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<TestExpression>\n");
        p.indentRight();
        //condition.writeToStdOut(p);
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
package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a while-statement.
 */

class JForStatement extends JStatement {

    /** Initialization of variable  */
    private JVariableDeclarator initialization;

    /** Initialization of variable (used for enhanced for loop) */
    private JStatement init;
    
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

    public JForStatement(int line, JVariableDeclarator initialization, JExpression termination, JExpression increment, JStatement body) {
        super(line);
        this.initialization = initialization;
        this.termination = termination;
        this.increment = increment;
        this.body = body;
    }

    public JForStatement(int line, JStatement init, JExpression termination, JExpression increment, JStatement body) {
        super(line);
        this.init = init;
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
    public JForStatement(int line, JVariableDeclarator initialization, JExpression arr, JStatement body) {
        super(line);
        this.initialization = initialization;
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
        if (initialization != null){
            initialization = initialization.analyze(context);
        } else {
            init = (JStatement) init.analyze(context);
        }
        if (termination != null){
            termination = termination.analyze(context);
            termination.type().mustMatchExpected(line(), Type.BOOLEAN);
        } else if (arr != null) {
            arr = arr.analyze(context);
        }
        if (increment != null){
            increment = increment.analyze(context);
        }
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
        p.printf("<Init>\n");
        p.indentRight();
        if(init != null){
            init.writeToStdOut(p);
        } else {
            initialization.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</Init>\n");
        if (termination != null){
            p.printf("<Termination>\n");
            p.indentRight();
            termination.writeToStdOut(p);
            p.indentLeft();
            p.printf("</Termination>\n");
        } else if (arr != null) {
            p.printf("<Collection>\n");
            p.indentRight();
            arr.writeToStdOut(p);
            p.indentLeft();
            p.printf("</Collection>\n");
        } else {
            p.printf("<Termination>\n");
            p.indentRight();
            p.printf("<No expression>\n");
            p.indentLeft();
            p.printf("</Termination>\n");
        }
        if (increment != null){
            p.printf("<Increment>\n");
            p.indentRight();
            increment.writeToStdOut(p);
            p.indentLeft();
            p.printf("</Increment>\n");
        } else if (arr == null){
            p.printf("<Increment>\n");
            p.indentRight();
            p.printf("<No expression>\n");
            p.indentLeft();
            p.printf("</Increment>\n");
        }
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.printf("</JForStatement>\n");
    }

}
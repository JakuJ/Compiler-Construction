package jminusminus;

/**
 * The AST node for a do-while-statement.
 */

class JDoWhileStatement extends JStatement {

    /**
     * Test expression.
     */
    private JExpression condition;

    /**
     * The body.
     */
    private JStatement body;

    /**
     * Constructs an AST node for a do-while-statement given its line number, the
     * test expression, and the body.
     *
     * @param line      line in which the do-while-statement occurs in the source file.
     * @param condition test expression.
     * @param body      the body.
     */

    public JDoWhileStatement(int line, JExpression condition, JStatement body) {
        super(line);
        this.condition = condition;
        this.body = body;
    }

    /**
     * Analysis involves analyzing the test, checking its type and analyzing the
     * body statement.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JDoWhileStatement analyze(Context context) {
        condition = condition.analyze(context);
        body.analyze(context);
        return this;
    }

    /**
     * Generates code for the while loop.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        // TODO: CODEGEN
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JDoWhileStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.printf("<TestExpression>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TestExpression>\n");
        p.indentLeft();
        p.printf("</JDoWhileStatement>\n");
    }

}

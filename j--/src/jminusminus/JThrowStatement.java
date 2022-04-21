package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a throw statement.
 */

class JThrowStatement extends JStatement {

   
    private JExpression expression;

    /** Constructor for a throw statement AST node.
     * Only takes in the line at which the statement was found
     * and the expression it is expressing (throw new Exception).
     * 
     * @param line line number at which the statement was found 
     * @param expression the expression (throw 'expression')
     */
    public JThrowStatement(int line, JExpression expression) {
        super(line);
        this.expression = expression;
    }

    public JWhileStatement analyze(Context context) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    public void codegen(CLEmitter output) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    /**
     * Formats how the statement will be printed to console.
     */
    public void writeToStdOut(PrettyPrinter p) {
        if(expression != null){
            p.printf("<JThrowStatement line=\"%d\">\n", line());
            p.indentRight();
            expression.writeToStdOut(p);
            p.indentLeft();
            p.printf("</JThrowStatement>\n");
        } else {
            p.printf("<JThrowStatement line=\"%d\"/>\n", line());
        }
    }

}

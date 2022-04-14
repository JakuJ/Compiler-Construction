package jminusminus;

public class JForeachStatement extends JStatement {
    JFormalParameter parameter;
    JExpression rhs;
    JStatement body;

    public JForeachStatement(int line,
                             JFormalParameter parameter,
                             JExpression rhs,
                             JStatement body) {
        super(line);
        this.parameter = parameter;
        this.rhs = rhs;
        this.body = body;
    }

    public JForeachStatement analyze(Context context) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    public void codegen(CLEmitter output) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    /**
     * {@inheritDoc}
     */
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForeachStatement line=\"%d\">\n", line());
        p.indentRight();
        parameter.writeToStdOut(p);
        p.indentLeft();
        p.println("<Collection>");
        p.indentRight();
        rhs.writeToStdOut(p);
        p.indentLeft();
        p.println("</Collection>");
        p.println("<Body>");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.println("</Body>");
        p.indentLeft();
        p.println("</JForeachStatement>");
    }
}

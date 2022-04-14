package jminusminus;

import static jminusminus.CLConstants.IREM;

public class JConditionalExpression extends JExpression{
    protected JExpression condition;
    protected JExpression thenBranch;
    protected JExpression elseBranch;

    public JConditionalExpression(int line, JExpression condition, JExpression thenBranch, JExpression elseBranch){
        super(line);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public JExpression analyze(Context context) {
        condition = (JExpression) condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        if(thenBranch != null){
            thenBranch = (JExpression) thenBranch.analyze(context);
            elseBranch = (JExpression) elseBranch.analyze(context);
            thenBranch.type().mustMatchExpected(line(), elseBranch.type());
            type = thenBranch.type();
        } else {
            type = condition.type();
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JConditionalExpression line=\"%d\" type=\"%s\">\n",line(), ((type == null) ? "" : type.toString()));
        p.indentRight();
        p.printf("<condition>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</condition>\n");
        p.printf("<thenBranch>\n");
        p.indentRight();
        thenBranch.writeToStdOut(p);
        p.indentLeft();
        p.printf("</thenBranch>\n");
        p.printf("<elseBranch>\n");
        p.indentRight();
        elseBranch.writeToStdOut(p);
        p.indentLeft();
        p.printf("</elseBranch>\n");
        p.indentLeft();
        p.printf("</JConditionalExpression>\n");

    }
}

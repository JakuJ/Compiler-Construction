package jminusminus;

public class JCatchClause extends JAST implements JMember {

    private JFormalParameter param;
    private JBlock body;

    public JCatchClause(int line, JFormalParameter param, JBlock body) {
        super(line);
        this.param = param;
        this.body = body;
    }

    public JFormalParameter getParam() {
        return param;
    }

    public JBlock getBody() {
        return body;
    }

    @Override
    public void preAnalyze(Context context, CLEmitter partial) {
        // TODO: PREANALYZE
    }

    @Override
    public JCatchClause analyze(Context context) {
        param.analyze(context);
        body.analyze(context);
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        param.codegen(output);
        body.codegen(output);
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JCatchClause> line=\"%d\">\n", line);
        p.indentRight();
        param.writeToStdOut(p);
        body.writeToStdOut(p);
        p.indentLeft();
        p.println("</JCatchClause>");
    }
}

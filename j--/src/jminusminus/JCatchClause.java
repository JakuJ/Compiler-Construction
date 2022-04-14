package jminusminus;

public class JCatchClause extends JAST implements JMember {

    private JFormalParameter param;
    private JBlock body;

    public JCatchClause(int line, JFormalParameter param, JBlock body) {
        super(line);
        this.param = param;
        this.body = body;
    }

    @Override
    public void preAnalyze(Context context, CLEmitter partial) {
        // TODO: PREANALYZE
    }

    @Override
    public JAST analyze(Context context) {
        // TODO: ANALYZE
        return null;
    }

    @Override
    public void codegen(CLEmitter output) {
        // TODO: CODEGEN
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

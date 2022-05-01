package jminusminus;

public class JInitializationBlock extends JAST implements JMember {

    private JBlock body;
    private final boolean isStatic;

    public JInitializationBlock(JBlock body, boolean isStatic) {
        super(body.line);
        this.body = body;
        this.isStatic = isStatic;
    }

    @Override
    public void preAnalyze(Context context, CLEmitter partial) {
        // TODO: PREANALYZE
    }

    @Override
    public JInitializationBlock analyze(Context context) {
        body = body.analyze(context);
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        // TODO: CODEGEN
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInitializationBlock line=\"%d\" isStatic=\"%b\">\n", line, isStatic);
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</JInitializationBlock>\n");
    }
}

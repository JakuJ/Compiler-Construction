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
    }

    @Override
    public JAST analyze(Context context) {
        // The analysis on a static and initialization block are the same as JBlock

        Context lc = new LocalContext(context);

        body.analyze(lc);

        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        // The codegen on a static and initialization block are the same as JBlock.
        
        body.codegen(output);
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

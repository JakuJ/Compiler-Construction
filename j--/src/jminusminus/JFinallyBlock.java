package jminusminus;

import static jminusminus.CLConstants.*;

public class JFinallyBlock extends JAST implements JMember {

    private LocalVariableDefn exception;
    private JBlock body;

    public JFinallyBlock(JBlock body) {
        super(body.line);
        this.body = body;
    }

    public JBlock getBody() {
        return body;
    }

    @Override
    public void preAnalyze(Context context, CLEmitter partial) {
        // do nothing
    }

    @Override
    public JFinallyBlock analyze(Context context) {
        // Declare the parameter inside the catch block
        var c = new LocalContext(context);

        exception = new LocalVariableDefn(Type.typeFor(Exception.class), c.nextOffset());
        exception.initialize();

        c.addEntry(body.line(), "finally_exception", exception);
        body.analyze(c);
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        body.codegen(output);
    }

    public void codegenUncaught(CLEmitter output) {
        output.addOneArgInstruction(ASTORE, exception.offset());

        body.codegen(output);

        output.addOneArgInstruction(ALOAD, exception.offset());
        output.addNoArgInstruction(ATHROW);
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        body.writeToStdOut(p);
    }
}

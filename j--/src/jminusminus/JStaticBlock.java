package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

public class JStaticBlock extends JBlock implements JMember {

    /** List of statements forming the block body. */
    private ArrayList<JStatement> statements;

    /**
     * The new context (built in analyze()) represented by this block.
     */
    private LocalContext context;

    public JStaticBlock(int line, ArrayList<JStatement> statements) {
        super(line, statements);
        this.statements = statements;
    }

    @Override
    public void preAnalyze(Context context, CLEmitter partial) {
            // not used
    }

    /**
     * Returns the list of statements comprising the block.
     * 
     * @return list of statements.
     */

    public ArrayList<JStatement> statements() {
        return statements;
    }

    /**
     * Analyzing a block consists of creating a new nested context for that
     * block and analyzing each of its statements within that context.
     * 
     * @param context
     *                context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JBlock analyze(Context context) {
        // { ... } defines a new level of scope.
        this.context = new LocalContext(context);

        for (int i = 0; i < statements.size(); i++) {
            statements.set(i, (JStatement) statements.get(i).analyze(
                    this.context));
        }
        return this;
    }

    /**
     * The codegen of the static blocks is done inside
     * the codegen of the class declaration.
     * 
     * The code of a static block goes in the same place as a static initialization inside the secret method <clinit>.
     * 
     * @param output
     *               the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        // This will be called inside the codegenClassInit() inside the JClassDeclaration

        for (JStatement jStatement : statements) {
            jStatement.codegen(output);
        }

        // Failed Attempt
        // String clint = JClassDeclaration.label;

        // boolean needReturn = false;

        // // Checks if the static method has been created
        // if (label == null) {
        //     if (clint == null) {
        //         // The static method has not been created yet so it needs to make it.
        //         ArrayList<String> mods = new ArrayList<String>();
        //         mods.add("public");
        //         mods.add("static");
        //         output.addMethod(mods, "<clinit>", "()V", null, false);
        //         updateLabel(output.createLabel());
        //     } else {
        //         // The static method has been created so just needs to get the label to jump to.
        //         updateLabel(clint);
        //     }

        // } else {
        //     output.addLabel(label);
        // }
        
        // /**
        
        // for (JStatement statement : statements) {
        //     statement.codegen(output);
        // }

        // // The return statement has to be made if not yet made.
        // if (needReturn) {
        //     // Return
        //     output.addNoArgInstruction(RETURN);
        // }

    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JStaticBlock line=\"%d\">\n", line);
        if (context != null) {
            p.indentRight();
            context.writeToStdOut(p);
            p.indentLeft();
        }
        for (JStatement statement : statements) {
            p.indentRight();
            statement.writeToStdOut(p);
            p.indentLeft();
        }
        p.printf("</JStaticBlock>\n");
    }

}
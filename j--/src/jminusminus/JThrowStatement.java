package jminusminus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public JThrowStatement analyze(Context context) {
        expression = expression.analyze(context);
        Type typ = expression.type();

        if (!Type.THROWABLE.isJavaAssignableFrom(typ)) {
            JAST.compilationUnit.reportSemanticError(line, "Throw type must be of type Throwable: \"%s\"", typ.toString());
        }

        Type[] types = context.methodContext().methodThrowTypes().toArray(new Type[0]);
        var resolved = Arrays.stream(types).map(x -> x.resolve(context)).collect(Collectors.toList());

        typ.mustMatchOneOf(expression.line(), resolved.toArray(new Type[0]));

        return this;
    }

    public void codegen(CLEmitter output) {
        expression.codegen(output);
        output.addNoArgInstruction(ATHROW);
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

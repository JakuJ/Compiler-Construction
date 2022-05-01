package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

public class JForStatement extends JStatement {

    /**
     * The for loop initializer for the loop declaration commonly follows format: int i=0
     */
    JForInit forInit;

    /**
     * The for loop update for the loop declaration commonly follows format: i++
     */
    ArrayList<JStatement> forUpdate;

    /**
     * The body of the for loop statement
     */
    JStatement body;

    /**
     * The expression of the for loop declaration commonly follows format: i < MAX_LOOP_COUNT
     */
    JExpression expression;

    public JForStatement(int line,
                         JForInit forInit,
                         JExpression expression,
                         ArrayList<JStatement> forUpdate,
                         JStatement body) {
        super(line);
        this.forInit = forInit;
        this.forUpdate = forUpdate;
        this.body = body;
        this.expression = expression;
    }

    public JForStatement analyze(Context context) {
        LocalContext lContext = new LocalContext(context);
        if(forInit != null){
            forInit.analyze(lContext);
        }
        
        // for update analyze
        if(forUpdate != null){
            for(JStatement statement : forUpdate){
                statement.analyze(lContext);
            }
        }
        if(expression != null){
            expression.analyze(lContext);
        }
        if(body != null){
            body.analyze(lContext);
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        String test = output.createLabel();
        String out = output.createLabel();

        if(forInit.isStatementExpression){
            for(JStatement s : forInit.statements){
                s.codegen(output);
            }
        }
        else{
            for(JVariableDeclarator v : forInit.variableDeclarators){
                v.codegen(output);
            }
        }

        output.addLabel(test);
        expression.codegen(output, out, false);
        
        body.codegen(output);
        
        for(JStatement s: forUpdate){
            s.codegen(output);
        }

        output.addBranchInstruction(GOTO, test);

        output.addLabel(out);
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForStatement line=\"%d\">\n", line());
        p.indentRight();
        if (forInit != null) {
            p.indentRight();
            p.println("<JForInit>");

            // Get what type of forInit it is
            if (forInit.isStatementExpression) {
                for (JStatement statement : forInit.statements) {
                    p.indentRight();
                    statement.writeToStdOut(p);
                    p.indentLeft();
                }
            } else {
                for (JVariableDeclarator variable : forInit.variableDeclarators) {
                    p.indentRight();
                    variable.writeToStdOut(p);
                    p.indentLeft();
                }
            }

            p.println("</JForInit>");
            p.indentLeft();
        }

        if (expression != null) {
            p.indentRight();
            p.println("<JForExpression>");
            p.indentRight();
            expression.writeToStdOut(p);
            p.indentLeft();
            p.println("</JForExpression>");
            p.indentLeft();
        }

        if (forUpdate != null) {
            p.indentRight();
            p.println("<JForUpdate>");
            p.indentRight();
            for (JStatement statement : forUpdate) {
                p.indentRight();
                statement.writeToStdOut(p);
                p.indentLeft();
            }
            p.indentLeft();
            p.println("</JForUpdate>");
            p.indentLeft();
        }

        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");
        p.indentLeft();
        p.println("</JForStatement>");
    }
}

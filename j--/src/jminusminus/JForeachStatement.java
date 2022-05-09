package jminusminus;

import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

public class JForeachStatement extends JStatement {
    JFormalParameter parameter;
    JExpression expression;
    JStatement body;

    public JForeachStatement(int line,
                             JFormalParameter parameter,
                             JExpression expression,
                             JStatement body) {
        super(line);
        this.parameter = parameter;
        this.expression = expression;
        this.body = body;
    }

    public JBlock analyze(Context context) {
        LocalContext lContext = new LocalContext(context);
        parameter.analyze(lContext);

        expression.analyze(lContext);

        if (!Type.ITERABLE.isJavaAssignableFrom(expression.type()) && !expression.type().isArray()) {
            JAST.compilationUnit.reportSemanticError(line,
                    "Local variable must be of type array or iterable: \"%s\"", expression.type().toString());
        }

        parameter.type().mustMatchExpected(line, expression.type().componentType());

        // First, check for shadowing
        IDefn previousDefn = context.lookup(parameter.name());
        if (previousDefn instanceof LocalVariableDefn) {
            JAST.compilationUnit.reportSemanticError(parameter.line(), "The name " + parameter.name() + " overshadows another local variable.");
        }

        // All initializations must be turned into assignment statements and analyzed
        ArrayList<JStatement> statements = new ArrayList<JStatement>();

        if (expression.type().isArray()) {
            // Create T[] a ’ = Expression ; iterable
            String arrayName = createUniqueName(parameter.name(), context);
            JVariableDeclarator arrayDecl = new JVariableDeclarator(line(), arrayName, expression.type(), expression);
            ArrayList<JVariableDeclarator> decls = new ArrayList<>();
            decls.add(arrayDecl);
            JVariableDeclaration arrayDeclaration = new JVariableDeclaration(line(), new ArrayList<>(), decls);
            statements.add(arrayDeclaration);

            //Create int i ’ = 0; the iterator
            String iteratorName = createUniqueName("iterator", context);
            JVariableDeclarator init = new JVariableDeclarator(parameter.line(), iteratorName, Type.INT, new JLiteralInt(line(), "0"));
            ArrayList<JVariableDeclarator> initDecls = new ArrayList<JVariableDeclarator>();
            initDecls.add(init);
            JForInit initStmt = new JForInit(initDecls, null);

            // Create i ’ < a ’. length ; the condition
            JExpression lhs = new JVariable(line(), iteratorName);
            JExpression rhs = new JFieldSelection(line(), new JVariable(line(), arrayName), "length");
            JExpression condition = new JLessThanOp(line(), lhs, rhs);

            // Create i ’ = i ’ + 1; the update
            JPostIncrementOp incrementOp = new JPostIncrementOp(line(), lhs);
            ArrayList<JStatement> update = new ArrayList<JStatement>();
            update.add(new JStatementExpression(line(), incrementOp));

            // Create Type Identifier = a ’[i ’]; update the variable
            JVariableDeclarator updateDecl = new JVariableDeclarator(line(), parameter.name(), parameter.type(), new JArrayExpression(line(), new JVariable(line(), arrayName), new JVariable(line(), iteratorName)));
            ArrayList<JVariableDeclarator> updateDecls = new ArrayList<JVariableDeclarator>();
            updateDecls.add(updateDecl);
            JVariableDeclaration updateDeclaration = new JVariableDeclaration(line(), new ArrayList<String>(), updateDecls);

            ArrayList<JStatement> bodyStatements = new ArrayList<JStatement>();
            bodyStatements.add(updateDeclaration);
            bodyStatements.add(body);
            JBlock bodyBlock = new JBlock(line(), bodyStatements);

            // Create the for loop
            JForStatement forLoop = new JForStatement(line(), initStmt, condition, update, bodyBlock);

            // Add the loop to statements
            statements.add(forLoop);
        } else {
            // Create I i’ = Expression.iterator(); the iterator
            String iteratorName = createUniqueName("iterator", context);
            JVariableDeclarator init = new JVariableDeclarator(line(), iteratorName, Type.typeFor(Iterator.class), new JMessageExpression(line(), new JVariable(line(), iteratorName), "iterator", new ArrayList<JExpression>()));
            ArrayList<JVariableDeclarator> initDecls = new ArrayList<JVariableDeclarator>();
            initDecls.add(init);
            JForInit initStmt = new JForInit(new ArrayList<JVariableDeclarator>(initDecls), null);

            // Create i’. hasNext(); the condition
            JExpression condition = new JMessageExpression(line(), new JVariable(line(), iteratorName), "hasNext", new ArrayList<JExpression>());

            // Update statement is empty
            // Create Type Identifier = i’.next (); update statement in the body
            JVariableDeclarator updateDecl = new JVariableDeclarator(line(), parameter.name(), parameter.type(), new JMessageExpression(line(), new JVariable(line(), iteratorName), "next", new ArrayList<JExpression>()));
            ArrayList<JVariableDeclarator> updateDecls = new ArrayList<JVariableDeclarator>();
            updateDecls.add(updateDecl);
            JVariableDeclaration updateDeclaration = new JVariableDeclaration(line(), new ArrayList<String>(), updateDecls);

            ArrayList<JStatement> bodyStatements = new ArrayList<JStatement>();
            bodyStatements.add(updateDeclaration);
            bodyStatements.add(body);
            JBlock bodyBlock = new JBlock(line(), bodyStatements);

            // Create the for loop
            JForStatement forLoop = new JForStatement(line(), initStmt, condition, null, bodyBlock);

            // Add the loop to statements
            statements.add(forLoop);
        }

        //Create a block to hold all the statements
        JBlock forBlock = new JBlock(line(), statements);
        //Analyze the block
        forBlock.analyze(lContext);
        //Return complete for loop block
        return forBlock;
    }

    private String createUniqueName(String name, Context context) {
        String uniqueName = "$" + name;
        while (context.lookup(uniqueName) != null) {
            uniqueName += "_";
        }
        return uniqueName;
    }

    public void codegen(CLEmitter output) {
        // Not needed, this is done in JForStatement
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
        expression.writeToStdOut(p);
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

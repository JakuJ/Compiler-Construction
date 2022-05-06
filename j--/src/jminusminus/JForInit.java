package jminusminus;

import java.util.ArrayList;

/**
 * This class is only here so that the method in the Parser called forInit can return two values
 *
 * @see Parser
 */
public class JForInit {

    public boolean isStatementExpression;

    public ArrayList<JVariableDeclarator> variableDeclarators;

    public ArrayList<JStatement> statements;

    public JForInit(ArrayList<JVariableDeclarator> variableDeclarators, ArrayList<JStatement> statements) {
        this.variableDeclarators = variableDeclarators;
        this.statements = statements;

        isStatementExpression = variableDeclarators == null;

        if (this.statements == null) {
            this.statements = new ArrayList<>();
        }

        if (this.variableDeclarators == null) {
            this.variableDeclarators = new ArrayList<>();
        }

    }

    public JForInit analyze(Context context) {
        for (JStatement statement : statements) {
            statement.analyze(context);
        }
        
        if(variableDeclarators != null){
            statements = new ArrayList<JStatement>();
            for (JVariableDeclarator decl : variableDeclarators) {
                // Local variables are declared here (fields are declared in preAnalyze())
                int offset = ((LocalContext) context).nextOffset();
                var defn = new LocalVariableDefn(decl.type().resolve(context), offset);
    
                // First, check for shadowing
                IDefn previousDefn = context.lookup(decl.name());
                if (previousDefn instanceof LocalVariableDefn) {
                    JAST.compilationUnit.reportSemanticError(decl.line(), "The name " + decl.name() + " overshadows another local variable.");
                }
    
                // Then declare it in the local context
                context.addEntry(decl.line(), decl.name(), defn);
    
                // All initializations must be turned into assignment statements and analyzed
                if (decl.initializer() != null) {
                    defn.initialize();
                    JAssign assignOp = new JAssign(decl.line(), new JVariable(decl.line(), decl.name()), decl.initializer());
                    assignOp.isStatementExpression = true;
                    statements.add(new JStatementExpression(decl.line(), assignOp).analyze(context));
                }
            }
        }
       

        return this;
    }
}

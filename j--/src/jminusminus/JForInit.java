package jminusminus;

import java.util.ArrayList;

/**
 * This class is only here so that the method in the Parser called forInit can return two values 
 * 
 * @see Parser.java
 */
public class JForInit {

    public boolean isStatementExpression;

    public ArrayList<JVariableDeclarator> variableDeclarators;

    public ArrayList<JStatement> statements;

    public JForInit(ArrayList<JVariableDeclarator> variableDeclarators, ArrayList<JStatement> statements){
        this.variableDeclarators = variableDeclarators;
        this.statements = statements;

        isStatementExpression = (variableDeclarators == null) ? true : false;
    }
}

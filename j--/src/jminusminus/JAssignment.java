// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * This abstract base class is the AST node for an assignment statement.
 */

abstract class JAssignment extends JBinaryExpression {

    /**
     * Constructs an AST node for an assignment operation.
     *
     * @param line     line in which the assignment operation occurs in the source
     *                 file.
     * @param operator the actual assignment operator.
     * @param lhs      the lhs operand.
     * @param rhs      the rhs operand.
     */

    public JAssignment(int line, String operator, JExpression lhs, JExpression rhs) {
        super(line, operator, lhs, rhs);
    }

}

/**
 * The AST node for an assignment (=) expression. The = operator has two
 * operands: a lhs and a rhs.
 */

class JAssign extends JAssignment {

    /**
     * Constructs the AST node for an assignment (=) expression given the lhs and
     * rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  lhs operand.
     * @param rhs  rhs operand.
     */

    public JAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, checking that types match, and sets the result
     * type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        rhs.type().mustMatchExpected(line(), lhs.type());
        type = rhs.type();
        if (lhs instanceof JVariable) {
            IDefn defn = ((JVariable) lhs).iDefn();
            if (defn != null) {
                // Local variable; consider it to be initialized now.
                ((LocalVariableDefn) defn).initialize();
            }
        }
        return this;
    }

    /**
     * Code generation for an assignment involves, generating code for loading
     * any necessary Lvalue onto the stack, for loading the Rvalue, for (unless
     * a statement) copying the Rvalue to its proper place on the stack, and for
     * doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        rhs.codegen(output);
        if (!isStatementExpression) {
            // Generate code to leave the Rvalue atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a += expression. A += expression has two operands: a lhs and
 * a rhs.
 */

class JPlusAssign extends JAssignment {

    /**
     * Constructs the AST node for a += expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JPlusAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "+=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, rewrites rhs as lhs + rhs (string concatenation)
     * if lhs is of type {@code String}, and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type().mustMatchExpected(line(), Type.INT);
            type = Type.INT;
        } else if (lhs.type().equals(Type.DOUBLE)) {
            rhs.type().mustMatchExpected(line(), Type.DOUBLE);
            type = Type.DOUBLE;
        } else if (lhs.type().equals(Type.STRING)) {
            rhs = (new JStringConcatenationOp(line, lhs, rhs)).analyze(context);
            type = Type.STRING;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for +=: " + lhs.type());
        }
        return this;
    }

    /**
     * Code generation for += involves, generating code for loading any
     * necessary l-value onto the stack, for (unless a string concatenation)
     * loading the r-value, for (unless a statement) copying the r-value to its
     * proper place on the stack, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        if (lhs.type().equals(Type.STRING)) {
            rhs.codegen(output);
        } else {
            ((JLhs) lhs).codegenLoadLhsRvalue(output);
            rhs.codegen(output);
            output.addNoArgInstruction(lhs.type().equals(Type.INT) ? IADD : DADD);
        }
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a -= expression. A -= expression has two operands: a lhs and
 * a rhs.
 */

class JMinusAssign extends JAssignment {

    /**
     * Constructs the AST node for a -= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JMinusAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "-=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else if (lhs.type().equals(Type.DOUBLE)) {
            rhs.type().mustMatchExpected(line(), Type.DOUBLE);
            type = Type.DOUBLE;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for -=: " + lhs.type());
        }
        return this;
    }

    /**
     * Code generation for -= involves, generating code for loading any
     * necessary l-value onto the stack, for loading the r-value, for (unless a
     * statement) copying the r-value to its
     * proper place on the stack, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(lhs.type().equals(Type.INT) ? ISUB : DSUB);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a *= expression. A *= expression has two operands: a lhs and
 * a rhs.
 */

class JStarAssign extends JAssignment {

    /**
     * Constructs the AST node for a *= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JStarAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "*=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchOneOf(line, Type.INT, Type.DOUBLE);
            type = Type.INT;

        } else if (lhs.type().equals(Type.DOUBLE)) {
            rhs.type().mustMatchExpected(line(), Type.DOUBLE);
            type = Type.DOUBLE;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for *=: " + lhs.type());
        }
        return this;
    }

    /**
     * Code generation for *= involves, generating code for loading any
     * necessary l-value onto the stack, for loading the r-value, for (unless a
     * statement) copying the r-value to its
     * proper place on the stack, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(lhs.type().equals(Type.INT) ? IMUL : DMUL);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a /= expression.
 * A /= expression has two operands: a lhs and a rhs.
 */

class JDivAssign extends JAssignment {

    /**
     * Constructs the AST node for a /= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JDivAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "/=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchOneOf(line, Type.INT, Type.DOUBLE);
            type = Type.INT;

        } else if (lhs.type().equals(Type.DOUBLE)) {
            rhs.type().mustMatchExpected(line(), Type.DOUBLE);
            type = Type.DOUBLE;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for /=: " + lhs.type());
        }
        return this;
    }

    /**
     * Code generation for /= involves, generating code for loading any
     * necessary l-value onto the stack, for loading the r-value, for (unless a
     * statement) copying the r-value to its
     * proper place on the stack, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(lhs.type().equals(Type.INT) ? IDIV : DDIV);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a %= expression.
 * A %= expression has two operands: a lhs and
 * a rhs.
 */

class JModAssign extends JAssignment {

    /**
     * Constructs the AST node for a %= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JModAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "%=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for %=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IREM);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a >>= expression.
 * An assign expression has two operands: a lhs and
 * a rhs.
 */

class JShiftrAssign extends JAssignment {

    /**
     * Constructs the AST node for a %= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JShiftrAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, ">>=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for >>=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(ISHR);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a >>>= expression.
 * An assign expression has two operands: a lhs and
 * a rhs.
 */

class JUshiftrAssign extends JAssignment {

    /**
     * Constructs the AST node for a >>>= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JUshiftrAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, ">>>=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for >>>=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IUSHR);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a <<= expression.
 * An assign expression has two operands: a lhs and
 * a rhs.
 */

class JShiftlAssign extends JAssignment {

    /**
     * Constructs the AST node for a <<= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JShiftlAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "<<=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for <<=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(ISHL);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a &= expression.
 * An assign expression has two operands: a lhs and
 * a rhs.
 */

class JBitAndAssign extends JAssignment {

    /**
     * Constructs the AST node for a &= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JBitAndAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "&=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for &=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IAND);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a |= expression.
 * An assign expression has two operands: a lhs and
 * a rhs.
 */

class JBitOrAssign extends JAssignment {

    /**
     * Constructs the AST node for a |= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JBitOrAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "|=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        } else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for |=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IOR);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a ^= expression.
 * An assign expression has two operands: a lhs and
 * a rhs.
 */

class JXorAssign extends JAssignment {

    /**
     * Constructs the AST node for a ^= expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JXorAssign(int line, JExpression lhs, JExpression rhs) {
        super(line, "^=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.INT)) {
            rhs.type.mustMatchExpected(line, Type.INT);
            type = Type.INT;
        }else {
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for ^=: " + lhs.type());
        }
        return this;
    }

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);
        rhs.codegen(output);
        output.addNoArgInstruction(IXOR);
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

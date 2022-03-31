package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * This class generates the AST node for: SHIFTL (<<) , SHIFTR (>>), USHIFTR
 * (>>>)
 * 
 * Since this class is an assignment operator it expects two operands
 * 
 * a LHS and a RHS (num << num)
 */
abstract class JBitwiseExpression extends JExpression {
    /** The binary operator. */
    protected String operator;

    /** The lhs operand. */
    protected JExpression lhs;

    /** The rhs operand. */
    protected JExpression rhs;

    /**
     * Constructs an AST node for a binary expression given its line number, the
     * binary operator, and lhs and rhs operands.
     * 
     * @param line
     *            line in which the binary expression occurs in the source file.
     * @param operator
     *            the binary operator.
     * @param lhs
     *            the lhs operand.
     * @param rhs
     *            the rhs operand.
     */

    protected JBitwiseExpression(int line, String operator, JExpression lhs,
            JExpression rhs) {
        super(line);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JBitwiseExpression line=\"%d\" type=\"%s\" "
                + "operator=\"%s\">\n", line(),
                ((type == null) ? ""
                        : type
                                .toString()),
                Util.escapeSpecialXMLChars(operator));
        p.indentRight();
        p.printf("<Lhs>\n");
        p.indentRight();
        lhs.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Lhs>\n");
        p.printf("<Rhs>\n");
        p.indentRight();
        rhs.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Rhs>\n");
        p.indentLeft();
        p.printf("</JBitwiseExpression>\n");
    }
}

/**
 * The AST node for an OR (|) expression.
 */
class JInclusiveOrOp extends JBitwiseExpression {
    public JInclusiveOrOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "|", lhs, rhs);
    }

    /**
     * Analyzing the << operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * TODO: Add double checking
     * 
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.INT);
        rhs.type().mustMatchExpected(line(), Type.INT);
        type = Type.INT;
        return this;
    }

    // TODO: Step 5
    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
    }
}

/**
 * The AST node for a XOR (^) expression.
 */
class JExclusiveOrOp extends JBitwiseExpression {
    public JExclusiveOrOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "^", lhs, rhs);
    }

    /**
     * Analyzing the ^ operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * TODO: Add double checking
     * 
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.INT);
        rhs.type().mustMatchExpected(line(), Type.INT);
        type = Type.INT;
        return this;
    }

    // TODO: Step 5
    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
    }
}

/**
 * The AST node for an AND (&) expression.
 */
class JAndOp extends JBitwiseExpression {
    public JAndOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "&", lhs, rhs);
    }

    /**
     * Analyzing the & operation involves analyzing its operands, checking
     * types, and determining the result type.
     * 
     * TODO: Add double checking
     * 
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JExpression analyze(Context context) {
        lhs = (JExpression) lhs.analyze(context);
        rhs = (JExpression) rhs.analyze(context);
        lhs.type().mustMatchExpected(line(), Type.INT);
        rhs.type().mustMatchExpected(line(), Type.INT);
        type = Type.INT;
        return this;
    }

    // TODO: Step 5
    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
    }
}

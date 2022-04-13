// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

// This file was modified for use in the DTU course 02247 Compiler Construction

package jminusminus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import java.util.Hashtable;

import javax.lang.model.element.Element;

import static jminusminus.TokenKind.*;

/**
 * A lexical analyzer for j--, that has no backtracking mechanism.
 * <p>
 * When you add a new token to the scanner, you must also add an entry in the
 * {@link TokenKind} enum in {@code TokenInfo.java} specifying the kind and
 * image of the new token.
 * <p>
 * See Appendix C.2.1 of the textbook or the
 * <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html">Java
 * Language Specifications</a>
 * for the full lexical grammar.
 */

class Scanner {

    /** End of file character. */
    public final static char EOFCH = CharReader.EOFCH;

    /** Keywords in j--. */
    private Hashtable<String, TokenKind> reserved;

    /** Source characters. */
    private CharReader input;

    /** Next unscanned character. */
    private char ch;

    /** Whether a scanner error has been found. */
    private boolean isInError;

    /** Source file name. */
    private String fileName;

    /** Line number of current token. */
    private int line;

    /**
     * Constructs a Scanner object.
     * 
     * @param fileName
     *                 the name of the file containing the source.
     * @exception FileNotFoundException
     *                                  when the named file cannot be found.
     */

    public Scanner(String fileName) throws FileNotFoundException {
        this.input = new CharReader(fileName);
        this.fileName = fileName;
        isInError = false;

        // Keywords in j--
        reserved = new Hashtable<String, TokenKind>();
        reserved.put(ABSTRACT.image(), ABSTRACT);
        reserved.put(BOOLEAN.image(), BOOLEAN);
        reserved.put(BREAK.image(), BREAK);
        reserved.put(BYTE.image(), BYTE);
        reserved.put(CASE.image(), CASE);
        reserved.put(CATCH.image(), CATCH);
        reserved.put(CHAR.image(), CHAR);
        reserved.put(CLASS.image(), CLASS);
        reserved.put(CONST.image(), CONST);
        reserved.put(CONTINUE.image(), CONTINUE);
        reserved.put(DEFAULT.image(), DEFAULT);
        reserved.put(DO.image(), DO);
        reserved.put(DOUBLE.image(), DOUBLE);
        reserved.put(ELSE.image(), ELSE);
        reserved.put(EXTENDS.image(), EXTENDS);
        reserved.put(FINAL.image(), FINAL);
        reserved.put(FINALLY.image(), FINALLY);
        reserved.put(FLOAT.image(), FLOAT);
        reserved.put(FOR.image(), FOR);
        reserved.put(FALSE.image(), FALSE);
        reserved.put(GOTO.image(), GOTO);
        reserved.put(IF.image(), IF);
        reserved.put(IMPLEMENTS.image(), IMPLEMENTS);
        reserved.put(IMPORT.image(), IMPORT);
        reserved.put(INSTANCEOF.image(), INSTANCEOF);
        reserved.put(INT.image(), INT);
        reserved.put(INTERFACE.image(), INTERFACE);
        reserved.put(LONG.image(), LONG);
        reserved.put(NATIVE.image(), NATIVE);
        reserved.put(NEW.image(), NEW);
        reserved.put(NULL.image(), NULL);
        reserved.put(PACKAGE.image(), PACKAGE);
        reserved.put(PRIVATE.image(), PRIVATE);
        reserved.put(PROTECTED.image(), PROTECTED);
        reserved.put(PUBLIC.image(), PUBLIC);
        reserved.put(RETURN.image(), RETURN);
        reserved.put(SHORT.image(), SHORT);
        reserved.put(STATIC.image(), STATIC);
        reserved.put(STRICTFP.image(), STRICTFP);
        reserved.put(SUPER.image(), SUPER);
        reserved.put(SWITCH.image(), SWITCH);
        reserved.put(SYNCHRONIZED.image(), SYNCHRONIZED);
        reserved.put(THIS.image(), THIS);
        reserved.put(THROW.image(), THROW);
        reserved.put(THROWS.image(), THROWS);
        reserved.put(TRANSIENT.image(), TRANSIENT);
        reserved.put(TRY.image(), TRY);
        reserved.put(TRUE.image(), TRUE);
        reserved.put(VOID.image(), VOID);
        reserved.put(WHILE.image(), WHILE);

        // Prime the pump.
        nextCh();
    }

    /**
     * Scans the next token from input.
     * 
     * @return the next scanned token.
     */

    public TokenInfo getNextToken() {
        StringBuffer buffer;
        boolean moreWhiteSpace = true;
        while (moreWhiteSpace) {
            while (isWhitespace(ch)) {
                nextCh();
            }
            if (ch == '/') {
                nextCh();
                if (ch == '/') {
                    // CharReader maps all new lines to '\n'
                    while (ch != '\n' && ch != EOFCH) {
                        nextCh();
                    }
                }

                // The addition of multi-line comments
                else if (ch == '*') {
                    nextCh();

                    // Loop until the end of the file to find the ending of the multi-line comment
                    while (ch != EOFCH) {
                        nextCh();

                        // Find the second *
                        if (ch == '*') {
                            nextCh();

                            // Check if it's the end of the comment else keep looping until another * is
                            // found
                            if (ch == '/') {
                                // The end of the comment has been found
                                nextCh();
                                break; // break the search
                            }
                        }
                    }
                } else if (ch == '=') {
                    nextCh();
                    return new TokenInfo(DIV_ASSIGN, line); // Token: '/='
                } else {
                    return new TokenInfo(DIV, line); // Token: '/'
                }
            } else {
                moreWhiteSpace = false;
            }
        }
        line = input.line();
        switch (ch) {
            case '(':
                nextCh();
                return new TokenInfo(LPAREN, line); // Token: '('
            case ')':
                nextCh();
                return new TokenInfo(RPAREN, line); // Token: ')'
            case '{':
                nextCh();
                return new TokenInfo(LCURLY, line); // Token: '{'
            case '}':
                nextCh();
                return new TokenInfo(RCURLY, line); // Token: '}'
            case '[':
                nextCh();
                return new TokenInfo(LBRACK, line); // Token: '['
            case ']':
                nextCh();
                return new TokenInfo(RBRACK, line); // Token: ']'
            case ';':
                nextCh();
                return new TokenInfo(SEMI, line); // Token: ';'
            case ':':
                nextCh();
                return new TokenInfo(COLON, line); // Token: ':'
            case '~':
                nextCh();
                return new TokenInfo(TILDE, line); // Token: '~'
            case ',':
                nextCh();
                return new TokenInfo(COMMA, line); // Token: ','
            case '=':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(EQ, line); // Token: '=='
                } else {
                    return new TokenInfo(ASSIGN, line); // Token: '='
                }
            case '!':
                nextCh();
                if(ch == '='){
                    nextCh();
                    return new TokenInfo(NEQ, line); // Token: '!='
                } else {
                    return new TokenInfo(LNOT, line); // Token: '!'
                }
            case '?':
                nextCh();
                return new TokenInfo(TERNARY, line); // Token: '?'
            case '*':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(STAR_ASSIGN, line); // Token: '*='
                } else {
                    return new TokenInfo(STAR, line); // Token: '*'
                }
            case '%':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(MOD_ASSIGN, line); // Token: '%='
                } else {
                    return new TokenInfo(MOD, line); // Token: '%'
                }
            case '+':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(PLUS_ASSIGN, line); // Token: '+='
                } else if (ch == '+') {
                    nextCh();
                    return new TokenInfo(INC, line); // Token: '++'
                } else {
                    return new TokenInfo(PLUS, line); // Token: '+'
                }
            case '-':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(MINUS_ASSIGN, line); // Token: '-='
                } else if (ch == '-') {
                    nextCh();
                    return new TokenInfo(DEC, line); // Token: '--'
                } else {
                    return new TokenInfo(MINUS, line); // Token: '-='
                }
            case '&':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(AND_ASSIGN, line); // Token: '&='
                } else if (ch == '&') {
                    nextCh();
                    return new TokenInfo(LAND, line); // Token: '&&'
                } else {
                    return new TokenInfo(AND, line); // Token: '&'
                }
            case '^':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(XOR_ASSIGN, line); // Token: '^='
                } else {
                    return new TokenInfo(XOR, line); // Token: '^'
                }
            case '|':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(OR_ASSIGN, line); // Token: '|='
                } else if (ch == '|') {
                    nextCh();
                    return new TokenInfo(LOR, line); // Token: '||'
                } else {
                    return new TokenInfo(OR, line); // Token: '|'
                }
            case '>':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(GE, line); // Token: '>='
                } else if (ch == '>') {
                    nextCh();
                    if (ch == '>') {
                        nextCh();
                        if (ch == '=') {
                            nextCh();
                            return new TokenInfo(USHIFTR_ASSIGN, line); // Token: '>>>='
                        } else {
                            return new TokenInfo(USHIFTR, line); // Token: '>>>'
                        }
                    } else if (ch == '=') {
                        nextCh();
                        return new TokenInfo(SHIFTR_ASSIGN, line); // Token: '>>='
                    } else {
                        nextCh();
                        return new TokenInfo(SHIFTR, line); // Token: '>>'
                    }
                } else {
                    return new TokenInfo(GT, line); // Token: '>'
                }
            case '<':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(LE, line); // Token: '<='
                } else if (ch == '<') {
                    nextCh();
                    if (ch == '=') {
                        return new TokenInfo(SHIFTL_ASSIGN, line); // Token: '<<='
                    } else {
                        return new TokenInfo(SHIFTL, line); // Token: '<<'
                    }
                } else {
                    return new TokenInfo(LT, line); // Token: '<'
                }
            case '\'':
                buffer = new StringBuffer();
                buffer.append('\'');
                nextCh();
                if (ch == '\\') {
                    nextCh();
                    buffer.append(escape());
                } else {
                    buffer.append(ch);
                    nextCh();
                }
                if (ch == '\'') {
                    buffer.append('\'');
                    nextCh();
                    return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
                } else {
                    // Expected a ' ; report error and try to
                    // recover.
                    reportScannerError(ch
                            + " found by scanner where closing ' was expected.");
                    while (ch != '\'' && ch != ';' && ch != '\n') {
                        nextCh();
                    }
                    return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
                }
            case '"':
                buffer = new StringBuffer();
                buffer.append("\"");
                nextCh();
                while (ch != '"' && ch != '\n' && ch != EOFCH) {
                    if (ch == '\\') {
                        nextCh();
                        buffer.append(escape());
                    } else {
                        buffer.append(ch);
                        nextCh();
                    }
                }
                if (ch == '\n') {
                    reportScannerError("Unexpected end of line found in String");
                } else if (ch == EOFCH) {
                    reportScannerError("Unexpected end of file found in String");
                } else {
                    // Scan the closing "
                    nextCh();
                    buffer.append("\"");
                }
                return new TokenInfo(STRING_LITERAL, buffer.toString(), line); // Token: 'string'
            case '.':
                nextCh();
                return new TokenInfo(DOT, line); // Token: '.'
            case EOFCH:
                return new TokenInfo(EOF, line); // Token: 'End of File'
            case '0':
                /**
                 * There are a couple ways in Java to declare a number
                 * since there are multiple number systems in Java
                 * 
                 * binary: base-2 01
                 * octal: base-8 01234567
                 * decimal: base-10 0123456789
                 * hex: base-16 0123456789ABCDEF
                 * 
                 * Declarations are handled by Java in the following manner:
                 * decimal: [0-9]
                 * octal: '0' [0-7]
                 * hex: '0x' || '0X' [0-9] [a-f] [A-F]
                 * binary: '0b' || '0B' [0-1]
                 * 
                 * a decimal point also separates an integer from a double or float
                 * float: 0 . [0-9] [f || F]
                 * double: 0 . [0-9] [d || D]
                 */
                buffer = new StringBuffer();
                buffer.append('0');
                nextCh();

                // check for euler '0e' and decimal '0.' notations
                if(ch == '.'){
                    buffer.append(ch);
                    nextCh();
                    return checkDecimalPoint(buffer);
                } else if (ch == 'e' || ch == 'E'){
                    buffer.append('e');
                    nextCh();
                    return checkEuler(buffer);
                }

                // check for number systems
                if (ch == 'b' || ch == 'B') {
                    // Binary Declaration '0[b || B]'
                    buffer.append('b');
                    nextCh();
                    // only '1' || '0' should follow
                    if (isBinary(ch)) {
                        while (isBinary(ch)) {
                            buffer.append(ch);
                            nextCh();
                        }
                    } else {
                        reportScannerError("Binary Declaration error, expected [0-1] received: '%c'", ch);
                    }

                } else if (ch == 'x' || ch == 'X') {
                    // Hex Declaration '0[x || X]'
                    buffer.append('x');
                    nextCh();
                    // Must have at least one of: [0-9] [a-f] [A-F]
                    if (isHex(ch)) {
                        while (isHex(ch)) {
                            buffer.append(ch);
                            nextCh();
                        }
                    } else {
                        reportScannerError("Hex Declaration error, expected [0-9] || [a-f] || [A-F] received: '%c'",
                                ch);
                    }
                } else if (isOctal(ch)) {
                    // Octal Declaration '0[0-7]'
                    while (isOctal(ch)) {
                        buffer.append(ch);
                        nextCh();
                    }
                }

                // check for literal type declarations
                if (ch == 'f' || ch == 'F') {
                    nextCh();
                    return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line); // Token: 'FLOAT_LITERAL'
                } else if (ch == 'd' || ch == 'D') {
                    nextCh();
                    return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line); // Token: 'DOUBLE_LITERAL'
                } else if (ch == 'l' || ch == 'L') {
                    nextCh();
                    return new TokenInfo(LONG_LITERAL, buffer.toString(), line); // Token: 'LONG_LITERAL'
                } else {
                    return new TokenInfo(INT_LITERAL, buffer.toString(), line); // Token: 'INT_LITERAL'
                }

            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                buffer = new StringBuffer();
                while (isDigit(ch)) {
                    buffer.append(ch);
                    nextCh();
                }

                /**
                 * From having '[0-9]' the following are possible:
                 * '.' indicates decimal expression
                 * 'e' || 'E' indicating euler expression
                 * 'f || F || d || D || l || L' type declaration
                 */

                 // check for euler '[0-9]e' and decimal '[0-9].' notations
                if (ch == '.') {
                    buffer.append(ch);
                    nextCh();
                    return checkDecimalPoint(buffer);
                } else if (ch == 'e' || ch == 'E') {
                    buffer.append('e');
                    nextCh();
                    return checkEuler(buffer);
                }

                // check for literal type declarations
                else if (ch == 'f' || ch == 'F') {
                    nextCh();
                    return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line); // Token: 'FLOAT_LITERAL'
                } else if (ch == 'd' || ch == 'D') {
                    nextCh();
                    return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line); // Token: 'DOUBLE_LITERAL'
                } else if (ch == 'l' || ch == 'L') {
                    nextCh();
                    return new TokenInfo(LONG_LITERAL, buffer.toString(), line); // Token: 'LONG_LITERAL'
                } else {
                    return new TokenInfo(INT_LITERAL, buffer.toString(), line); // Token: 'INT_LITERAL'
                }

            default:
                if (isIdentifierStart(ch)) {
                    buffer = new StringBuffer();
                    while (isIdentifierPart(ch)) {
                        buffer.append(ch);
                        nextCh();
                    }
                    String identifier = buffer.toString();
                    if (reserved.containsKey(identifier)) {
                        return new TokenInfo(reserved.get(identifier), line);
                    } else {
                        return new TokenInfo(IDENTIFIER, identifier, line);
                    }
                } else {
                    reportScannerError("Unidentified input token: '%c'", ch);
                    nextCh();
                    return getNextToken();
                }
        }
    }

    /**
     * Scans and returns an escaped character.
     * 
     * @return escaped character.
     */

    private String escape() {
        switch (ch) {
            case 'b':
                nextCh();
                return "\\b";
            case 't':
                nextCh();
                return "\\t";
            case 'n':
                nextCh();
                return "\\n";
            case 'f':
                nextCh();
                return "\\f";
            case 'r':
                nextCh();
                return "\\r";
            case '"':
                nextCh();
                return "\"";
            case '\'':
                nextCh();
                return "\\'";
            case '\\':
                nextCh();
                return "\\\\";
            default:
                reportScannerError("Badly formed escape: \\%c", ch);
                nextCh();
                return "";
        }
    }

    /**
     * Advances ch to the next character from input, and updates the line
     * number.
     */

    private void nextCh() {
        line = input.line();
        try {
            ch = input.nextChar();
        } catch (Exception e) {
            reportScannerError("Unable to read characters from input");
        }
    }

    /**
     * Reports a lexcial error and records the fact that an error has occured.
     * This fact can be ascertained from the Scanner by sending it an
     * errorHasOccurred message.
     * 
     * @param message
     *                message identifying the error.
     * @param args
     *                related values.
     */

    private void reportScannerError(String message, Object... args) {
        isInError = true;
        System.err.printf("%s:%d: ", fileName, line);
        System.err.printf(message, args);
        System.err.println();
    }

    /**
     * Returns true if the specified character is a digit (0-9); false otherwise.
     * 
     * @param c
     *          character.
     * @return true or false.
     */

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Checks if the char is part of the accepted hex characters [0-9] || [a-f] ||
     * [A-F]
     * 
     * @param c char, usually scanned ch
     * @return true || false
     */
    private boolean isHex(char c) {
        return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    /**
     * Checks if the char is part of the accepted binary characters [0-1]
     * 
     * @param c char, usually scanned ch
     * @return true || false
     */
    private boolean isBinary(char c) {
        return (ch == '1' || ch == '0');
    }

    /**
     * Checks if the char is part of the accepted octal characters [0-7]
     * 
     * @param c char, usually scanned ch
     * @return true || false
     */
    private boolean isOctal(char c) {
        return (ch >= '0' && ch <= '7');
    }

    /**
     * Returns true if the specified character is a whitespace; false otherwise.
     * 
     * @param c
     *          character.
     * @return true or false.
     */

    private boolean isWhitespace(char c) {
        return (c == ' ' || c == '\t' || c == '\n' || c == '\f');
    }

    /**
     * Returns true if the specified character can start an identifier name;
     * false otherwise.
     * 
     * @param c
     *          character.
     * @return true or false.
     */

    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$');
    }

    /**
     * Returns true if the specified character can be part of an identifier name;
     * false otherwise.
     * 
     * @param c
     *          character.
     * @return true or false.
     */

    private boolean isIdentifierPart(char c) {
        return (isIdentifierStart(c) || isDigit(c));
    }

    /**
     * Has an error occurred up to now in lexical analysis?
     * 
     * @return {@code true} if an error occurred and {@code false} otherwise.
     */

    public boolean errorHasOccurred() {
        return isInError;
    }

    /**
     * Returns the name of the source file.
     * 
     * @return name of the source file.
     */

    public String fileName() {
        return fileName;
    }

    private TokenInfo checkDecimalPoint(StringBuffer buffer) {
        // '0.' can be [0-9]
        while (isDigit(ch)) {
            buffer.append(ch);
            nextCh();
        }

        /**
         * If we have '0.[0-9]'
         * 
         * the possibilities to what could follow are:
         * float declaration [f || F]
         * double declaration [d || D]
         * euler declaration [e || E]
         */
        if (ch == 'f' || ch == 'F') {
            nextCh();
            return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line); // Token: 'FLOAT_LITERAL'
        } else if (ch == 'e' || ch == 'E') {
            return checkEuler(buffer);
        }

        // The default is double 
        if (ch == 'd' || ch == 'D') {
            nextCh();
        }
        
        return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line); // Token: 'DOUBLE_LITERAL'
    }

    private TokenInfo checkEuler(StringBuffer buffer) {
        buffer.append('e');
        nextCh();

        /**
         * An exponent of base 10 is declared by:
         * 'e' || 'E' followed by
         * optional sign [+ || -] followed by
         * mandatory [0-9] followed by
         * optional [double declaration || float declaration]
         */
        if (ch == '+' || ch == '-') {
            buffer.append(ch);
            nextCh();
        }

        if (isDigit(ch)) {
            while (isDigit(ch)) {
                buffer.append(ch);
                nextCh();
            }
        } else {
            reportScannerError("Euler declaration error, expected [0-9] received: '%c'", ch);
        }

        if (ch == 'd' || ch == 'D') {
            nextCh();
            return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line); // Token: 'DOUBLE_LITERAL'
        } else if (ch == 'f' || ch == 'F') {
            nextCh();
            return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line); // Token: 'FLOAT_LITERAL'
        }

        // The default is Token: 'FLOAT_LITERAL' for euler declarations
        return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line); // Token: 'FLOAT_LITERAL'
    }
}

/**
 * A buffered character reader. Abstracts out differences between platforms,
 * mapping all new lines to '\n'. Also, keeps track of line numbers where the
 * first line is numbered 1.
 */

class CharReader {

    /** A representation of the end of file as a character. */
    public final static char EOFCH = (char) -1;

    /** The underlying reader records line numbers. */
    private LineNumberReader lineNumberReader;

    /** Name of the file that is being read. */
    private String fileName;

    /**
     * Constructs a CharReader from a file name.
     * 
     * @param fileName
     *                 the name of the input file.
     * @exception FileNotFoundException
     *                                  if the file is not found.
     */

    public CharReader(String fileName) throws FileNotFoundException {
        lineNumberReader = new LineNumberReader(new FileReader(fileName));
        this.fileName = fileName;
    }

    /**
     * Scans the next character.
     * 
     * @return the character scanned.
     * @exception IOException
     *                        if an I/O error occurs.
     */

    public char nextChar() throws IOException {
        return (char) lineNumberReader.read();
    }

    /**
     * Returns the current line number in the source file, starting at 1.
     * 
     * @return the current line number.
     */

    public int line() {
        // LineNumberReader counts lines from 0.
        return lineNumberReader.getLineNumber() + 1;
    }

    /**
     * Returns the file name.
     * 
     * @return the file name.
     */

    public String fileName() {
        return fileName;
    }

    /**
     * Closes the file.
     * 
     * @exception IOException
     *                        if an I/O error occurs.
     */

    public void close() throws IOException {
        lineNumberReader.close();
    }

}

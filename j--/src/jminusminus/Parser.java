// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.naming.spi.DirStateFactory.Result;

import static jminusminus.TokenKind.*;

/**
 * A recursive descent parser that, given a lexical analyzer (a
 * {@link LookaheadScanner}), parses a Java compilation unit (program file),
 * taking tokens from the LookaheadScanner, and produces an abstract syntax
 * tree (AST) for it.
 * <p>
 * See Appendix C.2.2 in the textbook or the
 * <a href="https://docs.oracle.com/javase/specs/jls/se8/html/index.html">Java
 * Language Specifications</a>
 * for the full syntactic grammar.
 */

public class Parser {

    /** The lexical analyzer with which tokens are scanned. */
    private LookaheadScanner scanner;

    /** Whether a parser error has been found. */
    private boolean isInError;

    /** Whether we have recovered from a parser error. */
    private boolean isRecovered;

    /**
     * Constructs a parser from the given lexical analyzer.
     * 
     * @param scanner
     *                the lexical analyzer with which tokens are scanned.
     */

    public Parser(LookaheadScanner scanner) {
        this.scanner = scanner;
        isInError = false;
        isRecovered = true;
        scanner.next(); // Prime the pump
    }

    /**
     * Has a parser error occurred up to now?
     * 
     * @return {@code true} if a parser error occurred; {@code false} otherwise.
     */

    public boolean errorHasOccurred() {
        return isInError;
    }

    // ////////////////////////////////////////////////
    // Parsing Support ///////////////////////////////
    // ////////////////////////////////////////////////

    /**
     * Is the current token this one?
     * 
     * @param sought
     *               the token we're looking for.
     * @return true iff they match; false otherwise.
     */

    private boolean see(TokenKind sought) {
        return (sought == scanner.token().kind());
    }

    /**
     * Look at the current (unscanned) token to see if it's one we're looking
     * for. If so, scan it and return true; otherwise return false (without
     * scanning a thing).
     * 
     * @param sought
     *               the token we're looking for.
     * @return true iff they match; false otherwise.
     */

    private boolean have(TokenKind sought) {
        if (see(sought)) {
            scanner.next();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempt to match a token we're looking for with the current input token.
     * If we succeed, scan the token and go into a "isRecovered" state. If we
     * fail, then what we do next depends on whether or not we're currently in a
     * "isRecovered" state: if so, we report the error and go into an
     * "Unrecovered" state; if not, we repeatedly scan tokens until we find the
     * one we're looking for (or EOF) and then return to a "isRecovered" state.
     * This gives us a kind of poor man's syntactic error recovery. The strategy
     * is due to David Turner and Ron Morrison.
     * 
     * @param sought
     *               the token we're looking for.
     */

    private void mustBe(TokenKind sought) {
        if (scanner.token().kind() == sought) {
            scanner.next();
            isRecovered = true;
        } else if (isRecovered) {
            isRecovered = false;
            reportParserError("%s found where %s sought", scanner.token()
                    .image(), sought.image());
        } else {
            // Do not report the (possibly spurious) error,
            // but rather attempt to recover by forcing a match.
            while (!see(sought) && !see(EOF)) {
                scanner.next();
            }
            if (see(sought)) {
                scanner.next();
                isRecovered = true;
            }
        }
    }

    /**
     * Pull out the ambiguous part of a name and return it.
     * 
     * @param name
     *             with an ambiguos part (possibly).
     * @return ambiguous part or null.
     */

    private AmbiguousName ambiguousPart(TypeName name) {
        String qualifiedName = name.toString();
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        return lastDotIndex == -1 ? null // It was a simple
                // name
                : new AmbiguousName(name.line(), qualifiedName.substring(0,
                        lastDotIndex));
    }

    /**
     * Report a syntax error.
     * 
     * @param message
     *                message identifying the error.
     * @param args
     *                related values.
     */

    private void reportParserError(String message, Object... args) {
        isInError = true;
        isRecovered = false;
        System.err
                .printf("%s:%d: ", scanner.fileName(), scanner.token().line());
        System.err.printf(message, args);
        System.err.println();
    }

    // ////////////////////////////////////////////////
    // Lookahead /////////////////////////////////////
    // ////////////////////////////////////////////////

    /**
     * Are we looking at an IDENTIFIER followed by a LPAREN? Look ahead to find
     * out.
     * 
     * @return true if we're looking at IDENTIFIER LPAREN; false otherwise.
     */

    private boolean seeIdentLParen() {
        scanner.recordPosition();
        boolean result = have(IDENTIFIER) && see(LPAREN);
        scanner.returnToPosition();
        return result;
    }

    /**
     * Are we looking at a cast? ie.
     * 
     * <pre>
     *   LPAREN type RPAREN ...
     * </pre>
     * 
     * Look ahead to find out.
     * 
     * @return true iff we're looking at a cast; false otherwise.
     */

    private boolean seeCast() {
        scanner.recordPosition();
        if (!have(LPAREN)) {
            scanner.returnToPosition();
            return false;
        }
        if (seeBasicType()) {
            scanner.returnToPosition();
            return true;
        }
        if (!see(IDENTIFIER)) {
            scanner.returnToPosition();
            return false;
        } else {
            scanner.next(); // Scan the IDENTIFIER
            // A qualified identifier is ok
            while (have(DOT)) {
                if (!have(IDENTIFIER)) {
                    scanner.returnToPosition();
                    return false;
                }
            }
        }
        while (have(LBRACK)) {
            if (!have(RBRACK)) {
                scanner.returnToPosition();
                return false;
            }
        }
        if (!have(RPAREN)) {
            scanner.returnToPosition();
            return false;
        }
        scanner.returnToPosition();
        return true;
    }

    /**
     * Are we looking at a local variable declaration? ie.
     * 
     * <pre>
     *   type IDENTIFIER {LBRACK RBRACK} ...
     * </pre>
     * 
     * Look ahead to determine.
     * 
     * @return true iff we are looking at local variable declaration; false
     *         otherwise.
     */

    private boolean seeLocalVariableDeclaration() {
        scanner.recordPosition();
        if (have(IDENTIFIER)) {
            // A qualified identifier is ok
            while (have(DOT)) {
                if (!have(IDENTIFIER)) {
                    scanner.returnToPosition();
                    return false;
                }
            }
        } else if (seeBasicType()) {
            scanner.next();
        } else {
            scanner.returnToPosition();
            return false;
        }
        while (have(LBRACK)) {
            if (!have(RBRACK)) {
                scanner.returnToPosition();
                return false;
            }
        }
        if (!have(IDENTIFIER)) {
            scanner.returnToPosition();
            return false;
        }
        while (have(LBRACK)) {
            if (!have(RBRACK)) {
                scanner.returnToPosition();
                return false;
            }
        }
        scanner.returnToPosition();
        return true;
    }

    /**
     * Are we looking at a basic type? ie.
     * 
     * <pre>
     * BOOLEAN | CHAR | INT | DOUBLE | FLOAT | LONG | BYTE | SHORT
     * </pre>
     * 
     * @return true if we're looking at a basic type; false otherwise.
     */

    private boolean seeBasicType() {
        return (see(BOOLEAN) || see(CHAR) || see(INT)) || see(DOUBLE) || see(LONG) || see(FLOAT) || see(BYTE) || see(SHORT);
    }

    /**
     * Are we looking at a reference type? ie.
     * 
     * <pre>
     *   referenceType ::= basicType LBRACK RBRACK {LBRACK RBRACK}
     *                   | qualifiedIdentifier {LBRACK RBRACK}
     * </pre>
     * 
     * @return true iff we're looking at a reference type; false otherwise.
     */

    private boolean seeReferenceType() {
        if (see(IDENTIFIER)) {
            return true;
        } else {
            scanner.recordPosition();
            if (have(BOOLEAN) || have(CHAR) || have(INT) || have(DOUBLE) || have(FLOAT) || have(LONG) || have(BYTE) || have(SHORT)) {
                if (have(LBRACK) && see(RBRACK)) {
                    scanner.returnToPosition();
                    return true;
                }
            }
            scanner.returnToPosition();
        }
        return false;
    }

    /**
     * Are we looking at []?
     * 
     * @return true iff we're looking at a [] pair; false otherwise.
     */

    private boolean seeDims() {
        scanner.recordPosition();
        boolean result = have(LBRACK) && see(RBRACK);
        scanner.returnToPosition();
        return result;
    }

    /**
     * Are we looking at a typeDeclarationModifier? ie.
     * 
     * <pre>
     * PUBLIC | PROTECTED | PRIVATE | STATIC | ABSTRACT | FINAL | SCRIPTFP
     * </pre>
     * 
     * This is mostly used to check if there is a class/interface declaration
     * coming.
     * 
     * @return
     */
    private boolean seeTypeDeclModifiers() {
        return (see(PUBLIC) || see(PROTECTED) || see(PRIVATE) || see(STATIC) || see(ABSTRACT) || see(FINAL)
                || see(STRICTFP));
    }

    /**
     * Are we looking at a modifier? ie.
     * 
     * <pre>
     * PUBLIC | PROTECTED | PRIVATE | STATIC |
     *         ABSTRACT | TRANSIENT | FINAL | NATIVE |
     *         THREADSAFE | SYNCHRONIZED | CONST | VOLATILE | STRICTFP
     * </pre>
     * 
     * @return
     */
    private boolean seeModifiers() {
        return (see(PUBLIC) || see(PROTECTED) || see(PRIVATE) || see(STATIC) ||
                see(ABSTRACT) || see(TRANSIENT) || see(FINAL) || see(NATIVE) ||
                see(THREADSAFE) || see(SYNCHRONIZED) || see(CONST) || see(VOLATILE) || see(STRICTFP));
    }

    /**
     * Are we looking inside the parentheses of a traditional for loop declaration?
     * ie.
     * 
     * <pre>
     *      for( [forInit] ; [expression] ; [forUpdate])
     * </pre>
     * 
     * @return
     */
    private boolean seeTraditional() {
        boolean result = false;
        scanner.recordPosition();
        if (have(SEMI)) {
            result = true;
        } else {
            while (!see(RPAREN)) {
                scanner.next();
                if (have(SEMI)) {
                    result = true;
                    break;
                }
            }
        }
        scanner.returnToPosition();
        return result;
    }

    /**
     * Are we looking at any of the relational tokens? ie.
     * 
     * <pre>
     *  LT, GT, LE, GE
     * </pre>
     * 
     * @return
     */
    private boolean seeRelational(){
        boolean result = false;
        scanner.recordPosition();
        if (have(LT)) {
            result = true;
        } else if (have(GT)) {
            result = true;
        } else if (have(LE)) {
            result = true;
        } else if (have(GE)) {
            result = true;
        }
        scanner.returnToPosition();
        return result;
    }

    // ////////////////////////////////////////////////
    // Parser Proper /////////////////////////////////
    // ////////////////////////////////////////////////

    /**
     * Parses a compilation unit (a program file) and constructs an AST for it.
     * After constructing the Parser, this is its entry point.
     * 
     * <pre>
     *   compilationUnit ::= [PACKAGE qualifiedIdentifier SEMI]
     *                       {IMPORT  qualifiedIdentifier SEMI}
     *                       {typeDeclaration}
     *                       EOF
     * </pre>
     * 
     * @return an AST for a compilationUnit.
     */

    public JCompilationUnit compilationUnit() {
        int line = scanner.token().line();
        TypeName packageName = null; // Default
        if (have(PACKAGE)) {
            packageName = qualifiedIdentifier();
            mustBe(SEMI);
        }
        ArrayList<TypeName> imports = new ArrayList<TypeName>();
        while (have(IMPORT)) {
            imports.add(qualifiedIdentifier());
            mustBe(SEMI);
        }
        ArrayList<JAST> typeDeclarations = new ArrayList<JAST>();
        while (!see(EOF)) {
            JAST typeDeclaration = typeDeclaration();
            if (typeDeclaration != null) {
                typeDeclarations.add(typeDeclaration);
            }
        }
        mustBe(EOF);
        return new JCompilationUnit(scanner.fileName(),
                line, packageName,
                imports, typeDeclarations);
    }

    /**
     * Parse a qualified identifier.
     * 
     * <pre>
     *   qualifiedIdentifier ::= IDENTIFIER {DOT IDENTIFIER}
     * </pre>
     * 
     * @return an instance of TypeName.
     */

    private TypeName qualifiedIdentifier() {
        int line = scanner.token().line();
        mustBe(IDENTIFIER);
        String qualifiedIdentifier = scanner.previousToken().image();
        while (have(DOT)) {
            mustBe(IDENTIFIER);
            qualifiedIdentifier += "." + scanner.previousToken().image();
        }
        return new TypeName(line, qualifiedIdentifier);
    }

    /**
     * Parse a type declaration.
     * 
     * <pre>
     *   typeDeclaration ::= typeDeclarationModifiers (classDeclaration | interfaceDeclaration)
     * </pre>
     * 
     * @return an AST for a typeDeclaration.
     */

    private JAST typeDeclaration() {
        ArrayList<String> mods = typeDeclarationModifiers();

        if (see(CLASS)) {
            return classDeclaration(mods);
        } else if (see(INTERFACE)) {
            return interfaceDeclaration(mods);

        } else {

            if (have(SEMI)) {
                reportParserError("Warning: Ignored lone SEMI");
                return null;
            } else {
                // This should not happen but in case it does
                reportParserError("Expected either CLASS or INTERFACE received: " + scanner.token().kind());
                have(scanner.token().kind());
                return null;
            }
        }
    }

    /**
     * Parse class or interface modifiers.
     * 
     * <pre>
     *   typeDeclarationModifiers ::= {PUBLIC | PROTECTED | PRIVATE | STATIC | 
     *                  ABSTRACT | FINAL | STRICTFP}
     * </pre>
     * 
     * Check for duplicates, and conflicts among access modifiers (public,
     * protected, and private). Otherwise, no checks.
     * 
     * @return a list of modifiers.
     */
    private ArrayList<String> typeDeclarationModifiers() {
        ArrayList<String> mods = new ArrayList<String>();
        boolean scannedPUBLIC = false;
        boolean scannedPROTECTED = false;
        boolean scannedPRIVATE = false;
        boolean scannedSTATIC = false;
        boolean scannedABSTRACT = false;
        boolean scannedFINAL = false;
        boolean scannedSTRICTFP = false;
        boolean more = true;
        while (more) {
            if (have(PUBLIC)) {
                mods.add("public");
                if (scannedPUBLIC) {
                    reportParserError("Repeated modifier: public");
                }
                if (scannedPROTECTED || scannedPRIVATE) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPUBLIC = true;
            } else if (have(PROTECTED)) {
                mods.add("protected");
                if (scannedPROTECTED) {
                    reportParserError("Repeated modifier: protected");
                }
                if (scannedPUBLIC || scannedPRIVATE) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPROTECTED = true;
            } else if (have(PRIVATE)) {
                mods.add("private");
                if (scannedPRIVATE) {
                    reportParserError("Repeated modifier: private");
                }
                if (scannedPUBLIC || scannedPROTECTED) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPRIVATE = true;
            } else if (have(STATIC)) {
                mods.add("static");
                if (scannedSTATIC) {
                    reportParserError("Repeated modifier: static");
                }
                scannedSTATIC = true;
            } else if (have(ABSTRACT)) {
                mods.add("abstract");
                if (scannedABSTRACT) {
                    reportParserError("Repeated modifier: abstract");
                }
                scannedABSTRACT = true;
            } else if (have(FINAL)) {
                mods.add("final");
                if (scannedFINAL) {
                    reportParserError("Repeated modifier: final");
                }
                scannedFINAL = true;
            } else if (have(STRICTFP)) {
                mods.add("strictfp");
                if (scannedSTRICTFP) {
                    reportParserError("Repeated modifier: strictfp");
                }
                scannedSTRICTFP = true;
            } else {
                more = false;
            }
        }

        return mods;
    }

    /**
     * Parse modifiers.
     * 
     * <pre>
     *   modifiers ::= {PUBLIC | PROTECTED | PRIVATE | STATIC | 
     *                  ABSTRACT | TRANSIENT | FINAL | NATIVE | THREADSAFE | 
     *                  SYNCHRONIZED | CONST | VOLATILE | STRICTFP }
     * </pre>
     * 
     * Check for duplicates, and conflicts among access modifiers (public,
     * protected, and private). Otherwise, no checks.
     * 
     * @return a list of modifiers.
     */

    private ArrayList<String> modifiers() {
        ArrayList<String> mods = new ArrayList<String>();
        boolean scannedPUBLIC = false;
        boolean scannedPROTECTED = false;
        boolean scannedPRIVATE = false;
        boolean scannedSTATIC = false;
        boolean scannedABSTRACT = false;
        boolean scannedTRANSIENT = false;
        boolean scannedFINAL = false;
        boolean scannedNATIVE = false;
        boolean scannedTHREADSAFE = false;
        boolean scannedSYNCHRONIZED = false;
        boolean scannedCONST = false;
        boolean scannedVOLATILE = false;
        boolean scannedSTRICTFP = false;
        boolean more = true;
        while (more)
            if (have(PUBLIC)) {
                mods.add("public");
                if (scannedPUBLIC) {
                    reportParserError("Repeated modifier: public");
                }
                if (scannedPROTECTED || scannedPRIVATE) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPUBLIC = true;
            } else if (have(PROTECTED)) {
                mods.add("protected");
                if (scannedPROTECTED) {
                    reportParserError("Repeated modifier: protected");
                }
                if (scannedPUBLIC || scannedPRIVATE) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPROTECTED = true;
            } else if (have(PRIVATE)) {
                mods.add("private");
                if (scannedPRIVATE) {
                    reportParserError("Repeated modifier: private");
                }
                if (scannedPUBLIC || scannedPROTECTED) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPRIVATE = true;
            } else if (have(STATIC)) {
                mods.add("static");
                if (scannedSTATIC) {
                    reportParserError("Repeated modifier: static");
                }
                scannedSTATIC = true;
            } else if (have(ABSTRACT)) {
                mods.add("abstract");
                if (scannedABSTRACT) {
                    reportParserError("Repeated modifier: abstract");
                }
                scannedABSTRACT = true;
            } else if (have(FINAL)) {
                mods.add("final");
                if (scannedFINAL) {
                    reportParserError("Repeated modifier: final");
                }
                scannedFINAL = true;
            } else if (have(TRANSIENT)) {
                mods.add("transient");
                if (scannedTRANSIENT) {
                    reportParserError("Repeated modifier: transient");
                }
                scannedTRANSIENT = true;
            } else if (have(NATIVE)) {
                mods.add("native");
                if (scannedNATIVE) {
                    reportParserError("Repeated modifier: native");
                }
                scannedNATIVE = true;
            } else if (have(THREADSAFE)) {
                mods.add("threadsafe");
                if (scannedTHREADSAFE) {
                    reportParserError("Repeated modifier: threadsafe");
                }
                scannedTHREADSAFE = true;
            } else if (have(SYNCHRONIZED)) {
                mods.add("synchronized");
                if (scannedSYNCHRONIZED) {
                    reportParserError("Repeated modifier: synchronized");
                }
                scannedSYNCHRONIZED = true;
            } else if (have(CONST)) {
                // Const is reserved but not valid so check in the analysis
                mods.add("const");
                if (scannedCONST) {
                    reportParserError("Repeated modifier: const");
                }
                scannedCONST = true;
            } else if (have(VOLATILE)) {
                mods.add("volatile");
                if (scannedVOLATILE) {
                    reportParserError("Repeated modifier: volatile");
                }
                scannedVOLATILE = true;
            } else if (have(STRICTFP)) {
                mods.add("strictfp");
                if (scannedSTRICTFP) {
                    reportParserError("Repeated modifier: strictfp");
                }
                scannedSTRICTFP = true;
            } else {
                more = false;
            }
        return mods;
    }

    /**
     * Parse a class declaration.
     * 
     * <pre>
     *   classDeclaration ::= CLASS IDENTIFIER [EXTENDS qualifiedIdentifier] 
     *                          [implements qualifiedIdentifier {, qualifiedIdentifier}]
     *                              classBody
     * </pre>
     * 
     * A class which doesn't explicitly extend another (super) class implicitly
     * extends the superclass java.lang.Object.
     * 
     * @param mods
     *             the class modifiers.
     * @return an AST for a classDeclaration.
     */

    private JClassDeclaration classDeclaration(ArrayList<String> mods) {
        int line = scanner.token().line();
        mustBe(CLASS);
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        Type superClass;
        ArrayList<TypeName> implementations = new ArrayList<>();

        if (have(EXTENDS)) {
            superClass = qualifiedIdentifier();
        } else {
            superClass = Type.OBJECT;
        }

        if (have(IMPLEMENTS)) {
            implementations.add(qualifiedIdentifier()); // (Exception, NullPointerException, etc.)

            // Needs to check for more identifiers
            while (have(COMMA)) {
                implementations.add(qualifiedIdentifier());
            }
        }

        return new JClassDeclaration(line, mods, name, superClass, implementations, classBody());
    }

    /**
     * Parse an interface declaration
     * 
     * <pre>
     *  interfaceDeclaration ::= INTERFACE IDENTIFIER // cannot be final
     *                              [EXTENDS qualifiedIdentifier { ,qualifiedIdentifier}]
     *                                  interfaceBody
     * </pre>
     * 
     * @param mods the interface modifiers
     * @return an AST for a interfaceDeclaration
     */
    private JInterfaceDeclaration interfaceDeclaration(ArrayList<String> mods) {
        int line = scanner.token().line();
        mustBe(INTERFACE);
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        ArrayList<TypeName> superClasses = new ArrayList<>(); // in an interface it can be more than one

        if (have(EXTENDS)) {
            superClasses.add(qualifiedIdentifier());
            while (have(COMMA)) {
                superClasses.add(qualifiedIdentifier());
            }
        } else {
            superClasses = null;
        }

        return new JInterfaceDeclaration(line, mods, name, superClasses, interfaceBody());
    }

    /**
     * Parse a class body.
     * 
     * <pre>
     *   classBody ::= LCURLY
     *                      SEMI
     *                      static block
     *                      block
     *                   modifiers memberDecl
     *                 RCURLY
     * </pre>
     * 
     * @return list of members in the class body.
     */

    private ArrayList<JMember> classBody() {
        ArrayList<JMember> members = new ArrayList<JMember>();
        mustBe(LCURLY);
        while (!see(RCURLY) && !see(EOF)) {
            if (have(SEMI)) {
                reportParserError("Warning: Ignored lone SEMI");

            } else if (have(STATIC) && see(LCURLY)) {
                JBlock block = block();
                members.add(new JStaticBlock(block.line, block.statements()));

            } else if (see(LCURLY)) {
                JBlock block = block();
                members.add(new JInitBlock(block.line, block.statements()));

            } else {
                members.add(memberDecl(modifiers()));
            }
        }
        mustBe(RCURLY);
        return members;
    }

    /**
     * Parse an interfaceBody
     * 
     * <pre>
     * interfaceBody ::= LCURLY
     * SEMI
     * modifiers memberDecl
     * RCURLY
     * 
     * @return
     */
    private ArrayList<JMember> interfaceBody() {
        ArrayList<JMember> members = new ArrayList<JMember>();

        mustBe(LCURLY);
        while (!see(RCURLY) && !see(EOF)) {
            if (have(SEMI)) {
                reportParserError("Warning: Ignored lone SEMI");

            } else {
                members.add(interfaceMemberDecl(modifiers()));
            }
        }
        mustBe(RCURLY);

        return members;
    }

    /**
     * Parse a member declaration.
     * 
     * <pre>
     *   memberDecl ::= classDeclaration        // inner class
     *                 | interfaceDeclaration   // inner interface
     *                 | IDENTIFIER            // constructor
     *                    formalParameters
     *                    [throws qualifiedIdentifier { , qualifiedIdentifier}] block
     *                 | (VOID | type) IDENTIFIER  // method
     *                    formalParameters 
     *                      [throws qualifiedIdentifier { , qualifiedIdentifier}] (block | ;)
     *                | type variableDeclarators SEMI
     * </pre>
     * 
     * @param mods
     *             the class member modifiers.
     * @return an AST for a memberDecl.
     */

    private JMember memberDecl(ArrayList<String> mods) {
        int line = scanner.token().line();
        JMember memberDecl = null;

        // Needs to check for inner classes and interfaces
        if (see(CLASS)) {
            ArrayList<String> subMods = new ArrayList<>();
            subMods.add(scanner.previousToken().toString());
            memberDecl = classDeclaration(subMods);

        } else if (see(INTERFACE)) {
            ArrayList<String> subMods = new ArrayList<>();
            subMods.add(scanner.previousToken().toString());
            memberDecl = interfaceDeclaration(subMods);

        } else if (seeIdentLParen()) {
            // A constructor
            mustBe(IDENTIFIER);
            String name = scanner.previousToken().image();
            ArrayList<JFormalParameter> params = formalParameters();
            ArrayList<TypeName> exceptions = new ArrayList<>();

            // optional 'throws'
            if (have(THROWS)) {
                exceptions.add(qualifiedIdentifier());

                // Needs to check for more identifiers
                while (have(COMMA)) {
                    exceptions.add(qualifiedIdentifier());
                }
            }
            JBlock body = block();
            memberDecl = new JConstructorDeclaration(line, mods, name, params, exceptions, body);
        } else {
            // A Method
            Type type = null;
            if (have(VOID)) {
                // void method
                type = Type.VOID;
                mustBe(IDENTIFIER);
                String name = scanner.previousToken().image();
                ArrayList<JFormalParameter> params = formalParameters();
                ArrayList<TypeName> exceptions = new ArrayList<>();

                // optional 'throws'
                if (have(THROWS)) {
                    exceptions.add(qualifiedIdentifier());

                    // Needs to check for more identifiers
                    while (have(COMMA)) {
                        exceptions.add(qualifiedIdentifier());
                    }
                }

                JBlock body = have(SEMI) ? null : block();
                memberDecl = new JMethodDeclaration(line, mods, name, type,
                        params, exceptions, body);
            } else {
                type = type();
                if (seeIdentLParen()) {
                    // Non void method
                    mustBe(IDENTIFIER);
                    String name = scanner.previousToken().image();
                    ArrayList<JFormalParameter> params = formalParameters();
                    ArrayList<TypeName> exceptions = new ArrayList<>(); // holds all qualifiedIdentifiers

                    // optional 'throws'
                    if (have(THROWS)) {
                        exceptions.add(qualifiedIdentifier()); // (Exception, NullPointerException, etc.)

                        // Needs to check for more identifiers
                        while (have(COMMA)) {
                            exceptions.add(qualifiedIdentifier());
                        }
                    }
                    JBlock body = have(SEMI) ? null : block();
                    memberDecl = new JMethodDeclaration(line, mods, name, type,
                            params, exceptions, body);
                } else {
                    // Field
                    memberDecl = new JFieldDeclaration(line, mods,
                            variableDeclarators(type));
                    mustBe(SEMI);
                }
            }
        }
        return memberDecl;
    }

    /**
     * Parse a interfaceMemberDecl
     * 
     * <pre>
     *  interfaceMemberDecl ::= classDeclaration // inner class
     *                      | interfaceDeclaration // inner interface
     *                      | (VOID | type) IDENTIFIER // method
     *                          formalParameters { [ ] }
     *                              [throws qualifiedIdentifier {, qualifiedIdentifier}] SEMI
     *                      | type variableDeclarators SEMI // must have inits
     * </pre>
     * 
     * @see memberDecl
     * @param mods interface member modifiers
     * @return an AST for a interfaceMemberDecl
     */
    private JMember interfaceMemberDecl(ArrayList<String> mods) {
        int line = scanner.token().line();
        JMember memberDecl = null;
        Type type = null;

        // Needs to check for inner classes and interfaces
        if (see(CLASS)) {
            ArrayList<String> subMods = new ArrayList<>();
            subMods.add(scanner.previousToken().toString());
            memberDecl = classDeclaration(subMods);

        } else if (see(INTERFACE)) {
            ArrayList<String> subMods = new ArrayList<>();
            subMods.add(scanner.previousToken().toString());
            memberDecl = interfaceDeclaration(subMods);
        }

        // Checking of methods: (VOID | type) IDENTIFIER
        else if (have(VOID)) {
            type = Type.VOID;
            mustBe(IDENTIFIER);
            String name = scanner.previousToken().image();
            ArrayList<JFormalParameter> params = formalParameters(); // formalParameters { [ ] }
            ArrayList<TypeName> exceptions = new ArrayList<>(); // holds all qualifiedIdentifiers

            // optional 'throws' [throws qualifiedIdentifier {, qualifiedIdentifier}] SEMI
            if (have(THROWS)) {
                exceptions.add(qualifiedIdentifier()); // (Exception, NullPointerException, etc.)

                // Needs to check for more identifiers
                while (have(COMMA)) {
                    exceptions.add(qualifiedIdentifier());
                }
            }

            mustBe(SEMI);

            // methods in interfaces don't have a body
            memberDecl = new JMethodDeclaration(line, mods, name, type, params, exceptions, null);

        } else {
            type = type();

            // Check if it is a method declaration or a variable declaration
            if (seeIdentLParen()) {
                mustBe(IDENTIFIER);
                String name = scanner.previousToken().image();
                ArrayList<JFormalParameter> params = formalParameters(); // formalParameters { [ ] }
                ArrayList<TypeName> exceptions = new ArrayList<>(); // holds all qualifiedIdentifiers

                // optional 'throws' [throws qualifiedIdentifier {, qualifiedIdentifier}] SEMI
                if (have(THROWS)) {
                    exceptions.add(qualifiedIdentifier()); // (Exception, NullPointerException, etc.)

                    // Needs to check for more identifiers
                    while (have(COMMA)) {
                        exceptions.add(qualifiedIdentifier());
                    }
                }

                // methods in interfaces don't have a body
                memberDecl = new JMethodDeclaration(line, mods, name, type, params, exceptions, null);

            } else {
                // type variableDeclarators SEMI
                memberDecl = new JFieldDeclaration(line, mods, variableDeclarators(type));
            }

            mustBe(SEMI);
        }

        return memberDecl;
    }

    /**
     * Parse a block.
     * 
     * <pre>
     *   block ::= LCURLY {blockStatement} RCURLY
     * </pre>
     * 
     * @return an AST for a block.
     */

    private JBlock block() {
        int line = scanner.token().line();
        ArrayList<JStatement> statements = new ArrayList<JStatement>();
        mustBe(LCURLY);
        while (!see(RCURLY) && !see(EOF)) {
            statements.add(blockStatement());
        }
        mustBe(RCURLY);
        return new JBlock(line, statements);
    }

    /**
     * Parse a block statement.
     * 
     * <pre>
     *   blockStatement ::= localVariableDeclarationStatement
     *                    | statement
     * </pre>
     * 
     * @return an AST for a blockStatement.
     */

    private JStatement blockStatement() {
        if (seeLocalVariableDeclaration()) {
            return localVariableDeclarationStatement();
        } else {
            return statement();
        }
    }

    /**
     * Parse a statement.
     * 
     * <pre>
     *   statement ::= block
     *               | IF parExpression statement [ELSE statement]
     *               | FOR LPAREN [forInit] SEMI [expression] SEMI [forUpdate] RPAREN statement
     *               | WHILE parExpression statement 
     *               | TRY block
                            {CATCH (formalParameter) block}
                            [finally block]  // Mandatory if there is no CATCH 
     *               | RETURN [expression] SEMI
     *               | THROW expression SEMI
     *               | SEMI 
     *               | statementExpression SEMI
     * </pre>
     * 
     * @return an AST for a statement.
     */
    private JStatement statement() {
        int line = scanner.token().line();
        if (see(LCURLY)) {
            return block();

        } else if (have(IF)) {
            JExpression test = parExpression();
            JStatement consequent = statement();
            JStatement alternate = have(ELSE) ? statement() : null;
            return new JIfStatement(line, test, consequent, alternate);

        } else if (have(FOR)) {
            boolean isEnhanced = false;
            JForInit forInit;
            ArrayList<JStatement> forUpdate;
            JExpression expression;

            mustBe(LPAREN);
            if (seeTraditional()) {
                // Traditional for([forInit]; [expression]; [forUpdate])
                forInit = see(SEMI) ? null : forInit();
                mustBe(SEMI);
                expression = see(SEMI) ? null : expression();
                mustBe(SEMI);
                forUpdate = see(RPAREN) ? null : forUpdate();

            } else {
                // enhanced for(forInit : forUpdate)
                isEnhanced = true;
                expression = null;
                forInit = forInit();
                mustBe(COLON);
                forUpdate = forUpdate();
            }

            mustBe(RPAREN);
            JStatement statement = statement();

            if (isEnhanced) {
                return new JForEnhancedStatement(line, forInit, forUpdate, statement);
            } else {
                return new JForStatement(line, forInit, expression, forUpdate, statement);
            }

        } else if (have(WHILE)) {
            JExpression test = parExpression();
            JStatement statement = statement();
            return new JWhileStatement(line, test, statement);

        } else if (have(TRY)) {
            JBlock body_try = block();
            JBlock body_catch = null;
            JBlock body_finally = null;
            JFormalParameter param = null;

            if (have(CATCH)) {
                mustBe(LPAREN); // Make sure to consume the parentheses
                param = formalParameter();
                mustBe(RPAREN);
                body_catch = block();

                if (have(FINALLY)) {
                    body_finally = block();
                }

            } else {
                mustBe(FINALLY);
                body_finally = block();
            }

            return new JTryCatchStatement(line, body_try, body_catch, body_finally, param);

        } else if (have(RETURN)) {
            if (have(SEMI)) {
                return new JReturnStatement(line, null);
            } else {
                JExpression expr = expression();
                mustBe(SEMI);
                return new JReturnStatement(line, expr);
            }

        } else if (have(THROW)) {
            JExpression expression = expression();
            mustBe(SEMI);
            return new JThrowStatement(line, expression);

        } else if (have(SEMI)) {
            return new JEmptyStatement(line);

        } else { // Must be a statementExpression
            JStatement statement = statementExpression();
            mustBe(SEMI);
            return statement;
        }
    }

    /**
     * Parse formal parameters.
     * 
     * <pre>
     *   formalParameters ::= LPAREN 
     *                          [formalParameter 
     *                            {COMMA  formalParameter}]
     *                        RPAREN
     * </pre>
     * 
     * @return a list of formal parameters.
     */

    private ArrayList<JFormalParameter> formalParameters() {
        ArrayList<JFormalParameter> parameters = new ArrayList<JFormalParameter>();
        mustBe(LPAREN);
        if (have(RPAREN))
            return parameters; // ()
        do {
            parameters.add(formalParameter());
        } while (have(COMMA));
        mustBe(RPAREN);
        return parameters;
    }

    /**
     * Parse a formal parameter.
     * 
     * <pre>
     *   formalParameter ::= type IDENTIFIER
     * </pre>
     * 
     * @return an AST for a formalParameter.
     */

    private JFormalParameter formalParameter() {
        int line = scanner.token().line();

        if(have(FINAL)){
            // optional TODO: implement optional final
            reportParserError("Found final in formalParameter but ignore it!");
        }

        Type type = type();
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        return new JFormalParameter(line, name, type);
    }

    /**
     * Parse a parenthesized expression.
     * 
     * <pre>
     *   parExpression ::= LPAREN expression RPAREN
     * </pre>
     * 
     * @return an AST for a parExpression.
     */

    private JExpression parExpression() {
        mustBe(LPAREN);
        JExpression expr = expression();
        mustBe(RPAREN);
        return expr;
    }

    /**
     * Parse a for loop initialization section inside the declaration.
     * 
     * 
     * <pre>
     *      forInit ::= statementExpression {COMMA statementExpression}
     *                  | [FINAL] type variableDeclarators
     * </pre>
     * 
     * @return
     */
    private JForInit forInit() {
        if (!seeLocalVariableDeclaration()) {
            ArrayList<JStatement> statements = new ArrayList<JStatement>();
            statements.add(statementExpression());

            while (have(COMMA)) {
                statements.add(statementExpression());
            }

            return new JForInit(null, statements);
            
        } else {
            ArrayList<JVariableDeclarator> variableDeclarators = new ArrayList<JVariableDeclarator>();

            if (have(FINAL)) {
                // optional TODO: implement optional final
                reportParserError("Found final in formalParameter but ignore it!");
            }

            Type type = type();
            variableDeclarators = variableDeclarators(type);
            return new JForInit(variableDeclarators, null);
        }
    }

    /**
     * Parse a for loop update section inside the declaration.
     * 
     * <pre>
     *     forUpdate ::= statementExpression {COMMA statementExpression}
     * </pre>
     * @return
     */
    private ArrayList<JStatement> forUpdate(){
        ArrayList<JStatement> statements = new ArrayList<JStatement>();

        statements.add(statementExpression());

        while (have(COMMA)) {
            statements.add(statementExpression());
        }

        return statements;
    }

    /**
     * Parse a local variable declaration statement.
     * 
     * <pre>
     *   localVariableDeclarationStatement ::= type 
     *                                           variableDeclarators 
     *                                             SEMI
     * </pre>
     * 
     * @return an AST for a variableDeclaration.
     */
    private JVariableDeclaration localVariableDeclarationStatement() {
        int line = scanner.token().line();
        ArrayList<String> mods = new ArrayList<String>();
        ArrayList<JVariableDeclarator> vdecls = variableDeclarators(type());
        mustBe(SEMI);
        return new JVariableDeclaration(line, mods, vdecls);
    }

    /**
     * Parse variable declarators.
     * 
     * <pre>
     *   variableDeclarators ::= variableDeclarator 
     *                             {COMMA variableDeclarator}
     * </pre>
     * 
     * @param type
     *             type of the variables.
     * @return a list of variable declarators.
     */
    private ArrayList<JVariableDeclarator> variableDeclarators(Type type) {
        ArrayList<JVariableDeclarator> variableDeclarators = new ArrayList<JVariableDeclarator>();
        do {
            variableDeclarators.add(variableDeclarator(type));
        } while (have(COMMA));
        return variableDeclarators;
    }

    /**
     * Parse a variable declarator.
     * 
     * <pre>
     *   variableDeclarator ::= IDENTIFIER
     *                          [ASSIGN variableInitializer]
     * </pre>
     * 
     * @param type
     *             type of the variable.
     * @return an AST for a variableDeclarator.
     */
    private JVariableDeclarator variableDeclarator(Type type) {
        int line = scanner.token().line();
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        JExpression initial = have(ASSIGN) ? variableInitializer(type) : null;
        return new JVariableDeclarator(line, name, type, initial);
    }

    /**
     * Parse a variable initializer.
     * 
     * <pre>
     *   variableInitializer ::= arrayInitializer
     *                         | expression
     * </pre>
     * 
     * @param type
     *             type of the variable.
     * @return an AST for a variableInitializer.
     */
    private JExpression variableInitializer(Type type) {
        if (see(LCURLY)) {
            return arrayInitializer(type);
        }
        return expression();
    }

    /**
     * Parse an array initializer.
     * 
     * <pre>
     *   arrayInitializer ::= LCURLY 
     *                          [variableInitializer 
     *                            {COMMA variableInitializer} [COMMA]]
     *                        RCURLY
     * </pre>
     * 
     * @param type
     *             type of the array.
     * @return an AST for an arrayInitializer.
     */
    private JArrayInitializer arrayInitializer(Type type) {
        int line = scanner.token().line();
        ArrayList<JExpression> initials = new ArrayList<JExpression>();
        mustBe(LCURLY);
        if (have(RCURLY)) {
            return new JArrayInitializer(line, type, initials);
        }
        initials.add(variableInitializer(type.componentType()));
        while (have(COMMA)) {
            initials.add(see(RCURLY) ? null
                    : variableInitializer(type
                            .componentType()));
        }
        mustBe(RCURLY);
        return new JArrayInitializer(line, type, initials);
    }

    /**
     * Parse arguments.
     * 
     * <pre>
     *   arguments ::= LPAREN [expression {COMMA expression}] RPAREN
     * </pre>
     * 
     * @return a list of expressions.
     */

    private ArrayList<JExpression> arguments() {
        ArrayList<JExpression> args = new ArrayList<JExpression>();
        mustBe(LPAREN);
        if (have(RPAREN)) {
            return args;
        }
        do {
            args.add(expression());
        } while (have(COMMA));
        mustBe(RPAREN);
        return args;
    }

    /**
     * Parse a type.
     * 
     * <pre>
     *   type ::= referenceType 
     *          | basicType
     * </pre>
     * 
     * @return an instance of Type.
     */

    private Type type() {
        if (seeReferenceType()) {
            return referenceType();
        }
        return basicType();
    }

    /**
     * Parse a basic type.
     * 
     * <pre>
     *   basicType ::= BOOLEAN | CHAR | INT | FLOAT | LONG
     * </pre>
     * 
     * @return an instance of Type.
     */

    private Type basicType() {
        if (have(BOOLEAN)) {
            return Type.BOOLEAN;
        } else if (have(CHAR)) {
            return Type.CHAR;
        } else if (have(INT)) {
            return Type.INT;
        } else if (have(DOUBLE)) {
            return Type.DOUBLE;
        } else if (have(LONG)) {
            return Type.LONG;
        } else if (have(BYTE)) {
            return Type.INT;
        } else if (have(SHORT)) {
            return Type.INT;
        } else if (have(FLOAT)) {
            return Type.FLOAT;
        } else {
            reportParserError("Type sought where %s found", scanner.token()
                    .image());
            return Type.ANY;
        }
    }

    /**
     * Parse a reference type.
     * 
     * <pre>
     *   referenceType ::= basicType LBRACK RBRACK {LBRACK RBRACK}
     *                   | qualifiedIdentifier {LBRACK RBRACK}
     * </pre>
     * 
     * @return an instance of Type.
     */

    private Type referenceType() {
        Type type = null;
        if (!see(IDENTIFIER)) {
            type = basicType();
            mustBe(LBRACK);
            mustBe(RBRACK);
            type = new ArrayTypeName(type);
        } else {
            type = qualifiedIdentifier();
        }
        while (seeDims()) {
            mustBe(LBRACK);
            mustBe(RBRACK);
            type = new ArrayTypeName(type);
        }
        return type;
    }

    /**
     * Parse a statement expression.
     * 
     * <pre>
     *   statementExpression ::= expression // but must have 
     *                                      // side-effect, eg i++
     * </pre>
     * 
     * @return an AST for a statementExpression.
     */

    private JStatement statementExpression() {
        int line = scanner.token().line();
        JExpression expr = expression();
        if (expr instanceof JAssignment 
                || expr instanceof JPreIncrementOp
                || expr instanceof JPreDecrementOp
                || expr instanceof JPostDecrementOp
                || expr instanceof JPostIncrementOp
                || expr instanceof JMessageExpression
                || expr instanceof JSuperConstruction
                || expr instanceof JThisConstruction
                || expr instanceof JNewOp
                || expr instanceof JVariable
                || expr instanceof JNewArrayOp) {
            // So as not to save on stack
            expr.isStatementExpression = true;
        } else {
            reportParserError("Invalid statement expression " + expr.type() + "; "
                    + "it does not have a side-effect");
        }
        return new JStatementExpression(line, expr);
    }

    /**
     * An expression.
     * 
     * <pre>
     *   expression ::= assignmentExpression
     * </pre>
     * 
     * @return an AST for an expression.
     */

    private JExpression expression() {
        return assignmentExpression();
    }

    /**
     * Parse an assignment expression.
     * 
     * <pre>
     * assignmentExpression ::=
     * conditionalExpressions // level 13
     * [(ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | STAR_ASSIGN
     * | DIV_ASSIGN | MOD_ASSIGN
     * | SHIFTR_ASSIGN | USHIFTR_ASSIGN | SHIFTL_ASSIGN
     * | BIT_AND_ASSIGN | BIT_OR_ASSIGN | XOR_ASSIGN) assignmentExpression
     * 
     * @return an AST for an assignmentExpression.
     */

    private JExpression assignmentExpression() {
        int line = scanner.token().line();
        JExpression lhs = conditionalExpression();
        if (have(ASSIGN)) {
            return new JAssignOp(line, lhs, assignmentExpression());
        } else if (have(PLUS_ASSIGN)) {
            return new JPlusAssignOp(line, lhs, assignmentExpression());
        } else if (have(MINUS_ASSIGN)) {
            return new JMinusAssignOp(line, lhs, assignmentExpression());
        } else if (have(STAR_ASSIGN)) {
            return new JStarAssignOp(line, lhs, assignmentExpression());
        } else if (have(DIV_ASSIGN)) {
            return new JDivAssignOp(line, lhs, assignmentExpression());
        } else if (have(MOD_ASSIGN)) {
            return new JModAssignOp(line, lhs, assignmentExpression());
        } else if (have(SHIFTR_ASSIGN)) {
            return new JShiftRAssign(line, lhs, assignmentExpression());
        } else if (have(USHIFTR_ASSIGN)) {
            return new JUShiftRAssign(line, lhs, assignmentExpression());
        } else if (have(SHIFTL_ASSIGN)) {
            return new JShiftLAssign(line, lhs, assignmentExpression());
        } else if (have(AND_ASSIGN)) {
            return new JANDAssign(line, lhs, assignmentExpression());
        } else if (have(OR_ASSIGN)) {
            return new JORAssign(line, lhs, assignmentExpression());
        } else if (have(XOR_ASSIGN)) {
            return new JXORAssign(line, lhs, assignmentExpression());
        } else {
            return lhs;
        }
    }

    /**
     * Parse a conditional expression.
     *
     * <pre>
     * conditionalExpression ::= conditionalOrExpression
     *                          [TERNARY assignmentExpression COLON conditionalExpression] // level 12
     * </pre>
     *
     * @return an AST for a conditionalExpression.
     */
    private JExpression conditionalExpression() {
        int line = scanner.token().line();
        JExpression lhs = conditionalOrExpression();
        if (have(TERNARY)) {
            JExpression thenBranch = assignmentExpression();
            mustBe(COLON);
            JExpression elseBranch = conditionalExpression();
            return new JConditionalExpression(line, lhs, thenBranch, elseBranch);
        }
        return lhs;
    }

    /**
     * Parse a conditional-or expression.
     *
     * <pre>
     *   conditionalOrExpression ::= conditionalAndExpression // level 11
     *                                  {LOR conditionalAndExpression}
     * </pre>
     *
     * @return an AST for a conditionalExpression.
     */

    private JExpression conditionalOrExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = conditionalAndExpression();
        while (more) {
            if (have(LOR)) {
                lhs = new JLogicalOrOp(line, lhs, conditionalAndExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parse a conditional-and expression.
     * 
     * <pre>
     *   conditionalAndExpression ::= equalityExpression // level 10
     *                                  {LAND equalityExpression}
     * </pre>
     * 
     * @return an AST for a conditionalExpression.
     */

    private JExpression conditionalAndExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = inclusiveOrExpression();
        while (more) {
            if (have(LAND)) {
                lhs = new JLogicalAndOp(line, lhs, inclusiveOrExpression());
            } else if (have(LOR)) {
                lhs = new JLogicalOrOp(line, lhs, inclusiveOrExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parse an inclusiveOrExpression // level 9
     * 
     * inclusiveOrExpression ::= exclusiveOrExpression { OR exclusiveOrExpression }
     * 
     * @return returns either a inclusiveOrExpression or passes it on to look for an
     *         exclusiveOrExpression
     */
    private JExpression inclusiveOrExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = exclusiveOrExpression();
        while (more) {
            if (have(OR)) {
                lhs = new JInclusiveOrOp(line, lhs, exclusiveOrExpression()); // OR (|)
            } else {
                more = false;
            }
        }

        return lhs;
    }

    /**
     * Parse an inclusiveOrExpression // level 8
     * 
     * exclusiveOrExpression ::= andExpression { XOR andExpression } // level 8
     * 
     * @return returns either a exclusiveOrExpression or passes it on to look for an
     *         andExpression
     */
    private JExpression exclusiveOrExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = andExpression();
        while (more) {
            if (have(XOR)) {
                lhs = new JExclusiveOrOp(line, lhs, andExpression()); // XOR (^)
            } else {
                more = false;
            }
        }

        return lhs;
    }

    /**
     * Parse an inclusiveOrExpression // level 7
     * 
     * andExpression ::= equalityExpression { AND equalityExpression } // level 7
     * 
     * @return returns either a andExpression or passes it on to look for an
     *         equalityExpression
     */
    private JExpression andExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = equalityExpression();
        while (more) {
            if (have(AND)) {
                lhs = new JAndOp(line, lhs, equalityExpression()); // AND (&)
            } else {
                more = false;
            }
        }

        return lhs;
    }

    /**
     * Parse an equality expression.
     * 
     * <pre>
     *   equalityExpression ::= relationalExpression  // level 6
     *                            { ( EQ | NEQ) relationalExpression}
     * </pre>
     * 
     * @return an AST for an equalityExpression.
     */

    private JExpression equalityExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = relationalExpression();
        while (more) {
            if (have(EQ)) {
                lhs = new JEqualOp(line, lhs, relationalExpression());
            } else if (have(NEQ)) {
                lhs = new JNotEqualOp(line, lhs, relationalExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parse a relational expression.
     * 
     * <pre>
     *   relationalExpression ::= shiftExpression       // level 5
     *                      ({ (LT | GT | LE | GE) shiftExpression } | INSTANCEOF referenceType)
     * </pre>
     * 
     * @return an AST for a relationalExpression.
     */
    private JExpression relationalExpression() {
        int line = scanner.token().line();
        JExpression lhs = shiftExpression();

        if(seeRelational()){
            do{
                if (have(LT)) {
                    return new JLessThanOp(line, lhs, shiftExpression());
                } else if (have(GT)) {
                    return new JGreaterThanOp(line, lhs, shiftExpression());
                } else if (have(LE)) {
                    return new JLessEqualOp(line, lhs, shiftExpression());
                } else if (have(GE)) {
                    return new JGreaterEqualOp(line, lhs, shiftExpression());
                }
            }
            while((see(LAND) || see(LOR)));

            return lhs;

        } else if (have(INSTANCEOF)) {
            return new JInstanceOfOp(line, lhs, referenceType());
        } else {
            return lhs;
        }
    }

    /**
     * Parse a shift expression (<< | >> | >>>) // level 4
     * 
     * shiftExpression ::= additiveExpression { ( SHIFTL | SHIFTR | USHIFTR )
     * additiveExpression }
     * 
     * 
     * @return returns either a shiftExpression or passes it on to look for an
     *         additiveExpression
     */
    private JExpression shiftExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = additiveExpression();

        while (more) {
            if (have(SHIFTL)) {
                lhs = new JShiftLeftOp(line, lhs, additiveExpression()); // SHIFTL (num << num)

            } else if (have(SHIFTR)) {
                lhs = new JShiftRightOp(line, lhs, additiveExpression()); // SHIFTR (num >> num)

            } else if (have(USHIFTR)) {
                lhs = new JShiftUOp(line, lhs, additiveExpression()); // USHIFTR (num >>> num)

            } else {
                more = false;
            }
        }

        return lhs;
    }

    /**
     * Parse an additive expression.
     * 
     * <pre>
     *   additiveExpression ::= multiplicativeExpression // level 3
     *                            {MINUS multiplicativeExpression}
     * </pre>
     * 
     * @return an AST for an additiveExpression.
     */

    private JExpression additiveExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = multiplicativeExpression();
        while (more) {
            if (have(MINUS)) {
                lhs = new JSubtractOp(line, lhs, multiplicativeExpression());
            } else if (have(PLUS)) {
                lhs = new JPlusOp(line, lhs, multiplicativeExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parse a multiplicative expression.
     * 
     * <pre>
     *   multiplicativeExpression ::= unaryExpression  // level 2
     *                                  {STAR unaryExpression}
     * </pre>
     * 
     * @return an AST for a multiplicativeExpression.
     */

    private JExpression multiplicativeExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = unaryExpression();
        while (more) {
            if (have(STAR)) {
                lhs = new JMultiplyOp(line, lhs, unaryExpression());
            } else if (have(DIV)) {
                lhs = new JDivideOp(line, lhs, unaryExpression());
            } else if (have(MOD)) {
                lhs = new JModOp(line, lhs, unaryExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parse an unary expression.
     * 
     * <pre>
     *   unaryExpression ::= INC unaryExpression // level 1
     *            | DEC unaryExpression
     *            | (PLUS | MINUS | TILDE) unaryExpression
     *            | simpleUnaryExpression
     * </pre>
     * 
     * @return an AST for an unaryExpression.
     */

    private JExpression unaryExpression() {
        int line = scanner.token().line();

        if (have(INC)) {
            return new JPreIncrementOp(line, unaryExpression()); // INC unaryExpression (++num)

        } else if (have(DEC)) {
            return new JPreDecrementOp(line, unaryExpression()); // DEC unaryExpression (--num)

        } else if (have(PLUS)) {
            return new JUnaryPlusOp(line, unaryExpression()); // PLUS unaryExpression (+num)

        } else if (have(MINUS)) {
            return new JNegateOp(line, unaryExpression()); // MINUS unaryExpression (-num)

        } else if (have(TILDE)) {
            return new JTildeOp(line, unaryExpression()); // TILDE unaryExpression (~num)

        } else {
            return simpleUnaryExpression();
        }
    }

    /**
     * Parse a simple unary expression.
     * 
     * <pre>
     *   simpleUnaryExpression ::= LNOT unaryExpression
     *                           | LPAREN basicType RPAREN 
     *                               unaryExpression
     *                           | LPAREN         
     *                               referenceType
     *                             RPAREN simpleUnaryExpression
     *                           | postfixExpression
     * </pre>
     * 
     * @return an AST for a simpleUnaryExpression.
     */

    private JExpression simpleUnaryExpression() {
        int line = scanner.token().line();
        if (have(LNOT)) {
            return new JLogicalNotOp(line, unaryExpression());
        } else if (seeCast()) {
            mustBe(LPAREN);
            boolean isBasicType = seeBasicType();
            Type type = type();
            mustBe(RPAREN);
            JExpression expr = isBasicType ? unaryExpression()
                    : simpleUnaryExpression();
            return new JCastOp(line, type, expr);
        } else {
            return postfixExpression();
        }
    }

    /**
     * Parse a postfix expression.
     * 
     * <pre>
     *   postfixExpression ::= primary {selector} {DEC | INC}
     * </pre>
     * 
     * @return an AST for a postfixExpression.
     */

    private JExpression postfixExpression() {
        int line = scanner.token().line();
        JExpression primaryExpr = primary();
        while (see(DOT) || see(LBRACK)) {
            primaryExpr = selector(primaryExpr);
        }
        while (see(DEC) || see(INC)) {
            if (have(DEC)) {
                primaryExpr = new JPostDecrementOp(line, primaryExpr);
            } else if (have(INC)) {
                primaryExpr = new JPostIncrementOp(line, primaryExpr);
            }
        }
        return primaryExpr;
    }

    /**
     * Parse a selector.
     * 
     * <pre>
     *   selector ::= DOT qualifiedIdentifier [arguments]
     *              | LBRACK expression RBRACK
     * </pre>
     * 
     * @param target
     *               the target expression for this selector.
     * @return an AST for a selector.
     */

    private JExpression selector(JExpression target) {
        int line = scanner.token().line();
        if (have(DOT)) {
            // Target . selector
            mustBe(IDENTIFIER);
            String name = scanner.previousToken().image();
            if (see(LPAREN)) {
                ArrayList<JExpression> args = arguments();
                return new JMessageExpression(line, target, name, args);
            } else {
                return new JFieldSelection(line, target, name);
            }
        } else {
            mustBe(LBRACK);
            JExpression index = expression();
            mustBe(RBRACK);
            return new JArrayExpression(line, target, index);
        }
    }

    /**
     * Parse a primary expression.
     * 
     * <pre>
     *   primary ::= parExpression
     *             | THIS [arguments]
     *             | SUPER ( arguments 
     *                     | DOT IDENTIFIER [arguments] 
     *                     )
     *             | literal
     *             | NEW creator
     *             | qualifiedIdentifier [arguments]
     * </pre>
     * 
     * @return an AST for a primary.
     */

    private JExpression primary() {
        int line = scanner.token().line();
        if (see(LPAREN)) {
            return parExpression();
        } else if (have(THIS)) {
            if (see(LPAREN)) {
                return new JThisConstruction(line, arguments());
            } else {
                return new JThis(line);
            }
        } else if (have(SUPER)) {
            if (!have(DOT)) {
                return new JSuperConstruction(line, arguments());
            } else {
                mustBe(IDENTIFIER);
                String name = scanner.previousToken().image();
                JExpression newTarget = new JSuper(line);
                if (see(LPAREN)) {
                    return new JMessageExpression(line, newTarget, null, name,
                            arguments());
                } else {
                    return new JFieldSelection(line, newTarget, name);
                }
            }
        } else if (have(NEW)) {
            return creator();
        } else if (see(IDENTIFIER)) {
            TypeName id = qualifiedIdentifier();
            if (see(LPAREN)) {
                return new JMessageExpression(line, null, ambiguousPart(id), id
                        .simpleName(), arguments());
            } else if (ambiguousPart(id) == null) {
                // A simple name
                return new JVariable(line, id.simpleName());
            } else {
                // ambiguousPart.fieldName
                return new JFieldSelection(line, ambiguousPart(id), null, id
                        .simpleName());
            }
        } else {
            return literal();
        }
    }

    /**
     * Parse a creator.
     * 
     * <pre>
     *   creator ::= (basicType | qualifiedIdentifier) 
     *                 ( arguments
     *                 | LBRACK RBRACK {LBRACK RBRACK} 
     *                     [arrayInitializer]
     *                 | newArrayDeclarator
     *                 )
     * </pre>
     * 
     * @return an AST for a creator.
     */

    private JExpression creator() {
        int line = scanner.token().line();
        Type type = seeBasicType() ? basicType() : qualifiedIdentifier();
        if (see(LPAREN)) {
            ArrayList<JExpression> args = arguments();
            return new JNewOp(line, type, args);
        } else if (see(LBRACK)) {
            if (seeDims()) {
                Type expected = type;
                while (have(LBRACK)) {
                    mustBe(RBRACK);
                    expected = new ArrayTypeName(expected);
                }
                return arrayInitializer(expected);
            } else
                return newArrayDeclarator(line, type);
        } else {
            reportParserError("( or [ sought where %s found", scanner.token()
                    .image());
            return new JWildExpression(line);
        }
    }

    /**
     * Parse a new array declarator.
     * 
     * <pre>
     *   newArrayDeclarator ::= LBRACK expression RBRACK 
     *                            {LBRACK expression RBRACK}
     *                            {LBRACK RBRACK}
     * </pre>
     * 
     * @param line
     *             line in which the declarator occurred.
     * @param type
     *             type of the array.
     * @return an AST for a newArrayDeclarator.
     */

    private JNewArrayOp newArrayDeclarator(int line, Type type) {
        ArrayList<JExpression> dimensions = new ArrayList<JExpression>();
        mustBe(LBRACK);
        dimensions.add(expression());
        mustBe(RBRACK);
        type = new ArrayTypeName(type);
        while (have(LBRACK)) {
            if (have(RBRACK)) {
                // We're done with dimension expressions
                type = new ArrayTypeName(type);
                while (have(LBRACK)) {
                    mustBe(RBRACK);
                    type = new ArrayTypeName(type);
                }
                return new JNewArrayOp(line, type, dimensions);
            } else {
                dimensions.add(expression());
                type = new ArrayTypeName(type);
                mustBe(RBRACK);
            }
        }
        return new JNewArrayOp(line, type, dimensions);
    }

    /**
     * Parse a literal.
     * 
     * <pre>
     *   literal ::= INT_LITERAL | CHAR_LITERAL | STRING_LITERAL | DOUBLE_LITERAL
     *             | TRUE        | FALSE        | NULL           | FLOAT_LITERAL
     *             | LONG_LITERAL
     * </pre>
     * 
     * @return an AST for a literal.
     */
    private JExpression literal() {
        int line = scanner.token().line();

        if (have(INT_LITERAL)) {
            return new JLiteralInt(line, scanner.previousToken().image());
        } else if (have(LONG_LITERAL)) {
            return new JLiteralLong(line, scanner.previousToken().image());
        } else if (have(DOUBLE_LITERAL)) {
            return new JLiteralDouble(line, scanner.previousToken().image());
        } else if (have(FLOAT_LITERAL)) {
            return new JLiteralFloat(line, scanner.previousToken().image());
        } else if (have(CHAR_LITERAL)) {
            return new JLiteralChar(line, scanner.previousToken().image());
        } else if (have(STRING_LITERAL)) {
            return new JLiteralString(line, scanner.previousToken().image());
        } else if (have(TRUE)) {
            return new JLiteralTrue(line);
        } else if (have(FALSE)) {
            return new JLiteralFalse(line);
        } else if (have(NULL)) {
            return new JLiteralNull(line);
        } else {
            reportParserError("Literal sought where '%s' found!", scanner.token()
                    .image());
            return new JWildExpression(line);
        }
    }

    // A tracing aid. Invoke to debug the parser to see what token
    // is being parsed at that point.
    //
    // private void trace( String message )
    // {
    // System.err.println( "["
    // + scanner.token().line()
    // + ": "
    // + message
    // + ", looking at a: "
    // + scanner.token().tokenRep()
    // + " = " + scanner.token().image() + "]" );
    // }
}

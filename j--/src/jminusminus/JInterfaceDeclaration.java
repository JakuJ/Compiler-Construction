package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

public class JInterfaceDeclaration extends JAST implements JTypeDecl, JMember {

	/** Interface modifiers. */
	private ArrayList<String> mods;

	/** Interface name. */
	private String name;

	/** The block of the interface block */
	private ArrayList<JMember> block;

	/** Context for this interface. */
	private InterfaceContext context;

	/** Super interface types. */
	private ArrayList<TypeName> superClasses;

	/** This interface type. */
	private Type type;

	public JInterfaceDeclaration(int line, ArrayList<String> mods, String name, ArrayList<TypeName> superClasses, ArrayList<JMember> block) {
		super(line);
		this.mods = mods;
		this.name = name;
		this.superClasses = superClasses;
		this.block = block;
	}

	/**
	 * Return the class name.
	 * 
	 * @return the class name.
	 */

	public String name() {
		return name;
	}

	/**
	 * Return the class' super classes array list.
	 * 
	 * @return the super classes.
	 */

	public Type superType() {
		return null;
	}

	public ArrayList<TypeName> superClasses() {
		return superClasses;
	}

	/**
	 * Return the type that this class declaration defines.
	 * 
	 * @return the defined type.
	 */

	public Type thisType() {
		return type;
	}

	public void declareThisType(Context context) {
		String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
				: JAST.compilationUnit.packageName() + "/" + name;
		CLEmitter partial = new CLEmitter(false);
		partial.addClass(mods, qualifiedName, Type.NULLTYPE.jvmName(), null, 
				false); // Object for superClass, just for now
		type = Type.typeFor(partial.toClass());
		context.addType(line, type);
	}

	/** This is the pre analyze of a JMember meaning that the interface is the inner interface */
	@Override
	public void preAnalyze(Context context, CLEmitter partial) {
		// TODO Auto-generated method stub

		// TODO: Analyze inner interfaces.
	}

	/** This is the pre analyze of a JTypeDecl meaning that the interface is the outer interface */
	public void preAnalyze(Context context) {
		// Construct a class context
		this.context = new InterfaceContext(this, context);
		
		//Add implicit abstract modifier
		if (!mods.contains("abstract")) {
			mods.add("abstract");
		}

		for (Type type : superClasses) {
			type = type.resolve(this.context);
			type.checkAccess(line, type);
			if (type.matchesExpected(Type.NULLTYPE) || !type.isInterface()) {
				JAST.compilationUnit.reportSemanticError(line, "Cannot extend a non interface type: %s",
						type.toString());
			}
		}

		// Create the (partial) class
		CLEmitter partial = new CLEmitter(false);

		// Add the class header to the partial class
		String superTypesjvmNames = (superClasses.size() > 0) ? "" : "java/lang/Object";
		for (Type type : superClasses) {
			superTypesjvmNames = superTypesjvmNames + type.jvmName();
		}

		String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
				: JAST.compilationUnit.packageName() + "/" + name;
		partial.addClass(mods, qualifiedName, superTypesjvmNames, null, false);

		// Pre-analyze the members and add them to the partial
		// class
		for (JMember member : block) {
			member.preAnalyze(this.context, partial);
		}

		// Get the Class rep for the (partial) class and make it
		// the
		// representation for this type
		Type id = this.context.lookupType(name);
		if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
			id.setClassRep(partial.toClass());
		}
	}

	public JAST analyze(Context context) {
		return this;
	}

	public void codegen(CLEmitter output) {
		// The interface header
		String superTypesjvmNames = (superClasses.size() > 0) ? "" : Type.OBJECT.toDescriptor();
		for (Type type : superClasses) {
			superTypesjvmNames = superTypesjvmNames + type.jvmName();
		}
		String qualifiedName = JAST.compilationUnit.packageName().equals("") ? name : JAST.compilationUnit.packageName() + "/" + name;
		output.addClass(mods, qualifiedName, superTypesjvmNames, null, false);

		// The members
		for (JMember member : block) {
			((JAST) member).codegen(output);
		}
	}

	public void writeToStdOut(PrettyPrinter p) {
		String superTypesString = "";

		if(superClasses != null){
			for (Type type : superClasses) {
				superTypesString = superTypesString + type.toString();
			}
		}

		p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\"" + " super=\"%s\">\n", line(), name, superTypesString);
		p.indentRight();
		if (context != null) {
			context.writeToStdOut(p);
		}
		if (mods != null) {
			p.println("<Modifiers>");
			p.indentRight();
			for (String mod : mods) {
				p.printf("<Modifier name=\"%s\"/>\n", mod);
			}
			p.indentLeft();
			p.println("</Modifiers>");
		}
		if (block != null) {
			p.println("<InterfaceBlock>");
			for (JMember member : block) {
				p.indentRight();
				((JAST) member).writeToStdOut(p);
				p.indentLeft();
			}
			p.println("</InterfaceBlock>");
		}
		p.indentLeft();
		p.println("</JInterfaceDeclaration>");
	}

	
	
}
package semant;

import syntaxtree.*;
import java.util.Vector;

public class TypeCheckVisitor extends visitor.TypeDepthFirstVisitor {
    // By extending TypeDepthFirstVisitor, we only have to override those
    // methods that differ from the generic visitor.

    private errormsg.ErrorMsg errorMsg;
    private SymbolTable classTable;
    private ClassInfo currClass;
    private MethodInfo currMethod;

    // Type constants
    final IntegerType INTTY = new IntegerType();
    final IntArrayType INTARRTY = new IntArrayType();
    final BooleanType BOOLTY = new BooleanType();

    public TypeCheckVisitor(errormsg.ErrorMsg e, SymbolTable s){
	errorMsg = e;
	classTable = s;
	currClass = null;
	currMethod = null;
    }

    // Identifier i1,i2;
    // Statement s;
    public Type visit(MainClass n) {
	// Mostly you just need to typecheck the body of 'main' here.
	// But as shown in Foo.java, you need care concerning 'this'.
	currClass = classTable.get(n.i1.toString());
	n.s.accept(this);
	return null;
    }
    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Type visit(ClassDeclSimple n)
    {
	String id = n.i.toString();
	if(classTable.get(id) == null)
	    System.out.println("Cannot Find Symbol: " + id);

	currClass = classTable.get(id);
	for ( int i = 0; i < n.vl.size(); i++ ) {
	    n.vl.elementAt(i).accept(this);
	}
	for ( int i = 0; i < n.ml.size(); i++ ) {
	    n.ml.elementAt(i).accept(this);
	}
	return null;
    }

    // Exp e1,e2;
    public Type visit(LessThan n) {
	Type t1 = n.e1.accept(this);

	Type t2 = n.e2.accept(this);


	if (!equal(t1, t2, INTTY))
	    errorMsg.error(n.pos, eIncompBiop("<", t1.toString(), t2.toString()));
	return INTTY;
    }
    // Exp e1,e2;
    public Type visit(Plus n) {
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);
	if(t1 == null)
	    System.out.println("Array NULL");
	if(t2 == null)
	    System.out.println("Integer NULL");

//	System.out.println(t1.toString() + " vs "  + t2.toString());
	if (!equal(t1, t2, INTTY))
	    errorMsg.error(n.pos, eIncompBiop("+", t1.toString(), t2.toString()));
	return INTTY;
    }
    // Exp e1,e2;
    public Type visit(Minus n) {
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);
	if (!equal(t1, t2, INTTY))
	    errorMsg.error(n.pos, eIncompBiop("-", t1.toString(), t2.toString()));
	return INTTY;
    }
    // Exp e1,e2;
    public Type visit(Times n) {
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);
	if (!equal(t1, t2, INTTY))
	    errorMsg.error(n.pos, eIncompBiop("*", t1.toString(), t2.toString()));
	return INTTY;
    }

    // Exp e;
    public Type visit(NewArray n)
    {
	System.out.println("Visiting new Array");
	n.e.accept(this);
	return INTARRTY;
    }

    // int i;
    public Type visit(IntegerLiteral n)
    {
	System.out.println("Visiting Integer literal");
	return INTTY;
    }
    public Type visit(IntArrayType n)
    {
	return INTARRTY;
    }

    public Type visit(BooleanType n) {
	return BOOLTY;
    }

    public Type visit(IntegerType n) {
	return INTTY;
    }

    // String s;
    public Type visit(IdentifierType n) {
	return n;
    }
    // String s;
    public Type visit(Identifier n)
    {
	Type t = null;
	if(currMethod == null)
	{
	    if(currClass == null)
		t = null;
	    else
		t = classTable.get(currClass.getName()).getField(n.s).type;
	}
	else
	{
	    t = classTable.get(currClass.getName()).getMethod(currMethod.getName()).getVar(n.s).type;
	}
	return t;
    }

    // Exp e;
    public Type visit(Not n)
    {
	n.e.accept(this);
	return BOOLTY;
    }

    public Type visit(True n)
    {
	return BOOLTY;
    }
    public Type visit(False n)
    {
	return BOOLTY;
    }
    public Type visit(This n)
    {
	return null;
    }

    // Exp e;
    public Type visit(ArrayLength n) {
	n.e.accept(this);
	return INTTY;
    }

    // Check whether t1 == t2 == target, but suppress error messages if
    // either t1 or t2 is null.
    private boolean equal(Type t1, Type t2, Type target) {
	if ( t1 == null || t2 == null )
	    return true;

	if (target == null)
	    throw new Error("target argument in method equal cannot be null");

	if (target instanceof IdentifierType && t1 instanceof IdentifierType
	    && t2 instanceof IdentifierType)
	    return ((IdentifierType) t1).s.equals(((IdentifierType) t2).s );

	if (!(target instanceof IdentifierType) &&
	    t1.toString().equals(target.toString()) &&
	    t2.toString().equals(target.toString()))
	    return true;

	return false;
    }

    // Methods for error reporting:
    private String eIncompTypes(String t1, String t2) {
	return "incompatible types \nfound   : " + t1
	    + "\nrequired: " + t2 ;
    }

    private String eIncompBiop(String op, String t1, String t2) {
	return "operator " + op + " cannot be applied to " + t1 + "," + t2 ;
    }
}

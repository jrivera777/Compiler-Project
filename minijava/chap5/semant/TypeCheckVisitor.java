//Doug Otstott
//Joseph Rivera
//Assignment 5: Type Checker

package semant;

import syntaxtree.*;
import java.util.Vector;

public class TypeCheckVisitor extends visitor.TypeDepthFirstVisitor
{
    private errormsg.ErrorMsg errorMsg;
    private SymbolTable classTable;
    private ClassInfo currClass;
    private MethodInfo currMethod;
    private boolean inMain;
    // Type constants
    final IntegerType INTTY = new IntegerType();
    final IntArrayType INTARRTY = new IntArrayType();
    final BooleanType BOOLTY = new BooleanType();

    public TypeCheckVisitor(errormsg.ErrorMsg e, SymbolTable s)
    {
	errorMsg = e;
	classTable = s;
	currClass = null;
	currMethod = null;
	inMain = false;
    }

    // Identifier i1,i2;
    // Statement s;
    public Type visit(MainClass n)
    {
	inMain = true;
	currClass = classTable.get(n.i1.toString());
	n.s.accept(this);
	inMain = false;
	return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public Type visit(ClassDeclSimple n)
    {
	if(!n.duplicate)
	{
	    String id = n.i.s;
	    currClass = classTable.get(id);
	    if(currClass == null)
		errorMsg.error(n.pos, "cannot Find Symbol '" + id + "'");
	    else
	    {
		for ( int i = 0; i < n.vl.size(); i++ )
		    n.vl.elementAt(i).accept(this);
		for ( int i = 0; i < n.ml.size(); i++ )
		    n.ml.elementAt(i).accept(this);
	    }
	}

	return null;
    }
    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public Type visit(MethodDecl n)
    {
	currMethod = currClass.getMethod(n.i.s);
	if(!n.duplicate)
	{
	    for ( int i = 0; i < n.fl.size(); i++ )
		n.fl.elementAt(i).accept(this);
	    for ( int i = 0; i < n.vl.size(); i++ )
		n.vl.elementAt(i).accept(this);
	    for ( int i = 0; i < n.sl.size(); i++ )
		n.sl.elementAt(i).accept(this);

	    Type ret = n.e.accept(this);
	    if(!ret.toString().equals(n.t.toString())) //check for correct return type
		errorMsg.error(n.e.pos, eIncompTypes(ret.toString(), n.t.toString()));
	}

	return null;
    }

    public Type visit(IntArrayType n)
    {
	return INTARRTY;
    }

    public Type visit(BooleanType n)
    {
	return BOOLTY;
    }

    public Type visit(IntegerType n)
    {
	return INTTY;
    }

    // String s;
    public Type visit(IdentifierType n)
    {
	if(classTable.get(n.s) == null)
	{
	    errorMsg.error(n.pos, "cannot Find Symbol '" + n.s + "'");
	    return null;
	}

	return n;
    }

    // Exp e;
    // Statement s1,s2;
    public Type visit(If n)
    {
	Type t1 = n.e.accept(this);
	if (t1 != null && !(t1 instanceof BooleanType))
	    errorMsg.error(n.e.pos, eIncompTypes(t1.toString(), BOOLTY.toString()));
	n.s1.accept(this);
	n.s2.accept(this);
	return null;
    }

    // Exp e;
    // Statement s;
    public Type visit(While n)
    {
	Type t1 = n.e.accept(this);
	if (t1 != null && !(t1 instanceof BooleanType))
	    errorMsg.error(n.e.pos, eIncompTypes(t1.toString(), BOOLTY.toString()));
	n.s.accept(this);
	return null;
    }

    // Identifier i;
    // Exp e;
    public Type visit(Assign n)
    {
	Type t1 = n.i.accept(this);
	Type t2 = n.e.accept(this);

	if(t1 == null)
	    errorMsg.error(n.pos, "cannot find Symbol '" + n.i.s +
			   "' in class " + currClass.getName());
	if(!equal(t1, t2, t1))
	{
	    if (!t1.toString().equals(t2.toString()))
		errorMsg.error(n.pos, eIncompTypes(t2.toString(), t1.toString()));
	}
	return null;
    }

    // Identifier i;
    // Exp e1,e2;
    public Type visit(ArrayAssign n)
    {
	Type arrType = n.i.accept(this);
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);

	if(arrType == null)
	    errorMsg.error(n.pos, "cannot find Symbol '" + n.i.s +
			   "' in class " + currClass.getName());
	if(t1 != null && !(t1 instanceof IntegerType))
	    errorMsg.error(n.e1.pos, eIncompTypes(t1.toString(), INTTY.toString()));
	if(t2 != null && !(t2 instanceof IntegerType))
	    errorMsg.error(n.e2.pos, eIncompTypes(t2.toString(), INTTY.toString()));

	return null;
    }

    // Exp e1,e2;
    public Type visit(And n)
    {
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);
	if (!equal(t1, t2, BOOLTY))
	    errorMsg.error(n.pos, eIncompBiop("&&", t1.toString(), t2.toString()));
	return BOOLTY;
    }

    // Exp e1,e2;
    public Type visit(LessThan n) {
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);

	if(t1 != null &&  t2 != null && (!(t1 instanceof IntegerType) || !(t2 instanceof IntegerType)))
	    errorMsg.error(n.pos, eIncompBiop("<", t1.toString(), t2.toString()));

	return BOOLTY;
    }

    // Exp e1,e2;
    public Type visit(Plus n)
    {
	Type t1 = n.e1.accept(this);
	Type t2 = n.e2.accept(this);

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

    // Exp e1,e2;
    public Type visit(ArrayLookup n) {
	n.e1.accept(this);
	Type t1 = n.e2.accept(this);
	if(t1 != null && !(t1 instanceof IntegerType))
	    errorMsg.error(n.e2.pos, eIncompTypes(t1.toString(), INTTY.toString()));

	return INTTY;
    }

    // Exp e;
    public Type visit(ArrayLength n) {
	n.e.accept(this);
	return INTTY;
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public Type visit(Call n)
    {
	String method = n.i.s;
	MethodInfo mI = null;
	Type t1 = n.e.accept(this); //determine type of caller

	if(t1 == null)
	    return null;

	if(!(t1 instanceof IdentifierType))
	    errorMsg.error(n.e.pos, t1.toString() + " cannot be dereferenced"); // caller must be a class type
	else
	{
	    ClassInfo callingClass = classTable.get(t1.toString());
	    if(callingClass != null)
	    {
		n.fullname = callingClass.getName() + "$" + method;
		//build type list for called method
		StringBuilder sb = new StringBuilder("(");
		for(int i = 0; i < n.el.size(); i++)
		{
		    Type currType = n.el.elementAt(i).accept(this);
		    sb.append(currType.toString());
		    if( i + 1 != n.el.size())
			sb.append(", ");
		}
		sb.append(")");

		mI = callingClass.getMethod(method);
		if(mI == null)
		    errorMsg.error(n.pos,"cannot resolve symbol\nsymbol  : method " +
				   method + sb.toString() + "\nlocation: class " +
				   callingClass.getName());
		else
		{
		    if(!sb.toString().equals(mI.getFormalsTypes()))
			errorMsg.error(n.e.pos,
				       eMethGivenTypes(currClass.getName() + "." + method,
						       method + sb.toString(),	method + mI.getFormalsTypes()));
		}
	    }

	}
	if(mI == null)
	    return null;
	return mI.getReturnType();
    }

    // int i;
    public Type visit(IntegerLiteral n)
    {
	return INTTY;
    }

    public Type visit(True n)
    {
	return BOOLTY;
    }

    public Type visit(False n)
    {
	return BOOLTY;
    }

    // String s;
    public Type visit(IdentifierExp n)
    {
	VariableInfo v = null;

	if(currMethod != null)
	{
	    v = currMethod.getVar(n.s); //check locals
	    if(v != null)
		return v.type;
	}

	v = currClass.getField(n.s); //check class variables
	if(v != null)
	    return v.type;

	errorMsg.error(n.pos,"cannot resolve symbol\nsymbol  : variable " + n.s +
		       "\nlocation: class " + currClass.getName());
	return null;
    }

    public Type visit(This n)
    {
	if(inMain)
	    errorMsg.error(n.pos, "non-static variable \"this\" cannot be referenced from a static context");
	if(currClass != null)
	    return new IdentifierType(currClass.getName());
	return null;
    }

    // Exp e;
    public Type visit(NewArray n)
    {
	Type t1 = n.e.accept(this);
	if(t1 != null && !(t1 instanceof IntegerType))
	    errorMsg.error(n.e.pos, eIncompTypes(t1.toString(), INTTY.toString()));
	return INTARRTY;
    }

    // Identifier i;
    public Type visit(NewObject n)
    {
	if(classTable.get(n.i.s) == null)
	    errorMsg.error(n.pos, "cannot Find Symbol '" + n.i.s + "'");
	return new IdentifierType(n.i.s);
    }

    // Exp e;
    public Type visit(Not n)
    {
	n.e.accept(this);
	return BOOLTY;
    }

    // String s;
    public Type visit(Identifier n)
    {
	VariableInfo v = null;
	if(currMethod != null)
	{
	    v = currMethod.getVar(n.s);
	    if(v != null)
		return v.type;
	}

	v = currClass.getField(n.s);
	if(v != null)
	    return v.type;

	return null;
    }

    // Check whether t1 == t2 == target, but suppress error messages if
    // either t1 or t2 is null.
    private boolean equal(Type t1, Type t2, Type target)
    {
	if ( t1 == null || t2 == null )
	    return true;

	if (target == null)
	    throw new Error("target argument in method equal cannot be null");

	if (target instanceof IdentifierType && t1 instanceof IdentifierType
	    && t2 instanceof IdentifierType)
	    return ((IdentifierType)t1).s.equals(((IdentifierType)target).s) &&
		((IdentifierType) t1).s.equals(((IdentifierType) t2).s );

	if (!(target instanceof IdentifierType) &&
	    t1.toString().equals(target.toString()) &&
	    t2.toString().equals(target.toString()))
	    return true;

	return false;
    }

    // Methods for error reporting:
    private String eMethGivenTypes(String method, String t1, String t2)
    {
	return method +" cannot be applied to given types \nfound   : " + t1
	    + "\nrequired: " + t2 ;
    }
    private String eIncompTypes(String t1, String t2) {
	return "incompatible types \nfound   : " + t1
	    + "\nrequired: " + t2 ;
    }

    private String eIncompBiop(String op, String t1, String t2) {
	return "operator " + op + " cannot be applied to " + t1 + "," + t2 ;
    }
}

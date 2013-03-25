//Doug Otstott
//Joseph Rivera
//Assignment 5 Type Checker

package semant;

import syntaxtree.*;

public class BuildSymbolTableVisitor extends visitor.DepthFirstVisitor
{
    private errormsg.ErrorMsg errorMsg;
    private SymbolTable classTable;
    private ClassInfo  currClass;
    private MethodInfo currMethod;

    public BuildSymbolTableVisitor(errormsg.ErrorMsg e)
    {
	errorMsg   = e;
	classTable = new SymbolTable();
	currClass  = null;
	currMethod = null;
    }

    public SymbolTable getSymbolTable() {
	return classTable;
    }

    // Identifier i1,i2;
    // Statement s;3
    public void visit(MainClass n)
    {
	String id = n.i1.toString();
	classTable.addClass(id, new ClassInfo(id));
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n)
    {
	String id = n.i.toString();
	if (currMethod == null)
	{
	    if (!currClass.addField(id, new VariableInfo(n.t)))
		errorMsg.error(n.pos, id + " is already defined in " +
			       currClass.getName());
	}
	else if (!currMethod.addVar(id, new VariableInfo(n.t)))
	{
	    errorMsg.error(n.pos, id + " is already defined in " +
			   currClass.getName() + "." +
			   currMethod.getName() +  currMethod.getFormalsTypes());
	}
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n)
    {
	String id = n.i.toString();
	if(currClass == null)
	{
	    if(classTable.get(id) == null)
	    {
		currClass =  new ClassInfo(id);
		if(classTable.addClass(id, currClass))
		{
		    for(int i = 0; i < n.vl.size(); i++)
			n.vl.elementAt(i).accept(this); //visit each local variable declaration in the class
		    for(int i = 0; i < n.ml.size(); i++)
			n.ml.elementAt(i).accept(this); //visit all class methods
		}
	    }
	    else
	    {
		n.duplicate = true;
		errorMsg.error(n.pos, "duplicate class : " + id);
	    }
	    currClass = null;
	}
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n)
    {
	String id = n.i.toString();
	currMethod = currClass.getMethod(id);
	if(currMethod == null)
	{
	    currMethod  = new MethodInfo(id, n.t);
	    currClass.addMethod(id, currMethod);
	    for(int i = 0; i < n.fl.size(); i++)
		n.fl.elementAt(i).accept(this); //visit all formals, i.e. parameters
	    for(int i = 0; i < n.vl.size(); i++)
		n.vl.elementAt(i).accept(this); //visit all local variables
	    for(int i = 0; i < n.sl.size(); i++)
		n.sl.elementAt(i).accept(this);
	}
	else
	{
	    n.duplicate = true;
	    errorMsg.error(n.pos, id + currMethod.getFormalsTypes() + " is already defined in " + currClass.getName());
	}
	currMethod = null;
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n)
    {
	String id = n.i.toString();
	if(!currMethod.addFormal(id, new VariableInfo( n.t)))
	    errorMsg.error(n.pos, id + " is already defined in " +
			   currClass.getName() + "." + currMethod.getName() +
			   currMethod.getFormalsTypes());
    }
}

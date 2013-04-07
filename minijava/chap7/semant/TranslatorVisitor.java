package semant;

import syntaxtree.*;

public class TranslatorVisitor extends visitor.ExpDepthFirstVisitor {

    private SymbolTable  classTable;
    private frame.Frame  currFrame;
    private ClassInfo    currClass;
    private MethodInfo   currMethod;
    private tree.Exp     currThis;
    private Frag         frags;		// Linked list of accumlated fragments.
    private boolean      optimize;	// Do we want to optimize?

    public TranslatorVisitor(SymbolTable t, frame.Frame f, boolean optim)
    {
	classTable = t;
	currFrame  = f;
	currClass  = null;
	currMethod = null;
	currThis   = null;
	frags      = null;
	optimize   = optim;
    }

    public Frag getResult()
    {
	// Reverse frags and return it.
	Frag old = frags;
	frags = null;
	while (old != null) {
	    Frag temp = old.next;
	    old.next = frags;
	    frags = old;
	    old = temp;
	}
	return frags;
    }

    // Identifier i1,i2;
    // Statement s;
    // Here I give you the complete code for MainClass:
    public semant.Exp visit(MainClass n)
    {
	String id = n.i1.toString();

	currClass = classTable.get(id);

	currFrame = currFrame.newFrame(new temp.Label("main"), null);

	semant.Exp body = n.s.accept(this);
	procEntryExit(body, currFrame);

	return null;
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public semant.Exp visit(ClassDeclSimple n)
    {
	VariableInfo varInfo = null;
	int wSize = currFrame.wordSize();
	int offset = 0;

	currClass = classTable.get(n.i);

	for ( int i = 0; i < n.vl.size(); i++ )
	{
	    varInfo = currClass.getField(n.vl.getElement(i).i);
	    varInfo.access = new InHeap(offset);
	    offset += wSize;
	}

	for ( int i = 0; i < n.ml.size(); i++ )
	    n.ml.elementAt(i).accept(this);
	return null;
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public semant.Exp visit(MethodDecl n)
    {
	currMethod = currClass.getMethod(n.i);
	VariableInfo
	currMethod.addFormal(currClass.getName(),
//	currFrame = currFrame.newFrame(
	for ( int i = 0; i < n.fl.size(); i++ )
	{
	    n.fl.elementAt(i).accept(this);
	}
	for ( int i = 0; i < n.vl.size(); i++ )
	{
	    n.vl.elementAt(i).accept(this);
	}
	for ( int i = 0; i < n.sl.size(); i++ )
	{
	    n.sl.elementAt(i).accept(this);
	}
	n.e.accept(this);
	return null;
    }

    // StatementList sl;
    public semant.Exp visit(Block n)
    {
	for ( int i = 0; i < n.sl.size(); i++ )
	{
	    semant.Exp currStm = n.sl.elementAt(i).accept(this);
	    procEntryExit(currStm, currFrame);
	}
	return new Nx(n);
    }

    // Exp e;
    // Statement s1,s2;
    public semant.Exp visit(If n)
    {
	n.e.accept(this);
	n.s1.accept(this);
	n.s2.accept(this);
	return null;
    }

    // Exp e;
    // Statement s;
    public semant.Exp visit(While n)
    {
	n.e.accept(this);
	n.s.accept(this);
	return null;
    }

    // Exp e;
    public semant.Exp visit(Print n)
    {
	n.e.accept(this);
	return null;
    }

    // Identifier i;
    // Exp e;
    public semant.Exp visit(Assign n)
    {
	n.i.accept(this);
	n.e.accept(this);
	return null;
    }

    // Identifier i;
    // Exp e1,e2;
    public semant.Exp visit(ArrayAssign n) {
	n.i.accept(this);
	n.e1.accept(this);
	n.e2.accept(this);
	return null;
    }
    // int i;
    public semant.Exp visit(IntegerLiteral n)
    {
	return new Ex(new tree.CONST(n.i));
    }

    public semant.Exp visit(True n)
    {
	return new Ex(new tree.CONST(1));
    }

    public semant.Exp visit(False n)
    {
	return new Ex(new tree.CONST(0));
    }

    // Now we have some auxiliary functions:

    // Create a fragment for a function and add it to the front of frags.
    private void procEntryExit(Exp body, frame.Frame funcFrame) {
	Frag func = new ProcFrag(funcFrame.procEntryExit1(body.unNx()), funcFrame);
	func.next = frags;
	frags = func;
    }

    // plus and mul are useful abbreviations that could do simple optimizations.

    private tree.Exp plus(tree.Exp e1, tree.Exp e2) {
	return new tree.BINOP(tree.BINOP.PLUS, e1, e2);
    }

    private tree.Exp mul(tree.Exp e1, tree.Exp e2) {
	return new tree.BINOP(tree.BINOP.MUL, e1, e2);
    }

    // Finally, we have several nested auxiliary classes:

    class InHeap extends frame.Access
    {
	int offset;

	InHeap(int o) {offset=o;}

	// Here the base pointer will be the "this" pointer to the object.
	public tree.Exp exp(tree.Exp basePtr) {
	    return new tree.MEM(plus(basePtr, new tree.CONST(offset)));
	}
    }

    // The subclasses of semant.Exp (Ex, Nx, Cx, RelCx, IfThenElseExp, ...)
    // naturally represent the various phrases of the abstract syntax.
    // They let us hold off on generating tree code for a phrase until
    // we see the *context* in which it is used.

    class Ex extends Exp { 			// page 141
	tree.Exp exp;
	Ex(tree.Exp e) {exp=e;}

	tree.Exp unEx() {return exp;}

	tree.Stm unNx() {return new tree.EXPR(exp);}

	tree.Stm unCx(temp.Label t, temp.Label f) {
	    return new tree.CJUMP(tree.CJUMP.NE, exp, new tree.CONST(0), t, f);
	}
    }

    class Nx extends Exp { 			// page 141
	tree.Stm stm;
	Nx(tree.Stm s) {stm=s;}

	tree.Exp unEx() {throw new Error("unEx applied to Nx");}

	tree.Stm unNx() {return stm;}

	tree.Stm unCx(temp.Label t, temp.Label f) {
	    throw new Error("unCx applied to Nx");
	}
    }

    abstract class Cx extends Exp {  		// page 142
	tree.Exp unEx() {
	    temp.Temp r = new temp.Temp();
	    temp.Label t = new temp.Label();
	    temp.Label f = new temp.Label();

	    return new tree.ESEQ(
		new tree.SEQ(new tree.MOVE(new tree.TEMP(r), new tree.CONST(1)),
			     new tree.SEQ(this.unCx(t,f),
					  new tree.SEQ(new tree.LABEL(f),
						       new tree.SEQ(new tree.MOVE(new tree.TEMP(r), new tree.CONST(0)),
								    new tree.LABEL(t))))),
		new tree.TEMP(r));
	}

	abstract tree.Stm unCx(temp.Label t, temp.Label f);

	tree.Stm unNx()
	{

	}
    }

    class RelCx extends Cx { 			// page 149
	int relop;
	tree.Exp left;
	tree.Exp right;
	RelCx(int rel, tree.Exp l, tree.Exp r) {relop=rel; left=l; right=r;}

	tree.Stm unCx(temp.Label t, temp.Label f)
	{
	    return new tree.CJUMP(relop, left, right, t, f);
	}
    }

    class IfThenElseExp extends Exp
    {     	// page 150
	Exp cond, a, b;
	temp.Label t = new temp.Label();
	temp.Label f = new temp.Label();
	temp.Label join = new temp.Label();
	IfThenElseExp(Exp cc, Exp aa, Exp bb) {cond=cc; a=aa; b=bb;}

	tree.Exp unEx()
	{
	    temp.Temp r = new temp.Temp();
	    return new tree.ESEQ(
		new tree.SEQ(cond.unCx(t,f),
			     new tree.SEQ(new tree.LABEL(t),
					  new tree.SEQ(new tree.MOVE(new tree.TEMP(r), a.unEx()),
						       new tree.SEQ(new tree.JUMP(new tree.LABEL(join)),
								    new tree.SEQ(new tree.LABEL(f),
										 new tree.SEQ(new tree.MOVE(new tree.TEMP(r), b.unEx()),
											      new tree.LABEL(join))))))),
		new tree.TEMP(r));
	}

	tree.Stm unNx()
	{

	}

	tree.Stm unCx(temp.Label tt, temp.Label ff)
	{
	    // temp.Label z = new temp.Label();
	    // return new tree.SEQ(a.unCx(z,f),
	    // 			new SEQ(
	}
    }

    public util.BoolList generateFalseBL(int size)
    {
	util.BoolList blist = new util.BoolList(false, null);
	for(int i = 0; i < size - 1; i++)
	    blist.tail = new util.BoolList(false, null);

	return blist;
    }
}

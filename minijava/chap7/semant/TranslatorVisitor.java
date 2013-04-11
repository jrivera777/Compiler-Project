package semant;

import syntaxtree.*;

public class TranslatorVisitor extends visitor.ExpDepthFirstVisitor
{
    private SymbolTable  classTable;
    private frame.Frame  currFrame;
    private ClassInfo    currClass;
    private MethodInfo   currMethod;
    private tree.Exp     currThis;
    private Frag         frags;		// Linked list of accumlated fragments.
    private boolean      optimize;	// Do we want to optimize?
    tree.Print printer = new tree.Print(System.out);
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
	while (old != null)
	{
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

	currClass = classTable.get(n.i.s);
	for ( int i = 0; i < n.vl.size(); i++ )
	{
	    varInfo = currClass.getField(n.vl.elementAt(i).i.s);
	    System.out.println("Creating Access for field '" +  n.vl.elementAt(i).i.s + "' with offset " + offset);
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
	currMethod = currClass.getMethod(n.i.s);
	currFrame = currFrame.newFrame(new temp.Label(n.i.s), generateFalseBL(n.fl.size() + 1));

	frame.AccessList formals = currFrame.formals.tail;
	while(formals.tail != null)
	{
	    formals.head = currFrame.allocLocal(false);
	    formals = formals.tail;
	}
	for(int i = 0; i < n.vl.size(); i++)
	{
	    VariableInfo vInfo = currMethod.getVar(n.vl.elementAt(i).i.s);
	    vInfo.access = currFrame.allocLocal(false);
	}
	tree.Stm bodySeq = buildSEQ(n.sl, 0);
	bodySeq = new tree.SEQ(bodySeq, new tree.MOVE(new tree.TEMP(currFrame.RVCallee()),n.e.accept(this).unEx()));
	procEntryExit(new Nx(bodySeq), currFrame);
	return null;
    }

    // StatementList sl;
    public semant.Exp visit(Block n)
    {
	tree.Stm currStm = buildSEQ(n.sl, 0);
	procEntryExit(new Nx(currStm), currFrame);
	return null;
    }

//     Exp e;
    // Statement s1,s2;
    public semant.Exp visit(If n)
    {
	IfThenElseExp ifte = new IfThenElseExp(n.e.accept(this), n.s1.accept(this), n.s2.accept(this));
	tree.Stm brickSquad = ifte.unNx();
	procEntryExit(new Nx(brickSquad), currFrame);
	return null;
   }

//     // Exp e;
//     // Statement s;
//     public semant.Exp visit(While n)
//     {
// 	temp.Label lbl = new temp.Label();
// //	new tree.SEQ(new tree.LABEL(lbl)
// //		     IfThenElseExp ifte = new IfThenElseExp(n.e.accept(this)
// 	return null;
//     }

    // Exp e;
    public semant.Exp visit(Print n)
    {

	System.out.println("Visiting Print Statement");
	semant.Exp se = n.e.accept(this);
	System.out.println("Accepted!!");
	if(se == null)
	    System.out.println("BROKEN");
	tree.Exp e = se.unEx();
	System.out.println("About to print");
	printer.prExp(e);
	tree.ExpList eList = new tree.ExpList(e, null);
	tree.Exp prnt = currFrame.externalCall("printInt", eList);

	return new Ex(prnt);
    }

//     // Identifier i;
//     // Exp e;
//     public semant.Exp visit(Assign n)
//     {
// 	n.i.accept(this);
// 	n.e.accept(this);
// 	return null;
//     }

//     // Identifier i;
//     // Exp e1,e2;
//     public semant.Exp visit(ArrayAssign n) {
// 	n.i.accept(this);
// 	n.e1.accept(this);
// 	n.e2.accept(this);
// 	return null;
//     }

//     // Exp e1,e2;
//     public semant.Exp visit(And n)
//     {
// 	n.e1.accept(this);
// 	n.e2.accept(this);
// 	return null;
//     }

//     // Exp e1,e2;
//     public semant.Exp visit(LessThan n)
//     {
// 	n.e1.accept(this);
// 	n.e2.accept(this);
// 	return null;
//     }

//     // Exp e1,e2;
//     public semant.Exp visit(Plus n)
//     {
// 	tree.Exp i1 = ((Ex)n.e1.accept(this)).unEx();
// 	tree.Exp i2 = ((Ex)n.e2.accept(this)).unEx();
// 	return new Ex(plus(i1, i2, true));
//     }

//     // Exp e1,e2;
//     public semant.Exp visit(Minus n)
//     {
// 	tree.Exp i1 = ((Ex)n.e1.accept(this)).unEx();
// 	tree.Exp i2 = ((Ex)n.e2.accept(this)).unEx();
// 	return new Ex(plus(i1, i2, false));
//     }

//     // Exp e1,e2;
//     public semant.Exp visit(Times n)
//     {
// 	tree.Exp i1 = ((Ex)n.e1.accept(this)).unEx();
// 	tree.Exp i2 = ((Ex)n.e2.accept(this)).unEx();
// 	return new Ex(mul(i1, i2));
//     }

    // Exp e1,e2;
    public semant.Exp visit(ArrayLookup n)
    {
	n.e1.accept(this);
	n.e2.accept(this);
	return null;
    }

//     // Exp e;
//     public semant.Exp visit(ArrayLength n)
//     {
// 	n.e.accept(this);
// 	return null;
//     }

//     // Exp e;
//     // Identifier i;
//     // ExpList el;
//     public semant.Exp visit(Call n)
//     {
// 	n.e.accept(this);
// 	n.i.accept(this);
// 	for ( int i = 0; i < n.el.size(); i++ ) {
// 	    n.el.elementAt(i).accept(this);
// 	}
// 	return null;
//     }

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

//     // String s;
//     public semant.Exp visit(IdentifierExp n)
//     {
// 	return null;
//     }

//     public semant.Exp visit(This n)
//     {
// 	return null;
//     }

//     // Exp e;
//     public semant.Exp visit(NewArray n)
//     {
// 	n.e.accept(this);
// 	return null;
//     }

    //Identifier i;
    public semant.Exp visit(NewObject n)
    {
	System.out.println("Creating Class of type <" + n.i.s + ">");
	int numFields = classTable.get(n.i.s).getFieldsCount();
	System.out.printf("Visiting NewObject: %d fields to intialize\n", numFields);
	tree.ExpList params = new tree.ExpList(new tree.CONST(numFields),
					       new tree.ExpList(new tree.CONST(currFrame.wordSize()), null));
	System.out.println("Created parameters");
	tree.Exp obj = currFrame.externalCall("calloc", params);
	printer.prExp(obj);
	return new Ex(obj);
    }

//     // Exp e;
//     public semant.Exp visit(Not n)
//     {
// 	n.e.accept(this);
// 	return null;
//     }

//     // String s;
//     public semant.Exp visit(Identifier n)
//     {
// 	VariableInfo v = null;
// 	if(currMethod != null)
// 	{
// 	    v = currMethod.getVar(n.s);
// 	    if(v != null)
// 		return new Ex(v.access.exp(new tree.TEMP(currFrame.FP())));
// 	}

// 	v = currClass.getField(n.s);
// 	return new Ex(v.access.exp(currThis));
//     }


    // Now we have some auxiliary functions:

    // Create a fragment for a function and add it to the front of frags.
    private void procEntryExit(Exp body, frame.Frame funcFrame)
    {
	Frag func = new ProcFrag(funcFrame.procEntryExit1(body.unNx()), funcFrame);
	func.next = frags;
	frags = func;
    }

    // plus and mul are useful abbreviations that could do simple optimizations.

    private tree.Exp plus(tree.Exp e1, tree.Exp e2, boolean plus)
    {
	if(e1 instanceof tree.CONST && e2 instanceof tree.CONST)
	{
	    tree.CONST c1 = (tree.CONST)e1;
	    tree.CONST c2 = (tree.CONST)e2;
	    if(!plus)
		return new tree.CONST(c1.value - c2.value);
	    return new tree.CONST(c1.value + c2.value);
	}

	if(!plus)
	    return new tree.BINOP(tree.BINOP.MINUS, e1, e2);
	return new tree.BINOP(tree.BINOP.PLUS, e1, e2);
    }

    private tree.Exp mul(tree.Exp e1, tree.Exp e2)
    {
	if(e1 instanceof tree.CONST && e2 instanceof tree.CONST)
	{
	    tree.CONST c1 = (tree.CONST)e1;
	    tree.CONST c2 = (tree.CONST)e2;
	    return new tree.CONST(c1.value + c2.value);
	}
	return new tree.BINOP(tree.BINOP.MUL, e1, e2);
    }

    // Finally, we have several nested auxiliary classes:

    class InHeap extends frame.Access
    {
	int offset;
	InHeap(int o) {offset=o;}

	// Here the base pointer will be the "this" pointer to the object.
	public tree.Exp exp(tree.Exp basePtr)
	{
	    return new tree.MEM(plus(basePtr, new tree.CONST(offset), true));
	}
    }

    // The subclasses of semant.Exp (Ex, Nx, Cx, RelCx, IfThenElseExp, ...)
    // naturally represent the various phrases of the abstract syntax.
    // They let us hold off on generating tree code for a phrase until
    // we see the *context* in which it is used.

    class Ex extends Exp
    { 			// page 141
	tree.Exp exp;
	Ex(tree.Exp e) {exp=e;}

	tree.Exp unEx() {return exp;}

	tree.Stm unNx() {return new tree.EXPR(exp);}

	tree.Stm unCx(temp.Label t, temp.Label f)
	{
	    return new tree.CJUMP(tree.CJUMP.NE, exp, new tree.CONST(0), t, f);
	}
    }

    class Nx extends Exp
    {
	// page 141
	tree.Stm stm;
	Nx(tree.Stm s) {stm=s;}

	tree.Exp unEx() {throw new Error("unEx applied to Nx");}

	tree.Stm unNx() {return stm;}

	tree.Stm unCx(temp.Label t, temp.Label f) {
	    throw new Error("unCx applied to Nx");
	}
    }

    abstract class Cx extends Exp
    {
	// page 142
	tree.Exp unEx()
	{
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
	    // if(exp instanceof tree.CONST)
	    // {
	    // 	if(((tree.CONST)exp).value == 0)
	    // 	    return new tree.JUMP(f);
	    // 	return new tree.JUMP(t);
	    // }
	    // return this.unCx(t,f);
	    return null;
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
    {
	// page 150
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
			     new tree.SEQ(
				 new tree.LABEL(t),
				 new tree.SEQ(
				     new tree.MOVE(new tree.TEMP(r), a.unEx()),
				     new tree.SEQ(
					 new tree.JUMP(join),
					 new tree.SEQ(
					     new tree.LABEL(f),
					     new tree.SEQ(
						 new tree.MOVE(new tree.TEMP(r), b.unEx()),
						 new tree.LABEL(join))))))),
		new tree.TEMP(r));
	}

	tree.Stm unNx()
	{
	    return new tree.SEQ(cond.unCx(t,f),
				new tree.SEQ(
				    new tree.LABEL(t),
				    new tree.SEQ(
					a.unNx(),
					new tree.SEQ(
					    new tree.JUMP(join),
					    new tree.SEQ(
						new tree.LABEL(f),
						new tree.SEQ(b.unNx(), new tree.LABEL(join)))))));

	}

	tree.Stm unCx(temp.Label tt, temp.Label ff)
	{
	    return new tree.SEQ(cond.unCx(t,f),
				new tree.SEQ(
				    new tree.LABEL(t),
				    new tree.SEQ(a.unCx(tt,ff),
						 new tree.SEQ(
						     new tree.LABEL(f),
						     b.unCx(tt,ff)))));
	}
    }

    //Auxiliary auxiliary functions
    public util.BoolList generateFalseBL(int size)
    {
	util.BoolList blist = new util.BoolList(false, null);
	for(int i = 0; i < size - 1; i++)
	    blist.tail = new util.BoolList(false, null);

	return blist;
    }

    public tree.Stm buildSEQ(StatementList sl, int pos)
    {
	if(pos == sl.size())
	    return sl.elementAt(pos).accept(this).unNx();

	tree.Stm exp = sl.elementAt(pos).accept(this).unNx();
	return new tree.SEQ(exp, buildSEQ(sl, pos+1));
    }
}

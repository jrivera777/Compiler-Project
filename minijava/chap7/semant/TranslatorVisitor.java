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
	String fullname = currClass.getName() + "$" + n.i.s; //how else can I get this at this point.  Only Call objects have fullname!
	currFrame = currFrame.newFrame(new temp.Label(fullname), generateFalseBL(n.fl.size() + 1));

	int cnt = 0;
	frame.AccessList curr = currFrame.formals.tail;
	if(curr != null)
	{
	    while(curr != null) //allocate space for curr
	    {
		VariableInfo vInfo = currMethod.getVar(n.fl.elementAt(cnt++).i.s);
		vInfo.access = curr.head;
		curr = curr.tail;
	    }
	}

	currThis = currFrame.formals.head.exp(new tree.TEMP(currFrame.FP())); //set up currThis for future references

	for(int i = 0; i < n.vl.size(); i++) //allocate space for local variables
	{
	    VariableInfo vInfo = currMethod.getVar(n.vl.elementAt(i).i.s);
	    vInfo.access = currFrame.allocLocal(false);
	}
	tree.Stm bodySeq = buildSEQ(n.sl, 0);
	if(bodySeq == null)
	    bodySeq = new tree.MOVE(new tree.TEMP(currFrame.RVCallee()),n.e.accept(this).unEx());
	else
	   bodySeq = new tree.SEQ(bodySeq, new tree.MOVE(new tree.TEMP(currFrame.RVCallee()),n.e.accept(this).unEx()));

	Exp result = new Nx(bodySeq);
	procEntryExit(result, currFrame);
	return result;
    }

    // StatementList sl;
    public semant.Exp visit(Block n)
    {
	tree.Stm currStm = buildSEQ(n.sl, 0);
	return new Nx(currStm);
    }

    //     Exp e;
    // Statement s1,s2;
    public semant.Exp visit(If n)
    {
	IfThenElseExp ifte = new IfThenElseExp(n.e.accept(this), n.s1.accept(this), n.s2.accept(this));
	return new Nx(ifte.unNx());
    }

    // Exp e;
    // Statement s;
    public semant.Exp visit(While n)
    {
	temp.Label test = new temp.Label();
	temp.Label done = new temp.Label();
	IfThenElseExp ifte = new IfThenElseExp(n.e.accept(this), n.s.accept(this), new Ex(new tree.CONST(0)));
	return new Nx(new tree.SEQ(new tree.LABEL(test),
				   new tree.SEQ(ifte.unCx(test, done),
						new tree.LABEL(done))));
    }

    // Exp e;
    public semant.Exp visit(Print n)
    {
	semant.Exp se = n.e.accept(this);
	tree.Exp e = se.unEx();
	tree.ExpList eList = new tree.ExpList(e, null);
	tree.Exp prnt = currFrame.externalCall("printInt", eList);

	return new Ex(prnt);
    }

    // Identifier i;
    // Exp e;
    public semant.Exp visit(Assign n)
    {
	tree.Stm remix = new tree.MOVE(n.i.accept(this).unEx(), n.e.accept(this).unEx());
	return new Nx(remix);
    }

    // Identifier i;
    // Exp e1,e2;
    public semant.Exp visit(ArrayAssign n)
    {
	tree.Exp arrAddr = n.i.accept(this).unEx(); //base address
	tree.Exp offset = n.e1.accept(this).unEx();
	offset = plus(offset, new tree.CONST(1), true); //compensate for length spot in array
	tree.Exp loc = mul(new tree.CONST(currFrame.wordSize()), offset);

	tree.Stm wutang = new tree.MOVE(plus(arrAddr, loc, true), n.e2.accept(this).unEx());
	return new Nx(wutang);
    }

    // Exp e1,e2;
    public semant.Exp visit(And n)
    {
	IfThenElseExp and = new IfThenElseExp(n.e1.accept(this), n.e2.accept(this), new Ex(new tree.CONST(0)));
	// printer.debug("AND value from unEx():");
	// printer.prExp(and.unEx());
	return and;
    }

    // Exp e1,e2;
    public semant.Exp visit(LessThan n)
    {
	tree.Exp l_value = n.e1.accept(this).unEx();
	tree.Exp r_value = n.e2.accept(this).unEx();
	return new RelCx(tree.CJUMP.LT, l_value, r_value);
    }

    // Exp e1,e2;
    public semant.Exp visit(Plus n)
    {
	tree.Exp i1 = ((Ex)n.e1.accept(this)).unEx();
	tree.Exp i2 = ((Ex)n.e2.accept(this)).unEx();
	return new Ex(plus(i1, i2, true));
    }

    // Exp e1,e2;
    public semant.Exp visit(Minus n)
    {
	tree.Exp i1 = ((Ex)n.e1.accept(this)).unEx();
	tree.Exp i2 = ((Ex)n.e2.accept(this)).unEx();
	return new Ex(plus(i1, i2, false));
    }

    // Exp e1,e2;
    public semant.Exp visit(Times n)
    {
	tree.Exp i1 = ((Ex)n.e1.accept(this)).unEx();
	tree.Exp i2 = ((Ex)n.e2.accept(this)).unEx();
	return new Ex(mul(i1, i2));
    }

    // Exp e1,e2;
    public semant.Exp visit(ArrayLookup n)
    {
	tree.Exp arrAddr = n.e1.accept(this).unEx(); //base address
	tree.Exp offset = n.e2.accept(this).unEx();
	offset = plus(offset, new tree.CONST(1), true); //compensate for length spot in array

	tree.Exp loc = mul(new tree.CONST(currFrame.wordSize()), offset);
	return new Ex(new tree.MEM(plus(arrAddr, loc, true)));
    }

    // Exp e;
    public semant.Exp visit(ArrayLength n)
    {
	tree.Exp val = n.e.accept(this).unEx(); // first address is length of the array
	return new Ex(val);
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public semant.Exp visit(Call n)
    {
	tree.Exp caller = n.e.accept(this).unEx();
	tree.ExpList params = buildExpList(n.el, 0);
	params = new tree.ExpList(caller, params); //add implicit parameter, i.e. who called
	tree.Exp call  = new tree.CALL(new tree.NAME(new temp.Label(n.fullname)), params);
	return new Ex(call);
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

    // String s;
    public semant.Exp visit(IdentifierExp n)
    {
	VariableInfo v = null;
	if(currMethod != null)
	{
	    v = currMethod.getVar(n.s);
	    if(v != null)
		return new Ex(v.access.exp(new tree.TEMP(currFrame.FP())));

	    //wasn't local variable, must be a field
	    v = currClass.getField(n.s);
	    return new Ex(v.access.exp(currThis));
	}

	v = currClass.getField(n.s);
	return new Ex(v.access.exp(currThis));
    }

    public semant.Exp visit(This n)
    {
	return new Ex(currThis);
    }

    // Exp e;
    public semant.Exp visit(NewArray n)
    {
	tree.CONST len = (tree.CONST)n.e.accept(this).unEx();
	tree.ExpList params = new tree.ExpList(new tree.CONST(len.value + 1),
					       new tree.ExpList(new tree.CONST(currFrame.wordSize()), null));
	tree.Exp arr = currFrame.externalCall("calloc", params);
	tree.Exp mvlen = new tree.ESEQ(new tree.MOVE(new tree.MEM(arr), len), arr);
	return new Ex(mvlen);
    }

    //Identifier i;
    public semant.Exp visit(NewObject n)
    {
	int numFields = classTable.get(n.i.s).getFieldsCount();
	tree.ExpList params = new tree.ExpList(new tree.CONST(numFields),
					       new tree.ExpList(new tree.CONST(currFrame.wordSize()), null));
	tree.Exp obj = currFrame.externalCall("calloc", params);
	return new Ex(obj);
    }

    // Exp e;
    public semant.Exp visit(Not n)
    {
	tree.Exp val = n.e.accept(this).unEx();
	if(optimize)
	{
	    if(val instanceof tree.CONST)
	    {
		tree.CONST neg = (tree.CONST)val;
		if(neg.value == 0)
		    return new Ex(new tree.CONST(1));
		return new Ex(new tree.CONST(0));
	    }
	}
	return new RelCx(tree.CJUMP.EQ, val, new tree.CONST(0));
    }

    // String s;
    public semant.Exp visit(Identifier n)
    {
	VariableInfo v = null;
	if(currMethod != null)
	{
	    v = currMethod.getVar(n.s);
	    if(v != null)
		return new Ex(v.access.exp(new tree.TEMP(currFrame.FP())));

	    //wasn't local variable, must be a field
	    v = currClass.getField(n.s);
	    return new Ex(v.access.exp(currThis));
	}

	v = currClass.getField(n.s);
	tree.Exp iVal = v.access.exp(currThis);
	return new Ex(iVal);
    }


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
	if(optimize)
	{
	    if(e1 instanceof tree.CONST && e2 instanceof tree.CONST)
	    {
		tree.CONST c1 = (tree.CONST)e1;
		tree.CONST c2 = (tree.CONST)e2;
		if(!plus)
		    return new tree.CONST(c1.value - c2.value);
		return new tree.CONST(c1.value + c2.value);
	    }
	}
	if(!plus)
	    return new tree.BINOP(tree.BINOP.MINUS, e1, e2);
	return new tree.BINOP(tree.BINOP.PLUS, e1, e2);
    }

    private tree.Exp mul(tree.Exp e1, tree.Exp e2)
    {
	if(optimize)
	{
	    if(e1 instanceof tree.CONST && e2 instanceof tree.CONST)
	    {
		tree.CONST c1 = (tree.CONST)e1;
		tree.CONST c2 = (tree.CONST)e2;
		return new tree.CONST(c1.value * c2.value);
	    }
	    else if(e1 instanceof tree.CONST && ((tree.CONST)e1).value == 4)
	    {
		return new tree.BINOP(tree.BINOP.LSHIFT, e2, new tree.CONST(2));
	    }
	    else if(e2 instanceof tree.CONST && ((tree.CONST)e2).value == 4)
	    {
		return new tree.BINOP(tree.BINOP.LSHIFT, e1, new tree.CONST(2));
	    }
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
	    if(optimize)
		if(offset == 0)
		    return new tree.MEM(basePtr);

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
	    if(optimize)
	    {

	    }
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
	    temp.Label t = new temp.Label();
	    temp.Label f = new temp.Label();
	    // if(exp instanceof tree.CONST)
	    // {
	    // 	if(((tree.CONST)exp).value == 0)
	    // 	    return new tree.JUMP(f);
	    // 	return new tree.JUMP(t);
	    // }
	    // return this.unCx(t,f);
	    return this.unCx(t, f);
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
	    temp.Label t = new temp.Label();
	    temp.Label f = new temp.Label();
	    return new tree.SEQ(cond.unCx(t,f),
				new tree.SEQ(new tree.LABEL(t),
					     new tree.SEQ(a.unNx(),
							  new tree.SEQ(new tree.JUMP(tt),
								       new tree.SEQ(new tree.LABEL(f),
										    b.unNx())))));
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
	if(sl.size() <= 0)
	    return null;
	if(pos == sl.size()-1)
	    return sl.elementAt(pos).accept(this).unNx();

	tree.Stm exp = sl.elementAt(pos).accept(this).unNx();
	return new tree.SEQ(exp, buildSEQ(sl, pos+1));
    }

    public tree.ExpList buildExpList(syntaxtree.ExpList lst, int pos)
    {
	if(pos == lst.size() - 1)
	    return new tree.ExpList(lst.elementAt(pos).accept(this).unEx(), null);
	return new tree.ExpList(lst.elementAt(pos).accept(this).unEx(), buildExpList(lst, pos + 1));
    }
}

/*
 * Assignment 1: Straigt-line Program Interpreter
 * Doug Otstott
 * Joseph Rivera
 */
class interp
{
    static int maxargs(Stm s)
    {
        if(s instanceof CompoundStm)
        {
            CompoundStm cmpStm = (CompoundStm) s;
            int max1 = maxargs(cmpStm.stm1);
            int max2 = maxargs(cmpStm.stm2);
            return Math.max(max1, max2);
        }
        if(s instanceof AssignStm)
        {
            AssignStm assgn = (AssignStm) s;
            return countExpArgs(assgn.exp);
        }
        if(s instanceof PrintStm)
        {
            PrintStm prnt = (PrintStm) s;
            return countPrintArgs(prnt.exps);
        }
        return 0;
    }

    static int countPrintArgs(ExpList list)
    {
        if(list instanceof PairExpList)
        {
            PairExpList pair = (PairExpList) list;
	    int expCount = countExpArgs(pair.exp);
            int count = countPrintArgs(pair.tail);
            return Math.max(expCount, count);
        }
        return countExpArgs(((LastExpList)list).exp);
    }

    static int countExpArgs(Exp exp)
    {
        if(exp instanceof OpExp)
        {
            OpExp id = (OpExp) exp;
            return countExpArgs(id.left) + countExpArgs(id.right);
        }
        if(exp instanceof EseqExp)
        {
            EseqExp eseq = (EseqExp) exp;
            int count1 = maxargs(eseq.stm);
            int count2 = countExpArgs(eseq.exp);
            return Math.max(count1, count2);
        }
        return 0;
    }

    static void interp(Stm s)
    {
        // interpret s with respect to an empty Table
        interpStm(s, null);
    }
    static class Table
    {
        final String id;
        final int value;
        final Table tail;

        Table(String i, int v, Table t)
        {
            id = i;
            value = v;
            tail = t;
        }
    }

    // Returns the value of key in Table t.
    static int lookup(Table t, String key)
    {
        if(t == null)
            throw new Error("unknown identifier: " + key);
        else if(t.id.equals(key))
            return t.value;
        else
            return lookup(t.tail, key);
    }

    // Returns a new Table that is the same as t except that id has value val.
    static Table update(Table t, String id, int val)
    {
        return new Table(id, val, t);
    }
    static class IntAndTable
    {
        final int i;
        final Table t;

        IntAndTable(int ii, Table tt)
        {
            i = ii;
            t = tt;
        }
    }

    static Table interpStm(Stm s, Table t)
    {
        if(s instanceof CompoundStm)
        {
            CompoundStm cs = (CompoundStm) s;
            return interpStm(cs.stm2, interpStm(cs.stm1, t));
        }
        if(s instanceof AssignStm)
        {
            AssignStm assgn = (AssignStm) s;
            IntAndTable it = interpExp(assgn.exp, t);
            return update(it.t, assgn.id, it.i);
        }
        if(s instanceof PrintStm)
        {
            PrintStm prnt = (PrintStm) s;
            return interpAndPrint(prnt.exps, t);
        }
        else
            throw new Error("Bad Statement");
    }

    static Table interpAndPrint(ExpList exps, Table t)
    {
        if(exps instanceof PairExpList)
        {
            PairExpList pair = (PairExpList) exps;
            IntAndTable it = interpExp(pair.head, t);
            System.out.print(it.i + " ");
            return interpAndPrint(pair.tail, it.t);
        }
        if(exps instanceof LastExpList)
        {
            LastExpList lst = (LastExpList) exps;
            IntAndTable it = interpExp(lst.head, t);
            System.out.println(it.i);
            return it.t;
        }
        else
            throw new Error("Bad Print");
    }

    static IntAndTable interpExp(Exp e, Table t)
    {
        if(e instanceof IdExp)
        {
            IdExp id = (IdExp) e;
            return new IntAndTable(lookup(t, id.id), t);
        }
        if(e instanceof NumExp)
        {
            NumExp num = (NumExp) e;
            return new IntAndTable(num.num, t);
        }
        if(e instanceof OpExp)
        {
            OpExp op = (OpExp) e;
            IntAndTable val1 = interpExp(op.left, t);
            IntAndTable val2 = interpExp(op.right, val1.t);
            if(op.oper == op.Times)
                return new IntAndTable(val1.i * val2.i, val2.t);
            if(op.oper == op.Div)
                return new IntAndTable(val1.i / val2.i, val2.t);
            if(op.oper == op.Plus)
                return new IntAndTable(val1.i + val2.i, val2.t);
            if(op.oper == op.Minus)
                return new IntAndTable(val1.i - val2.i, val2.t);
        }
        if(e instanceof EseqExp)
        {
            EseqExp eseq = (EseqExp) e;
            Table res = interpStm(eseq.stm, t);
            IntAndTable it = interpExp(eseq.exp, res);
            return new IntAndTable(it.i, it.t);
        }
        return null;	// replace this with the actual code needed
    }

    public static void main(String args[])
    {
        System.out.println("maxargs result: " + maxargs(prog.prog));
        System.out.print("interpretation result: ");
        interp(prog.prog);

        System.out.println("maxargs result: " + maxargs(prog1.prog));
        System.out.print("interpretation result: ");
        interp(prog1.prog);

        System.out.println("maxargs result: " + maxargs(prog2.prog));
        System.out.print("interpretation result: ");
        interp(prog2.prog);

        System.out.println("maxargs result: " + maxargs(prog3.prog));
        System.out.print("interpretation result: ");
        interp(prog3.prog);

    }
}

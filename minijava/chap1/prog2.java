
// print((a := 3, (print((b := a-1, a)), b*3)), a*3, (c := a*b, c))

class prog2 {
static Stm prog =
new PrintStm(
  new PairExpList(
    new EseqExp(
      new AssignStm("a", new NumExp(3)),
      new EseqExp(
        new PrintStm(
	  new LastExpList(
	    new EseqExp(
	      new AssignStm("b", new OpExp(new IdExp("a"),
					   OpExp.Minus,
					   new NumExp(1))),
	      new IdExp("a")))),
	new OpExp(new IdExp("b"), OpExp.Times, new NumExp(3)))),
  new PairExpList(
    new OpExp(new IdExp("a"), OpExp.Times, new NumExp(3)),
  new LastExpList(
    new EseqExp(
      new AssignStm("c", new OpExp(new IdExp("a"),
				   OpExp.Times,
				   new IdExp("b"))),
      new IdExp("c"))))));
}

// Should produce the following output:
//
// maxargs result: 3
// interpretation result: 3
// 6 9 6


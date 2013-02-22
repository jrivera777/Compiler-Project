
// a := 7; print(a, (print(2*a, (a := a+1, a)), a))

class prog3 {
static Stm prog =
new CompoundStm(new AssignStm("a", new NumExp(7)),
		new PrintStm(new PairExpList(new IdExp("a"),
  new LastExpList(new EseqExp(new PrintStm(new PairExpList(
  new OpExp(new NumExp(2), OpExp.Times, new IdExp("a")),
  new LastExpList(new EseqExp(new AssignStm("a",
  new OpExp(new IdExp("a"), OpExp.Plus, new NumExp(1))),
  new IdExp("a"))))), new IdExp("a"))))));
}

// Should produce the following output:
//
// maxargs result: 2
// interpretation result: 7 14 8
// 8


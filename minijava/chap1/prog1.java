
// a := 7; b := (a := a+2, a)*(a := a-3, a); print(a, b)

class prog1 {
static Stm prog =
  new CompoundStm( new AssignStm("a", new NumExp(7)),
  new CompoundStm(new AssignStm("b",
  new OpExp(new EseqExp(new AssignStm("a",
  new OpExp(new IdExp("a"), OpExp.Plus, new NumExp(2))), new IdExp("a")),
  OpExp.Times,
  new EseqExp(new AssignStm("a",
  new OpExp(new IdExp("a"), OpExp.Minus, new NumExp(3))), new IdExp("a")))),
  new PrintStm(new PairExpList(new IdExp("a"),
  new LastExpList(new IdExp("b"))))));
}

// Should produce the following output:
//
// maxargs result: 2
// interpretation result: 6 54


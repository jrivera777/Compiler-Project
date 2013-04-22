// Chapter 9 version.

package frame;

// See pages 127, 143, 157, and 251.
public abstract class Frame implements temp.TempMap {
  abstract public temp.Temp RVCallee();
  abstract public temp.Temp RVCaller();
  abstract public temp.Temp FP();
  abstract public temp.TempList registers();
  abstract public String tempMap(temp.Temp temp);
  abstract public int wordSize();
  abstract public tree.Exp externalCall(String func, tree.ExpList args);
  abstract public Frame newFrame(temp.Label name, util.BoolList formals);
  public AccessList formals;
  public temp.Label name;
  abstract public Access allocLocal(boolean escape);
  abstract public tree.Stm procEntryExit1(tree.Stm body);
  abstract public assem.InstrList procEntryExit2(assem.InstrList body);
  abstract public Proc procEntryExit3(assem.InstrList body);
  abstract public assem.InstrList codegen(tree.Stm stm);
}

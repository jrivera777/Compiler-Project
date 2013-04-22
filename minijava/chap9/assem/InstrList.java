package assem;

public class InstrList {
  public Instr head;
  public InstrList tail;
  public InstrList(Instr h, InstrList t) {
    head=h; tail=t;
  }
}

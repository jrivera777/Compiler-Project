package semant;

public class ProcFrag extends Frag {
  public tree.Stm body;
  public frame.Frame frame;
  ProcFrag(tree.Stm b, frame.Frame f) {
    body = b; frame = f;
  }
}

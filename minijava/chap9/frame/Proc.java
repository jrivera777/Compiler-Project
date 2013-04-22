package frame;

public class Proc {			// Pages 250-252.
  public String prolog;
  public assem.InstrList body;
  public String epilog;
  public Proc(String p, assem.InstrList b, String e) {
     prolog=p; body=b; epilog=e;
  }
}

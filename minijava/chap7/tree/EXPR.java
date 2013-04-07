package tree;

public class EXPR extends Stm {
  public Exp exp; 
  public EXPR(Exp e) {exp=e;}
  public ExpList kids() {return new ExpList(exp,null);}
  public Stm build(ExpList kids) {
    return new EXPR(kids.head);
  }
}


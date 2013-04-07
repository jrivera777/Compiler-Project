package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class LessThan extends Exp {
  public Exp e1,e2;
  
  public LessThan(int p, Exp ae1, Exp ae2) {
    pos=p; e1=ae1; e2=ae2;
  }

  public void accept(Visitor v) {
    v.visit(this);
  } 
 
  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public semant.Exp accept(ExpVisitor v) {
    return v.visit(this);
  }
}

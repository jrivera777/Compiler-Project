package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class This extends Exp {
  public This(int p) {
    pos=p;
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

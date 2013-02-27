package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class False extends Exp {
  public False(int p) {
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

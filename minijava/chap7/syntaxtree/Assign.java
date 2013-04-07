package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class Assign extends Statement {
  public Identifier i;
  public Exp e;

  public Assign(int p, Identifier ai, Exp ae) {
    pos=p; i=ai; e=ae; 
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


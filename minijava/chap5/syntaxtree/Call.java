package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class Call extends Exp {
  public Exp e;
  public Identifier i;
  public ExpList el;
  
  // The full name of the method, to be filled in by the typechecker.
  // For example, method "f" in class "A" has full name "A$f".
  public String fullname;

  public Call(int p, Exp ae, Identifier ai, ExpList ael) {
    pos=p; e=ae; i=ai; el=ael;
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

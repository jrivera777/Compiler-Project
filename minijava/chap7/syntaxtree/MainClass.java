package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class MainClass {
  public int pos;
  public Identifier i1,i2;
  public Statement s;

  public MainClass(int p, Identifier ai1, Identifier ai2, Statement as) {
    pos=p; i1=ai1; i2=ai2; s=as;
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


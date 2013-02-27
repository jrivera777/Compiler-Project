package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class If extends Statement {
  public Exp e;
  public Statement s1,s2;

  public If(int p, Exp ae, Statement as1, Statement as2) {
    pos=p; e=ae; s1=as1; s2=as2;
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


package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class IdentifierType extends Type {
  public String s;

  public IdentifierType(int p, String as) {
    pos=p; s=as;
  }

  public IdentifierType(String as) {
    pos = -1 ; s=as;
  }

  public String toString(){
    return s;
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

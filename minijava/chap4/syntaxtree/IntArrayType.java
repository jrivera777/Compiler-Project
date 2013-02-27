package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class IntArrayType extends Type {
  public IntArrayType(int p) {
    pos=p;
  }
  
  // for type checking
  public IntArrayType() {
    pos =  -1;
  }

  public String toString(){
    return "int[]";
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

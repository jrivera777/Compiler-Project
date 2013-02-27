package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class ClassDeclExtends extends ClassDecl {
  public Identifier i;
  public Identifier j;
  public VarDeclList vl;  
  public MethodDeclList ml;
 
  public ClassDeclExtends(int p, Identifier ai, Identifier aj, 
                  VarDeclList avl, MethodDeclList aml) {
    pos=p; i=ai; j=aj; vl=avl; ml=aml;
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

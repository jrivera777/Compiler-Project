package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public abstract class ClassDecl {
  public int pos;
  // We use 'duplicate' to indicate whether a class with this name
  // has already been declared.  If so, we ignore this declaration.
  public boolean duplicate = false;
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
  public abstract semant.Exp accept(ExpVisitor v);
}

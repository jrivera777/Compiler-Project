package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public abstract class Statement {
  public int pos;
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
  public abstract semant.Exp accept(ExpVisitor v);
}

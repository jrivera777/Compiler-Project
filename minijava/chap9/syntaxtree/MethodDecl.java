package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import visitor.ExpVisitor;

public class MethodDecl {
  public int pos;
  // We use 'duplicate' to indicate whether a method with this name has
  // already been declared in this class.  If so, we ignore this declaration.
  public boolean duplicate = false;
  public Type t;
  public Identifier i;
  public FormalList fl;
  public VarDeclList vl;
  public StatementList sl;
  public Exp e;

  public MethodDecl(int p, Type at, Identifier ai, FormalList afl,
			VarDeclList avl, StatementList asl, Exp ae) {
    pos=p; t=at; i=ai; fl=afl; vl=avl; sl=asl; e=ae;
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

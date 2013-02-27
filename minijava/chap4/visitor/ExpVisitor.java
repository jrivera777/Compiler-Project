package visitor;

import syntaxtree.*;

public interface ExpVisitor {
  public semant.Exp visit(Program n);
  public semant.Exp visit(MainClass n);
  public semant.Exp visit(ClassDeclSimple n);
  public semant.Exp visit(ClassDeclExtends n);
  public semant.Exp visit(VarDecl n);
  public semant.Exp visit(MethodDecl n);
  public semant.Exp visit(Formal n);
  public semant.Exp visit(IntArrayType n);
  public semant.Exp visit(BooleanType n);
  public semant.Exp visit(IntegerType n);
  public semant.Exp visit(IdentifierType n);
  public semant.Exp visit(Block n);
  public semant.Exp visit(If n);
  public semant.Exp visit(While n);
  public semant.Exp visit(Print n);
  public semant.Exp visit(Assign n);
  public semant.Exp visit(ArrayAssign n);
  public semant.Exp visit(And n);
  public semant.Exp visit(LessThan n);
  public semant.Exp visit(Plus n);
  public semant.Exp visit(Minus n);
  public semant.Exp visit(Times n);
  public semant.Exp visit(ArrayLookup n);
  public semant.Exp visit(ArrayLength n);
  public semant.Exp visit(Call n);
  public semant.Exp visit(IntegerLiteral n);
  public semant.Exp visit(True n);
  public semant.Exp visit(False n);
  public semant.Exp visit(IdentifierExp n);
  public semant.Exp visit(This n);
  public semant.Exp visit(NewArray n);
  public semant.Exp visit(NewObject n);
  public semant.Exp visit(Not n);
  public semant.Exp visit(Identifier n);
}

// A generic depth-first syntax tree visitor whose visit methods
// return semant.Exp.  Can be extended for particular applications.

package visitor;

import syntaxtree.*;

public class ExpDepthFirstVisitor implements ExpVisitor {

  // MainClass m;
  // ClassDeclList cl;
  public semant.Exp visit(Program n) {
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.elementAt(i).accept(this);
    }
    return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public semant.Exp visit(MainClass n) {
    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
    return null;
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public semant.Exp visit(ClassDeclSimple n) {
    n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
    return null;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public semant.Exp visit(ClassDeclExtends n) {
    n.i.accept(this);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
    return null;
  }

  // Type t;
  // Identifier i;
  public semant.Exp visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public semant.Exp visit(MethodDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    n.e.accept(this);
    return null;
  }

  // Type t;
  // Identifier i;
  public semant.Exp visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public semant.Exp visit(IntArrayType n) {
    return null;
  }

  public semant.Exp visit(BooleanType n) {
    return null;
  }

  public semant.Exp visit(IntegerType n) {
    return null;
  }

  // String s;
  public semant.Exp visit(IdentifierType n) {
    return null;
  }

  // StatementList sl;
  public semant.Exp visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    return null;
  }

  // Exp e;
  // Statement s1,s2;
  public semant.Exp visit(If n) {
    n.e.accept(this);
    n.s1.accept(this);
    n.s2.accept(this);
    return null;
  }

  // Exp e;
  // Statement s;
  public semant.Exp visit(While n) {
    n.e.accept(this);
    n.s.accept(this);
    return null;
  }

  // Exp e;
  public semant.Exp visit(Print n) {
    n.e.accept(this);
    return null;
  }
  
  // Identifier i;
  // Exp e;
  public semant.Exp visit(Assign n) {
    n.i.accept(this);
    n.e.accept(this);
    return null;
  }

  // Identifier i;
  // Exp e1,e2;
  public semant.Exp visit(ArrayAssign n) {
    n.i.accept(this);
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public semant.Exp visit(And n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public semant.Exp visit(LessThan n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public semant.Exp visit(Plus n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public semant.Exp visit(Minus n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public semant.Exp visit(Times n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e1,e2;
  public semant.Exp visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return null;
  }

  // Exp e;
  public semant.Exp visit(ArrayLength n) {
    n.e.accept(this);
    return null;
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public semant.Exp visit(Call n) {
    n.e.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
    return null;
  }

  // int i;
  public semant.Exp visit(IntegerLiteral n) {
    return null;
  }

  public semant.Exp visit(True n) {
    return null;
  }

  public semant.Exp visit(False n) {
    return null;
  }

  // String s;
  public semant.Exp visit(IdentifierExp n) {
    return null;
  }

  public semant.Exp visit(This n) {
    return null;
  }

  // Exp e;
  public semant.Exp visit(NewArray n) {
    n.e.accept(this);
    return null;
  }

  // Identifier i;
  public semant.Exp visit(NewObject n) {
    return null;
  }

  // Exp e;
  public semant.Exp visit(Not n) {
    n.e.accept(this);
    return null;
  }

  // String s;
  public semant.Exp visit(Identifier n) {
    return null;
  }
}

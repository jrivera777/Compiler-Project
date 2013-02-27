// Pretty Printer for MiniJava Abstract Syntax
// Geoffrey Smith, October 2004

package visitor;

import syntaxtree.*;

public class PrettyPrintVisitor implements Visitor {

  int col = 0; 		// How many columns should we indent each new line?
  
  void indent(int c) {
    for (int i = 0; i < c; i++)
      System.out.print("  ");
  }

  // int pos;
  // MainClass m;
  // ClassDeclList cl;
  public void visit(Program n) {  	
    System.out.println("Program(" + n.pos + ",");
    indent(++col);
    n.m.accept(this);
    System.out.println(",");
    indent(col);
    System.out.print("ClassDeclList(");
    col++;
    for (int i = 0; i < n.cl.size(); i++ ) {        
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.cl.elementAt(i).accept(this);
    }
    col -=2;
    System.out.println("))");
  }
  
  // int pos;
  // Identifier i1,i2;
  // Statement s;
  public void visit(MainClass n) {
    System.out.print("MainClass(" + n.pos + ", ");
    n.i1.accept(this);
    System.out.print(", ");
    n.i2.accept(this);
    System.out.println(",");
    indent(++col);
    n.s.accept(this);   
    col--;
    System.out.print(")");
  }

  // int pos;
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclSimple n) {
    System.out.print("ClassDeclSimple(" + n.pos + ", ");
    n.i.accept(this);
    System.out.println(",");
    indent(++col);
    System.out.print("VarDeclList(");
    col++;
    for (int i = 0; i < n.vl.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.vl.elementAt(i).accept(this);    
    }
    System.out.println("),");
    indent(col-1);
    System.out.print("MethodDeclList(");
    for (int i = 0; i < n.ml.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.ml.elementAt(i).accept(this);    
    }
    col -= 2;
    System.out.print("))");
  }
 
  // int pos;
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public void visit(ClassDeclExtends n) {
    System.out.print("ClassDeclExtends(" + n.pos + ", ");
    n.i.accept(this);
    System.out.print(", ");
    n.j.accept(this);
    System.out.println(",");
    indent(++col);
    System.out.print("VarDeclList(");
    col++;
    for (int i = 0; i < n.vl.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.vl.elementAt(i).accept(this);    
    }
    System.out.println("),");
    indent(col-1);
    System.out.print("MethodDeclList(");
    for (int i = 0; i < n.ml.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.ml.elementAt(i).accept(this);    
    }
    col -= 2;
    System.out.print("))");
  }

  // int pos;
  // Type t;
  // Identifier i;
  public void visit(VarDecl n) {
    System.out.print("VarDecl(");
    n.t.accept(this);
    System.out.print(", ");
    n.i.accept(this);
    System.out.print(")");
  }

  // int pos;
  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public void visit(MethodDecl n) {
    System.out.print("MethodDecl(" + n.pos + ",");
    n.t.accept(this);
    System.out.print(", ");
    n.i.accept(this);
    System.out.println(",");
    indent(++col);
    System.out.print("FormalList(");
    col++;
    for (int i = 0; i < n.fl.size(); i++) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.fl.elementAt(i).accept(this);
    }
    System.out.println("),"); 
    indent(col-1);
    System.out.print("VarDeclList(");
    for (int i = 0; i < n.vl.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.vl.elementAt(i).accept(this);    
    }
    System.out.println("),");
    indent(col-1);
    System.out.print("StatementList(");
    for (int i = 0; i < n.sl.size(); i++) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.sl.elementAt(i).accept(this);
    }
    System.out.println("),");
    indent(--col);
    n.e.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Type t;
  // Identifier i;
  public void visit(Formal n) {
    System.out.print("Formal(" + n.pos + ",");
    n.t.accept(this);
    System.out.print(", ");
    n.i.accept(this);
    System.out.print(")");
  }

  // int pos;
  public void visit(IntArrayType n) {
    System.out.print("IntArrayType(" + n.pos + ")");
  }

  // int pos;
  public void visit(BooleanType n) {
    System.out.print("BooleanType(" + n.pos + ")");
  }

  // int pos;
  public void visit(IntegerType n) {
    System.out.print("IntegerType(" + n.pos + ")");
  }

  // int pos;
  // String s;
  public void visit(IdentifierType n) {
    System.out.print("IdentifierType(" + n.pos + ", " + n.s + ")");
  }

  // int pos;
  // StatementList sl;
  public void visit(Block n) {
    System.out.println("Block(" + n.pos + ",");
    indent(++col);
    System.out.print("StatementList(");
    col++;
    for (int i = 0; i < n.sl.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.sl.elementAt(i).accept(this);
    }
    col -= 2;
    System.out.print("))");
  }

  // int pos;
  // Exp e;
  // Statement s1,s2;
  public void visit(If n) {
    System.out.println("If(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    System.out.println(",");
    indent(col);
    n.s1.accept(this);
    System.out.println(",");
    indent(col);
    n.s2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e;
  // Statement s;
  public void visit(While n) {
    System.out.println("While(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    System.out.println(",");
    indent(col);
    n.s.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e;
  public void visit(Print n) {
    System.out.println("Print(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    col--;
    System.out.print(")");
  }
  
  // int pos;
  // Identifier i;
  // Exp e;
  public void visit(Assign n) {
    System.out.print("Assign(" + n.pos + ", ");
    n.i.accept(this);
    System.out.println(",");
    indent(++col);
    n.e.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Identifier i;
  // Exp e1,e2;
  public void visit(ArrayAssign n) {
    System.out.print("ArrayAssign(" + n.pos + ", ");
    n.i.accept(this);
    System.out.println(",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e1,e2;
  public void visit(And n) {
    System.out.println("And(" + n.pos + ",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e1,e2;
  public void visit(LessThan n) {
    System.out.println("LessThan(" + n.pos + ",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e1,e2;
  public void visit(Plus n) {
    System.out.println("Plus(" + n.pos + ",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e1,e2;
  public void visit(Minus n) {
    System.out.println("Minus(" + n.pos + ",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e1,e2;
  public void visit(Times n) {
    System.out.println("Times(" + n.pos + ",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e1,e2;
  public void visit(ArrayLookup n) {
    System.out.println("ArrayLookup(" + n.pos + ",");
    indent(++col);
    n.e1.accept(this);
    System.out.println(",");
    indent(col);
    n.e2.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e;
  public void visit(ArrayLength n) {
    System.out.println("ArrayLength(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Exp e;
  // Identifier i;
  // ExpList el;
  public void visit(Call n) {
    System.out.println("Call(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    System.out.println(",");
    indent(col);
    n.i.accept(this);
    System.out.println(",");
    indent(col);
    System.out.print("ExpList(");
    col++;
    for (int i = 0; i < n.el.size(); i++ ) {
      System.out.println((i==0 ? "" : ","));
      indent(col);
      n.el.elementAt(i).accept(this);
    }    
    col -= 2;
    System.out.print("))");
  }

  // int pos;
  // int i;
  public void visit(IntegerLiteral n) {
    System.out.print("IntegerLiteral(" + n.pos + ", " + n.i + ")");
  }

  // int pos;
  public void visit(True n) {
    System.out.print("True(" + n.pos + ")");
  }

  // int pos;
  public void visit(False n) {
    System.out.print("False(" + n.pos + ")");
  }

  // int pos;
  // String s;
  public void visit(IdentifierExp n) {
    System.out.print("IdentifierExp(" + n.pos + ", " + n.s + ")");
  }

  // int pos;
  public void visit(This n) {
    System.out.print("This(" + n.pos + ")");
  }

  // int pos;
  // Exp e;
  public void visit(NewArray n) {
    System.out.println("NewArray(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    col--;
    System.out.print(")");
  }

  // int pos;
  // Identifier i;
  public void visit(NewObject n) {
    System.out.print("NewObject(" + n.pos + ", ");  
    n.i.accept(this);
    System.out.print(")");
  }

  // int pos;
  // Exp e;
  public void visit(Not n) {
    System.out.println("Not(" + n.pos + ",");
    indent(++col);
    n.e.accept(this);
    col--;
    System.out.print(")");
  }

  // String s;
  public void visit(Identifier n) {
    System.out.print("Identifier(" + n.s + ")");
  }
}


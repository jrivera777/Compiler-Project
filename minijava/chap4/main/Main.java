package main;

public class Main {

  public static void main(String argv[]) {
    String sourcefile = argv[0];

    // Make the error messager printer.
    errormsg.ErrorMsg errorMsg = new errormsg.ErrorMsg(sourcefile);

    // Parse the MiniJava source in sourcefile and build abstract syntax.
    parse.Parse parser = new parse.Parse(sourcefile, errorMsg);

    // Get the abstract syntax tree.
    syntaxtree.Program prog = parser.absyn;

    // Print out the abstract syntax tree.
    System.out.println("Abstract syntax for " + sourcefile + ":");
    System.out.println();
    prog.accept(new visitor.PrettyPrintVisitor());
    System.out.println();
  }

}

package main;

public class Main {

  public static void main(String argv[]) {
    boolean verbose = false;    // Are we in verbose mode?
    boolean optimize = true;	// Do we want to optimize?

    // Allow two command-line arguments: -v (verbose) and -n (no optimization)
    int i;
    for (i = 0; i < argv.length && argv[i].charAt(0) == '-'; i++)
      switch (argv[i].charAt(1)) {
	case 'v':
	  verbose = true; break;
	case 'n':
	  optimize = false; break;
	default:
	  System.out.println("Illegal command-line argument: " + argv[i]);
      }
    if (i == argv.length) {
      System.out.println("Usage: mjc -v -n file.java");
      System.exit(1);
    }
    String sourcefile = argv[i];

    // Make the error messager printer.
    errormsg.ErrorMsg errorMsg = new errormsg.ErrorMsg(sourcefile);

    // Parse the MiniJava source in sourcefile and build abstract syntax.
    parse.Parse parser = new parse.Parse(sourcefile, errorMsg);

    // Get the abstract syntax tree.
    syntaxtree.Program prog = parser.absyn;

    // Print out the abstract syntax tree.
    if (verbose) {
      System.out.println("Abstract syntax for " + sourcefile + ":");
      prog.accept(new visitor.PrettyPrintVisitor());
      System.out.println();
    }

    // First phase of type checking: build the symbol table.
    semant.BuildSymbolTableVisitor builder =
			new semant.BuildSymbolTableVisitor(errorMsg);
    prog.accept(builder);
    semant.SymbolTable symbolTable = builder.getSymbolTable();

    // Second phase of type checking.
    prog.accept(new semant.TypeCheckVisitor(errorMsg, symbolTable));

    // Print out the number of errors found.
    if (errorMsg.errorsCount > 0) {
      System.out.println(errorMsg.errorsCount +
		(errorMsg.errorsCount > 1 ? " errors" : " error"));
      System.exit(1);
    }

    // Generate intermediate code.
    frame.Frame frame = new sparc.SparcFrame();
    semant.TranslatorVisitor translator =
		new semant.TranslatorVisitor(symbolTable, frame, optimize);
    prog.accept(translator);

    // Create intermediatePrinter to print the intermediate code.
    temp.TempMap tm = new temp.CombineMap(frame, new temp.DefaultMap());
    tree.Print intermediatePrinter = new tree.Print(System.out, tm);

    // Print out the intermediate code for each function.
    semant.Frag frags = translator.getResult();
    for (semant.Frag f = frags; f != null; f = f.next)
      if (f instanceof semant.ProcFrag) {
        semant.ProcFrag pf = (semant.ProcFrag) f;
        System.out.println("\nIntermediate code for " + pf.frame.name + ":");
	intermediatePrinter.prStm(pf.body);
      }
  }

}

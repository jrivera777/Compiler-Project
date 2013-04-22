package main;

public class Main {

  // This code really doesn't belong here!
  static assem.InstrList codegen(frame.Frame f, tree.StmList stms) {
    assem.InstrList first = null, last = null;
    for(tree.StmList s = stms; s != null; s = s.tail) {
      assem.InstrList i = f.codegen(s.head);
      if (last == null)
        first = last = i;
      else {
        while (last.tail != null)
          last = last.tail;
        last.tail = i;
      }
    }
    return first;
  }

  public static void main(String argv[]) throws java.io.IOException {
    boolean verbose = false;		// Are we in verbose mode?
    boolean optimize = true;		// Should we optimize?
    boolean all_registers = false;	// Use all callee-save registers?

    // Allow three optional command-line arguments: -v (verbose),
    // -n (no optimization), and -r (use all callee-save registers).
    int i;
    for (i = 0; i < argv.length && argv[i].charAt(0) == '-'; i++)
      switch (argv[i].charAt(1)) {
	case 'v':
	  verbose = true; break;
	case 'n':
	  optimize = false; break;
	case 'r':
	  all_registers = true; break;
	default:
	  System.out.println("Illegal command-line argument: " + argv[i]);
      }
    if (i == argv.length) {
      System.out.println("Usage: mjc -v -n -r file.java");
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
    frame.Frame frame = new sparc.SparcFrame(all_registers);
    semant.TranslatorVisitor translator =
		new semant.TranslatorVisitor(symbolTable, frame, optimize);
    prog.accept(translator);

    // Create the object file.
    String objectfile;
    if (sourcefile.endsWith(".java"))
      objectfile = sourcefile.substring(0, sourcefile.length()-5) + ".s";
    else
      objectfile = sourcefile + ".s";
    java.io.PrintStream out =
      new java.io.PrintStream(new java.io.FileOutputStream(objectfile));

    // This Sparc-dependent code really doesn't belong here...
    out.println("\t.global main\n\n\t.text");

    // Create intermediatePrinter to print the intermediate code.
    temp.TempMap tm = new temp.CombineMap(frame, new temp.DefaultMap());
    tree.Print intermediatePrinter = new tree.Print(System.out, tm);

    // Canonicalize the intermediate code and generate object code.
    semant.Frag frags = translator.getResult();
    for (semant.Frag f = frags; f != null; f = f.next)
      if (f instanceof semant.ProcFrag) {
	semant.ProcFrag pf = (semant.ProcFrag) f;
	if (verbose) {
	  System.out.println("\nIntermediate code for " + pf.frame.name + ":");
	  intermediatePrinter.prStm(pf.body);
	}
	// Canonicalize the intermediate code:
	tree.StmList canonical = canon.Canon.linearize(pf.body);
	if (verbose) {
	  System.out.println("\nAfter canonicalization:");
	  for (tree.StmList sl = canonical; sl != null; sl = sl.tail)
	    intermediatePrinter.prStm(sl.head);
	}
	// Trace schedule the intermediate code:
	canon.BasicBlocks blocks = new canon.BasicBlocks(canonical);
	tree.StmList scheduled = (new canon.TraceSchedule(blocks)).stms;
	if (verbose) {
	  System.out.println("\nAfter trace scheduling:");
	  for (tree.StmList sl = scheduled; sl != null; sl = sl.tail)
	    intermediatePrinter.prStm(sl.head);
	}
	// Now do code generation.
	assem.InstrList instrs = codegen(pf.frame, scheduled);
	if (verbose) {
	  System.out.println("\nBefore register allocation:");
	  for (assem.InstrList is = instrs; is != null; is = is.tail)
	    System.out.print(is.head.format(tm));
	  System.out.println();
	}
	// Do trivial register allocation:
	// [Note the cast that is unfortunately required...]
	temp.TempMap tempmap =
	    new sparc.TrivAlloc((sparc.SparcFrame) pf.frame, instrs,
								all_registers);
	frame.Proc proc = pf.frame.procEntryExit3(instrs);
	out.print(proc.prolog);
	for (assem.InstrList is = proc.body; is != null; is = is.tail)
	  out.print(is.head.format(tempmap));
	out.print(proc.epilog);
      }
      out.close();
  }

}

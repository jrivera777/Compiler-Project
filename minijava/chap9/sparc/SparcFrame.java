// Full version.

package sparc;

class InFrame extends frame.Access {
  int offset;

  InFrame(int o) {offset=o;}

  // Here the base pointer will be the frame pointer.
  public tree.Exp exp(tree.Exp basePtr) {
    return new tree.MEM(
	     new tree.BINOP(tree.BINOP.PLUS,
			    basePtr,
			    new tree.CONST(offset)));
  }
}

class InReg extends frame.Access {
  temp.Temp temp;

  InReg(temp.Temp t) {temp=t;}

  public tree.Exp exp(tree.Exp basePtr) {
    return new tree.TEMP(temp);
  }
}

public class SparcFrame extends frame.Frame {

  boolean all_registers;	// Should we use all callee-save registers?

  public SparcFrame(boolean all_regs) {
    all_registers = all_regs;
  }

  // Sparc registers
  static final temp.Temp g0 = new temp.Temp();  // zero register
  static final temp.Temp g1 = new temp.Temp();
  static final temp.Temp g2 = new temp.Temp();
  static final temp.Temp g3 = new temp.Temp();
  static final temp.Temp g4 = new temp.Temp();
  static final temp.Temp g5 = new temp.Temp();
  static final temp.Temp g6 = new temp.Temp();
  static final temp.Temp g7 = new temp.Temp();
  static final temp.Temp o0 = new temp.Temp();
  static final temp.Temp o1 = new temp.Temp();
  static final temp.Temp o2 = new temp.Temp();
  static final temp.Temp o3 = new temp.Temp();
  static final temp.Temp o4 = new temp.Temp();
  static final temp.Temp o5 = new temp.Temp();
  static final temp.Temp o6 = new temp.Temp();  // stack pointer
  static final temp.Temp o7 = new temp.Temp();
  static final temp.Temp l0 = new temp.Temp();
  static final temp.Temp l1 = new temp.Temp();
  static final temp.Temp l2 = new temp.Temp();
  static final temp.Temp l3 = new temp.Temp();
  static final temp.Temp l4 = new temp.Temp();
  static final temp.Temp l5 = new temp.Temp();
  static final temp.Temp l6 = new temp.Temp();
  static final temp.Temp l7 = new temp.Temp();
  static final temp.Temp i0 = new temp.Temp();
  static final temp.Temp i1 = new temp.Temp();
  static final temp.Temp i2 = new temp.Temp();
  static final temp.Temp i3 = new temp.Temp();
  static final temp.Temp i4 = new temp.Temp();
  static final temp.Temp i5 = new temp.Temp();
  static final temp.Temp i6 = new temp.Temp();  // frame pointer
  static final temp.Temp i7 = new temp.Temp();  // return address

  // Global registers
  static final temp.Temp [] globals = {g0, g1, g2, g3, g4, g5, g6, g7};
  // Outgoing argument registers
  static final temp.Temp [] outgoingArgs = {o0, o1, o2, o3, o4, o5};
  // Local registers
  static final temp.Temp [] locals = {l0, l1, l2, l3, l4, l5, l6, l7};
  // Incoming argument registers
  static final temp.Temp [] incomingArgs = {i0, i1, i2, i3, i4, i5};

  // Return value
  public temp.Temp RVCallee() {return i0;}
  public temp.Temp RVCaller() {return o0;}

  // Frame pointer
  public temp.Temp FP() {return i6;}

  // Not yet implemented...
  public temp.TempList registers() {
    return null;
  }

  // Define the "color" of precolored temps (pp. 198, 251).
  static final java.util.Hashtable tempcolors = new java.util.Hashtable();
  static {			// a static initializer
    for (int i = 0; i < 8; i++) {
      tempcolors.put(globals[i], "%g" + i);
      tempcolors.put(locals[i], "%l" + i);
    }
    for (int i = 0; i < 6; i++) {
      tempcolors.put(outgoingArgs[i], "%o" + i);
      tempcolors.put(incomingArgs[i], "%i" + i);
    }
    tempcolors.put(i6, "%fp");
    tempcolors.put(i7, "%i7");
  }
    
  public String tempMap(temp.Temp temp) {
    return (String) tempcolors.get(temp);
  }

  public int wordSize() {return 4;}

  public tree.Exp externalCall(String s, tree.ExpList args) {
    return new tree.CALL(new tree.NAME(new temp.Label(s)), args);
  }

  public frame.Frame newFrame(temp.Label name, util.BoolList formals) {
    sparc.SparcFrame frame = new SparcFrame(all_registers);
    frame.formals = frame.argsAccesses(0, formals);
    frame.name = name;
    return (frame.Frame) frame;
  }

  // TrivAlloc wants to know how many of %i0 - %i5 are available to it.
  int argsCount = 0;

  // mover holds the code needed to move register arguments from the
  // registers where they arrive (i.e. %i0 through %i5) to the place where
  // they will be accessed by the function body.  (Since MiniJava doesn't
  // have escaping variables, mover will typically be null.)
  tree.Stm mover = null;

  // Returns a Frame.Access for each argument of the function, taking note
  // of whether the argument escapes or not.  As a side effect, updates
  // mover.  See pp. 126-130, 155-158, 251.
  frame.AccessList argsAccesses(int i, util.BoolList formals) {
    // i is the number of the argument currently being processed.
    if (formals == null) {
      argsCount = i;
      return null;
    }
    else if (i >= 6)
      // The argument will arrive on the stack.
      return new frame.AccessList(new InFrame(68+4*i),
                                  argsAccesses(i+1, formals.tail));
    else if (formals.head) {
      // The argument escapes, and hence needs to be accessed from the stack.
      // But it initially arrives in a register, and so we have to generate
      // code to move it onto the stack.
      tree.Exp src = new tree.TEMP(incomingArgs[i]);
      frame.Access arg = new InFrame(68+4*i);
      if (mover == null)
	mover = new tree.MOVE(arg.exp(new tree.TEMP(FP())), src);
      else
        mover = new tree.SEQ(mover,
			     new tree.MOVE(arg.exp(new tree.TEMP(FP())), src));
      return new frame.AccessList(arg, argsAccesses(i+1, formals.tail));
    } else
      // The argument does not escape, and can be gotten from a register.
      // Since the SPARC's register windows prevent the kind of interference
      // described by Appel on page 129, I allow the argument to be accessed
      // from the register in which it arrives.
      // [But note that if we had a good register allocator, it might be
      // better to move the argument to a fresh register anyway, since
      // this would enable the register allocator to optimize the use
      // of the registers.  For instance, if the argument arriving in an
      // input register is not used much, it might be better to move it to
      // the stack, thereby freeing up the input register for other uses.]
      return new frame.AccessList(new InReg(incomingArgs[i]),
                                  argsAccesses(i+1, formals.tail));
  }

  // How many locals have been allocated on the stack in this frame?
  int localsCount = 0;

  public frame.Access allocLocal(boolean escape) {
    if (escape)
      return new InFrame(-wordSize()*(++localsCount));
    else
      return new InReg(new temp.Temp());
  }

  // Callee-save registers that can be used if they are saved and restored.
  static final temp.Temp [] calleeSaves = {g2, g3, g4, g5, g6, g7, i7};

  public tree.Stm procEntryExit1(tree.Stm body) {	// pp. 156-157, 251
    if (mover != null)
      body = new tree.SEQ(mover, body);
    if (all_registers)
      // Generate code to save and restore the additional
      // callee-save registers: %g2 -- %g7 and %i7.
      for (int i = 0; i < calleeSaves.length; i++) {
	tree.Exp register = new tree.TEMP(calleeSaves[i]);
	tree.Exp location = allocLocal(true).exp(new tree.TEMP(FP()));
	body = new tree.SEQ(new tree.MOVE(location, register),
		 new tree.SEQ(body, new tree.MOVE(register, location)));
      }
    return body;
  }

  // Not yet implemented...
  public assem.InstrList procEntryExit2(assem.InstrList body) {
    return body;
  }

  public frame.Proc procEntryExit3(assem.InstrList body) {	// pp. 251-252
    // A quick hacked version.  If the function calls any functions with
    // more than 6 arguments, we need to allocate a bigger stack frame...
    int spIncr = (-64-28-wordSize()*localsCount) & -8;
    String prolog = name + ":\n" + "\tsave\t%sp, " + spIncr + ", %sp\n";
    String epilog = "\tret\n" +
                    "\trestore\n\n";
    return new frame.Proc(prolog, body, epilog);
  }

  public assem.InstrList codegen(tree.Stm stm) {		// page 196 
    return (new Codegen(this)).codegen(stm);
  }

}

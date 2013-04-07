// Chapter 7 version.

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

  // Incoming argument registers
  static final temp.Temp [] incomingArgs = new temp.Temp[6];

  // A static initializer
  static {
    for (int i = 0; i < 6; i++) {
      incomingArgs[i] = new temp.Temp();
    }
  }

  // Return value
  public temp.Temp RVCallee() {return incomingArgs[0];}

  // Frame pointer
  static final temp.Temp fp = new temp.Temp();
  public temp.Temp FP() {return fp;}

  // Define the "color" of precolored temps (pp. 198, 251).
  public String tempMap(temp.Temp temp) {
    // This code could be made more efficient...
    for (int i = 0; i < 6; i++)
      if (temp == incomingArgs[i]) return "%i" + i;
    if (temp == fp) return "%fp";
    return null;
  }

  public int wordSize() {return 4;}

  public tree.Exp externalCall(String s, tree.ExpList args) {
    return new tree.CALL(new tree.NAME(new temp.Label(s)), args);
  }

  public frame.Frame newFrame(temp.Label name, util.BoolList formals) {
    sparc.SparcFrame frame = new SparcFrame();
    frame.formals = frame.argsAccesses(0, formals);
    frame.name = name;
    return (frame.Frame) frame;
  }

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
    if (formals == null)
      return null;
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

  public tree.Stm procEntryExit1(tree.Stm body) {	// pp. 156-157, 251
    // If we generated code here to save and restore callee-save registers,
    // we could make use of 7 more registers: %g2 -- %g7 and %i7.
    if (mover == null)
      return body;
    else
      return new tree.SEQ(mover, body);
  }

}

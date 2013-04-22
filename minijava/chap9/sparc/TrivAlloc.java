// A trivial register allocator that assigns a different hardware
// register to every Temp.

package sparc;

public class TrivAlloc implements temp.TempMap {

  SparcFrame frame;

  String [] colors = {"%l0", "%l1", "%l2", "%l3", "%l4", "%l5", "%l6", "%l7",
		      "%i5", "%i4", "%i3", "%i2", "%i1", "%i0"};

  String [] allcolors = {"%l0", "%l1", "%l2", "%l3", "%l4", "%l5", "%l6", "%l7",
			 "%g2", "%g3", "%g4", "%g5", "%g6", "%g7", "%i7",
			 "%i5", "%i4", "%i3", "%i2", "%i1", "%i0"};

  java.util.Dictionary tempTab = new java.util.Hashtable();

  public TrivAlloc(SparcFrame f, assem.InstrList is, boolean all_registers) {
    frame = f;

    // Always available are %l0 -- %l7.
    // Also available are any unused registers from %i0 -- %i5.
    int maxcolors = Math.max(14 - frame.argsCount, 8);

    if (all_registers) {	 // Use all the callee-save registers.
      colors = allcolors;
      maxcolors = Math.max(21 - frame.argsCount, 15);
    }

    int tempcnt = 0;		 // How many temps need to be colored?

    for ( ; is != null; is = is.tail) {
      temp.TempList temps = append(is.head.use(), is.head.def());
      for ( ; temps != null; temps = temps.tail) {
	temp.Temp temp = temps.head;
	if (frame.tempMap(temp) == null && tempTab.get(temp) == null) {
	  // temp needs to be colored.
	  if (tempcnt == maxcolors)
	    System.out.println("Ran out of registers for " + frame.name + "!");
	  else
	    tempTab.put(temp, colors[tempcnt++]);
	}
      }
    }
  }

  static temp.TempList append(temp.TempList t1, temp.TempList t2) {
    if (t1 == null)
      return t2;
    else
      return new temp.TempList(t1.head, append(t1.tail, t2));
  }

  public String tempMap(temp.Temp t) {
    String s;
    
    // Is t a precolored temp like %fp?
    if ((s = frame.tempMap(t)) != null)
      return s;
    // Was t colored here?
    if ((s = (String) tempTab.get(t)) != null)
      return s;
    // Else we must have run out of colors.
    return t.toString();
  }

}

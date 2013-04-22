// Error Message Printer
//
// Provides methods
//   public void error(String msg)
//   public void error(int pos, String msg)
// and a public int field, errorsCount, that records how many
// times error() has ever been called.

package errormsg;

import java.util.Vector;

public class ErrorMsg {

  public int errorsCount;

  private String filename;
  private Vector<String> sourceLines;		// uses Java's generics

  public ErrorMsg(String fn) {
    errorsCount = 0;
    filename = fn;

    java.io.BufferedReader sourceReader;
    try {
      sourceReader = new java.io.BufferedReader(new java.io.FileReader(fn));
    } catch (java.io.FileNotFoundException e) {
      throw new Error("File not found: " + fn);
    }
    // Now we read the source file into sourceLines.
    // But we can't just use sourceReader.readLine(), because that strips
    // off the line terminator \n (which is preceded by \r in Windows files).
    // Instead we read the source file character by character, keeping the
    // line terminators but converting them to spaces.
    sourceLines = new Vector<String>();		// uses Java's generics
    try {
      StringBuffer chars = new StringBuffer();
      while (true) {
	// Get the next input line.
	chars.setLength(0);
	while (true) {
	  // Get the next character of the line.
	  int next = sourceReader.read();
	  if (next == -1)
	    break;
	  if (next == '\r')
	    chars.append(' ');
	  else if (next == '\n') {
	    chars.append(' ');
	    break;
	  }
	  else
	    chars.append((char) next);
	}
	if (chars.length() == 0)
	  break;
	sourceLines.addElement(chars.toString());
      }
      // To make the position of EOF valid, extend the last line with a space.
      sourceLines.setElementAt(sourceLines.lastElement() + " ",
			       sourceLines.size() - 1);
    } catch (java.io.IOException e) {
      throw new Error("Error reading file " + fn);
    } 
  }

  public void error(String msg) {
    errorsCount++;
    System.out.println(filename + ":?.?: " + msg);
  }

  public void error(int pos, String msg) {
    errorsCount++;
    int line;
    String currentLine = null;
    if (pos < 0) {		// pos is invalid
      System.out.println(filename + ":?.?: " + msg);
      return;
    }
    for (line = 0; line < sourceLines.size(); line++) {
      currentLine = sourceLines.elementAt(line);   // now no cast to String!
      if (pos < currentLine.length())
	break;
      else
	pos -= currentLine.length();
    }
    if (line == sourceLines.size())	// pos is invalid
      System.out.println(filename + ":?.?: " + msg);
    else {
      System.out.println(filename + ":" +
				(line+1) + "." + (pos+1) + ": " + msg);
      System.out.println(currentLine);
      for (int i = 0; i < pos; i++)
	if (currentLine.charAt(i) != '\t')
	  System.out.print(" ");
	else
	  System.out.print("\t");
      System.out.println("^");
    }
  }
}


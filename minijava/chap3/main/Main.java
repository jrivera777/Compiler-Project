package main;

public class Main {

  public static void main(String argv[]) {
    String sourcefile = argv[0];

    // Make the error messager printer.
    errormsg.ErrorMsg errorMsg = new errormsg.ErrorMsg(sourcefile);

    // Parse the MiniJava source in sourcefile.
    new parse.Parse(sourcefile, errorMsg);
  }

}

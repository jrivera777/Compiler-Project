// Test scaffold for the MiniJava lexical analyzer.

package parse;


public class Main {

  public static void main(String argv[]) throws java.lang.Exception {
    String sourcefile = argv[0];
    errormsg.ErrorMsg errorMsg = new errormsg.ErrorMsg(sourcefile);
    java.io.InputStream inp = new java.io.FileInputStream(sourcefile);
    java_cup.runtime.Scanner scanner = new Yylex(inp,errorMsg);
    java_cup.runtime.Symbol tok;

    do { 
      tok = scanner.next_token();
      System.out.println(symnames[tok.sym] +
	   	" (" + tok.left + "," + tok.right + ") " + tok.value);
    } while (tok.sym != sym.EOF);

    inp.close();
  }

  static String symnames[] = new String[100];
  static {
     symnames[sym.LPAREN] = "LPAREN";
     symnames[sym.INT] = "INT";
     symnames[sym.PRINTLN] = "PRINTLN";
     symnames[sym.MINUS] = "MINUS";
     symnames[sym.STATIC] = "STATIC";
     symnames[sym.RPAREN] = "RPAREN";
     symnames[sym.SEMICOLON] = "SEMICOLON";
     symnames[sym.AND] = "AND";
     symnames[sym.COMMA] = "COMMA";
     symnames[sym.LT] = "LT"; 
     symnames[sym.CLASS] = "CLASS";
     symnames[sym.PLUS] = "PLUS";
     symnames[sym.EXCLAMATION] = "EXCLAMATION";
     symnames[sym.ASSIGN] = "ASSIGN";
     symnames[sym.MAIN] = "MAIN";
     symnames[sym.IF] = "IF";
     symnames[sym.THIS] = "THIS";
     symnames[sym.DOT] = "DOT";
     symnames[sym.ID] = "ID";
     symnames[sym.EOF] = "EOF";
     symnames[sym.BOOLEAN] = "BOOLEAN";
     symnames[sym.RETURN] = "RETURN";
     symnames[sym.TRUE] = "TRUE";
     symnames[sym.NEW] = "NEW";
     symnames[sym.error] = "error";
     symnames[sym.VOID] = "VOID";
     symnames[sym.LBRACK] = "LBRACK";
     symnames[sym.TIMES] = "TIMES";
     symnames[sym.LBRACE] = "LBRACE";
     symnames[sym.ELSE] = "ELSE";
     symnames[sym.RBRACK] = "RBRACK";
     symnames[sym.WHILE] = "WHILE";
     symnames[sym.PUBLIC]= "PUBLIC";
     symnames[sym.RBRACE] = "RBRACE";
     symnames[sym.STRING] = "STRING";
     symnames[sym.FALSE] = "FALSE";
     symnames[sym.LENGTH] = "LENGTH";
     symnames[sym.INTEGER_LITERAL] = "INTEGER_LITERAL";
   }
}


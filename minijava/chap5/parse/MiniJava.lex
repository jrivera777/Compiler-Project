//MiniJava Lexical Analyzer
// Douglas Otstott
// Joseph Rivera

package parse;

%%

%implements java_cup.runtime.Scanner
%function next_token
%type java_cup.runtime.Symbol
%char



%state COMMENT, MULTICOMMENT
%{
private errormsg.ErrorMsg errorMsg;
private int lastChar = -1;

private java_cup.runtime.Symbol tok(int kind, Object value) {
  return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

Yylex(java.io.InputStream s, errormsg.ErrorMsg e) {
  this(s);
  errorMsg=e;
}

%}

%eofval{
{
    if(lastChar >= 0)
    {
	errorMsg.error(lastChar, "unclosed comment"  + yytext());
    }
    return tok(sym.EOF, null);
}
%eofval}

%%

<YYINITIAL> class		{return tok(sym.CLASS, null);}
<YYINITIAL> public              {return tok(sym.PUBLIC, null);}
<YYINITIAL> static              {return tok(sym.STATIC, null);}
<YYINITIAL> void                {return tok(sym.VOID, null);}
<YYINITIAL> main                {return tok(sym.MAIN, null);}
<YYINITIAL> String              {return tok(sym.STRING, null);}
<YYINITIAL> int                 {return tok(sym.INT, null);}
<YYINITIAL> boolean             {return tok(sym.BOOLEAN, null);}
<YYINITIAL> if                  {return tok(sym.IF, null);}
<YYINITIAL> else                {return tok(sym.ELSE, null);}
<YYINITIAL> while               {return tok(sym.WHILE, null);}
<YYINITIAL> System\.out\.println  {return tok(sym.PRINTLN, null);}
<YYINITIAL> length              {return tok(sym.LENGTH, null);}
<YYINITIAL> true                {return tok(sym.TRUE, null);}
<YYINITIAL> false               {return tok(sym.FALSE, null);}
<YYINITIAL> this                {return tok(sym.THIS, null);}
<YYINITIAL> return              {return tok(sym.RETURN, null);}
<YYINITIAL> new                 {return tok(sym.NEW, null);}
<YYINITIAL> "{"                 {return tok(sym.LBRACE, null);}
<YYINITIAL> "}"                 {return tok(sym.RBRACE, null);}
<YYINITIAL> "("                 {return tok(sym.LPAREN, null);}
<YYINITIAL> ")"                 {return tok(sym.RPAREN, null);}
<YYINITIAL> "["                 {return tok(sym.LBRACK, null);}
<YYINITIAL> "]"                 {return tok(sym.RBRACK, null);}
<YYINITIAL> "."                 {return tok(sym.DOT, null);}
<YYINITIAL> "*"                 {return tok(sym.TIMES, null);}
<YYINITIAL> "+"                 {return tok(sym.PLUS, null);}
<YYINITIAL> "-"                 {return tok(sym.MINUS, null);}
<YYINITIAL> "!"                 {return tok(sym.EXCLAMATION, null);}
<YYINITIAL> "="                 {return tok(sym.ASSIGN, null);}
<YYINITIAL> "&&"                {return tok(sym.AND, null);}
<YYINITIAL> "<"                 {return tok(sym.LT, null);}
<YYINITIAL> ","                 {return tok(sym.COMMA, null);}
<YYINITIAL> ";"                 {return tok(sym.SEMICOLON, null);}
<YYINITIAL>  "/*"               {lastChar = yychar; yybegin(MULTICOMMENT);}
<YYINITIAL>  "//"               {yybegin(COMMENT);}
<YYINITIAL> [a-zA-Z][a-zA-Z0-9_]*  {return tok(sym.ID, yytext());}
<YYINITIAL> [0-9]+              {return tok(sym.INTEGER_LITERAL, Integer.parseInt(yytext()));}
<YYINITIAL> [\ \t\n]+		{ }
<YYINITIAL> .			{errorMsg.error(yychar,
					"unmatched input: " + yytext());}

<MULTICOMMENT> "*/"            {lastChar = -1; yybegin(YYINITIAL);}
<MULTICOMMENT> \n              { }
<MULTICOMMENT> .               { }

<COMMENT> \n                   {yybegin(YYINITIAL);}
<COMMENT> .                    { }

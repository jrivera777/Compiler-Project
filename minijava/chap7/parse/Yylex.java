//MiniJava Lexical Analyzer
// Douglas Otstott
// Joseph Rivera
package parse;


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

private errormsg.ErrorMsg errorMsg;
private int lastChar = -1;
private java_cup.runtime.Symbol tok(int kind, Object value) {
  return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}
Yylex(java.io.InputStream s, errormsg.ErrorMsg e) {
  this(s);
  errorMsg=e;
}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int MULTICOMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		72,
		74
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NOT_ACCEPT,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NOT_ACCEPT,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NOT_ACCEPT,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NOT_ACCEPT,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"44:9,45,43,44:2,0,44:18,45,33,44:4,35,44,26,27,30,31,37,32,23,39,42:10,44,3" +
"8,36,34,44:3,40:18,15,40:7,28,44,29,44,41,44,3,7,1,12,18,19,17,21,8,40:2,2," +
"13,14,11,5,40,16,4,9,6,10,20,40,22,40,24,44,25,44:2,46:2")[0];

	private int yy_rmap[] = unpackFromString(1,129,
"0,1,2:12,3,2:3,4,5,2,6,2:3,6:16,2:6,7,8,9,10,11,12,2,13,14,15,16,17,18,19,2" +
"0,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,4" +
"5,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,7" +
"0,71,72,73,74,75,76,77,78,79,6,80,81,82,83,84,85,86")[0];

	private int yy_nxt[][] = unpackFromString(87,47,
"-1,1,119,121,122,123,121,124,48,97,98,121:2,99,81,125,126,121,100,127,128,1" +
"21:2,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,49,121,53,18,19,53,19,20,-1,12" +
"1,101,121:20,-1:17,121:3,-1:86,22,-1:53,18,-1:47,19,-1,19,-1:2,121:22,-1:17" +
",121:3,-1:15,51,-1:36,121:13,52,121:4,21,121:3,-1:17,121:3,-1:34,23,-1:8,24" +
",-1:46,46,-1:13,54,-1:41,121:8,25,121:13,-1:17,121:3,-1:13,56,-1:38,121:19," +
"26,121:2,-1:17,121:3,-1:27,58,-1:24,121:17,27,121:4,-1:17,121:3,-1:9,60,-1:" +
"42,121:3,28,121:18,-1:17,121:3,-1:20,62,-1:31,121:11,29,121:10,-1:17,121:3," +
"-1:12,64,-1:39,121:13,30,121:8,-1:17,121:3,-1:18,66,-1:33,121:17,31,121:4,-" +
"1:17,121:3,-1:13,68,-1:38,121:3,32,121:18,-1:17,121:3,-1:6,70,-1:45,121:17," +
"33,121:4,-1:17,121:3,-1:18,41,-1:33,121:17,34,121:4,-1:17,121:3,-1:5,42:42," +
"43,42:2,20,-1,121:20,35,121,-1:17,121:3,-1:5,44:29,50,44:12,45,44:2,20,-1,3" +
"6,121:21,-1:17,121:3,-1:5,37,121:21,-1:17,121:3,-1:5,121:16,38,121:5,-1:17," +
"121:3,-1:5,121:13,39,121:8,-1:17,121:3,-1:5,121:13,40,121:8,-1:17,121:3,-1:" +
"5,121:22,47,-1:16,121:3,-1:5,121:17,55,121:4,-1:17,121:3,-1:5,121:5,57,121:" +
"16,-1:17,121:3,-1:5,121:7,59,121:14,-1:17,121:3,-1:5,121:7,61,121:14,-1:17," +
"121:3,-1:5,121:7,63,121:14,-1:17,121:3,-1:5,121:3,65,121:18,-1:17,121:3,-1:" +
"5,121:3,67,121:18,-1:17,121:3,-1:5,121:3,69,121:18,-1:17,121:3,-1:5,121,71," +
"121:20,-1:17,121:3,-1:5,121:8,73,121:13,-1:17,121:3,-1:5,121:7,75,121:14,-1" +
":17,121:3,-1:5,121:7,76,121:14,-1:17,121:3,-1:5,121:13,77,121:8,-1:17,121:3" +
",-1:5,121:15,78,121:6,-1:17,121:3,-1:5,121:2,79,121:19,-1:17,121:3,-1:5,121" +
":12,80,121:9,-1:17,121:3,-1:5,121:15,82,121:4,83,121,-1:17,121:3,-1:5,121:1" +
"0,84,121:11,-1:17,121:3,-1:5,121:2,85,121:19,-1:17,121:3,-1:5,121,86,121:20" +
",-1:17,121:3,-1:5,121:2,87,121:19,-1:17,121:3,-1:5,121:13,111,121:8,-1:17,1" +
"21:3,-1:5,121:2,112,121:19,-1:17,121:3,-1:5,121:6,113,121:15,-1:17,121:3,-1" +
":5,121:10,114,121:11,-1:17,121:3,-1:5,121:15,115,121:6,-1:17,121:3,-1:5,121" +
":3,120,121:18,-1:17,121:3,-1:5,121:8,116,121:13,-1:17,121:3,-1:5,121,88,121" +
":20,-1:17,121:3,-1:5,121:7,89,121:14,-1:17,121:3,-1:5,121:16,90,121:5,-1:17" +
",121:3,-1:5,121:8,91,121:13,-1:17,121:3,-1:5,121,92,121:20,-1:17,121:3,-1:5" +
",121,117,121:20,-1:17,121:3,-1:5,121:7,93,121:14,-1:17,121:3,-1:5,121:5,94," +
"121:16,-1:17,121:3,-1:5,121:17,95,121:4,-1:17,121:3,-1:5,121:17,96,121:4,-1" +
":17,121:3,-1:5,121:17,102,121:4,-1:17,121:3,-1:5,121:8,118,121:13,-1:17,121" +
":3,-1:5,121:8,103,121:13,-1:17,121:3,-1:5,121:5,104,121:16,-1:17,121:3,-1:5" +
",121:10,105,121:11,-1:17,121:3,-1:5,121:8,106,121:12,107,-1:17,121:3,-1:5,1" +
"21:17,108,121:4,-1:17,121:3,-1:5,121:2,109,121:19,-1:17,121:3,-1:5,121:20,1" +
"10,121,-1:17,121:3,-1:4");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

{
    if(lastChar >= 0)
    {
	errorMsg.error(lastChar, "unclosed comment"  + yytext());
    }
    return tok(sym.EOF, null);
}
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						{return tok(sym.ID, yytext());}
					case -2:
						break;
					case 2:
						{return tok(sym.DOT, null);}
					case -3:
						break;
					case 3:
						{return tok(sym.LBRACE, null);}
					case -4:
						break;
					case 4:
						{return tok(sym.RBRACE, null);}
					case -5:
						break;
					case 5:
						{return tok(sym.LPAREN, null);}
					case -6:
						break;
					case 6:
						{return tok(sym.RPAREN, null);}
					case -7:
						break;
					case 7:
						{return tok(sym.LBRACK, null);}
					case -8:
						break;
					case 8:
						{return tok(sym.RBRACK, null);}
					case -9:
						break;
					case 9:
						{return tok(sym.TIMES, null);}
					case -10:
						break;
					case 10:
						{return tok(sym.PLUS, null);}
					case -11:
						break;
					case 11:
						{return tok(sym.MINUS, null);}
					case -12:
						break;
					case 12:
						{return tok(sym.EXCLAMATION, null);}
					case -13:
						break;
					case 13:
						{return tok(sym.ASSIGN, null);}
					case -14:
						break;
					case 14:
						{errorMsg.error(yychar,
					"unmatched input: " + yytext());}
					case -15:
						break;
					case 15:
						{return tok(sym.LT, null);}
					case -16:
						break;
					case 16:
						{return tok(sym.COMMA, null);}
					case -17:
						break;
					case 17:
						{return tok(sym.SEMICOLON, null);}
					case -18:
						break;
					case 18:
						{return tok(sym.INTEGER_LITERAL, Integer.parseInt(yytext()));}
					case -19:
						break;
					case 19:
						{ }
					case -20:
						break;
					case 20:
						
					case -21:
						break;
					case 21:
						{return tok(sym.IF, null);}
					case -22:
						break;
					case 22:
						{return tok(sym.AND, null);}
					case -23:
						break;
					case 23:
						{lastChar = yychar; yybegin(MULTICOMMENT);}
					case -24:
						break;
					case 24:
						{yybegin(COMMENT);}
					case -25:
						break;
					case 25:
						{return tok(sym.INT, null);}
					case -26:
						break;
					case 26:
						{return tok(sym.NEW, null);}
					case -27:
						break;
					case 27:
						{return tok(sym.TRUE, null);}
					case -28:
						break;
					case 28:
						{return tok(sym.THIS, null);}
					case -29:
						break;
					case 29:
						{return tok(sym.VOID, null);}
					case -30:
						break;
					case 30:
						{return tok(sym.MAIN, null);}
					case -31:
						break;
					case 31:
						{return tok(sym.ELSE, null);}
					case -32:
						break;
					case 32:
						{return tok(sym.CLASS, null);}
					case -33:
						break;
					case 33:
						{return tok(sym.FALSE, null);}
					case -34:
						break;
					case 34:
						{return tok(sym.WHILE, null);}
					case -35:
						break;
					case 35:
						{return tok(sym.LENGTH, null);}
					case -36:
						break;
					case 36:
						{return tok(sym.STATIC, null);}
					case -37:
						break;
					case 37:
						{return tok(sym.PUBLIC, null);}
					case -38:
						break;
					case 38:
						{return tok(sym.STRING, null);}
					case -39:
						break;
					case 39:
						{return tok(sym.RETURN, null);}
					case -40:
						break;
					case 40:
						{return tok(sym.BOOLEAN, null);}
					case -41:
						break;
					case 41:
						{return tok(sym.PRINTLN, null);}
					case -42:
						break;
					case 42:
						{ }
					case -43:
						break;
					case 43:
						{yybegin(YYINITIAL);}
					case -44:
						break;
					case 44:
						{ }
					case -45:
						break;
					case 45:
						{ }
					case -46:
						break;
					case 46:
						{lastChar = -1; yybegin(YYINITIAL);}
					case -47:
						break;
					case 48:
						{return tok(sym.ID, yytext());}
					case -48:
						break;
					case 49:
						{errorMsg.error(yychar,
					"unmatched input: " + yytext());}
					case -49:
						break;
					case 50:
						{ }
					case -50:
						break;
					case 52:
						{return tok(sym.ID, yytext());}
					case -51:
						break;
					case 53:
						{errorMsg.error(yychar,
					"unmatched input: " + yytext());}
					case -52:
						break;
					case 55:
						{return tok(sym.ID, yytext());}
					case -53:
						break;
					case 57:
						{return tok(sym.ID, yytext());}
					case -54:
						break;
					case 59:
						{return tok(sym.ID, yytext());}
					case -55:
						break;
					case 61:
						{return tok(sym.ID, yytext());}
					case -56:
						break;
					case 63:
						{return tok(sym.ID, yytext());}
					case -57:
						break;
					case 65:
						{return tok(sym.ID, yytext());}
					case -58:
						break;
					case 67:
						{return tok(sym.ID, yytext());}
					case -59:
						break;
					case 69:
						{return tok(sym.ID, yytext());}
					case -60:
						break;
					case 71:
						{return tok(sym.ID, yytext());}
					case -61:
						break;
					case 73:
						{return tok(sym.ID, yytext());}
					case -62:
						break;
					case 75:
						{return tok(sym.ID, yytext());}
					case -63:
						break;
					case 76:
						{return tok(sym.ID, yytext());}
					case -64:
						break;
					case 77:
						{return tok(sym.ID, yytext());}
					case -65:
						break;
					case 78:
						{return tok(sym.ID, yytext());}
					case -66:
						break;
					case 79:
						{return tok(sym.ID, yytext());}
					case -67:
						break;
					case 80:
						{return tok(sym.ID, yytext());}
					case -68:
						break;
					case 81:
						{return tok(sym.ID, yytext());}
					case -69:
						break;
					case 82:
						{return tok(sym.ID, yytext());}
					case -70:
						break;
					case 83:
						{return tok(sym.ID, yytext());}
					case -71:
						break;
					case 84:
						{return tok(sym.ID, yytext());}
					case -72:
						break;
					case 85:
						{return tok(sym.ID, yytext());}
					case -73:
						break;
					case 86:
						{return tok(sym.ID, yytext());}
					case -74:
						break;
					case 87:
						{return tok(sym.ID, yytext());}
					case -75:
						break;
					case 88:
						{return tok(sym.ID, yytext());}
					case -76:
						break;
					case 89:
						{return tok(sym.ID, yytext());}
					case -77:
						break;
					case 90:
						{return tok(sym.ID, yytext());}
					case -78:
						break;
					case 91:
						{return tok(sym.ID, yytext());}
					case -79:
						break;
					case 92:
						{return tok(sym.ID, yytext());}
					case -80:
						break;
					case 93:
						{return tok(sym.ID, yytext());}
					case -81:
						break;
					case 94:
						{return tok(sym.ID, yytext());}
					case -82:
						break;
					case 95:
						{return tok(sym.ID, yytext());}
					case -83:
						break;
					case 96:
						{return tok(sym.ID, yytext());}
					case -84:
						break;
					case 97:
						{return tok(sym.ID, yytext());}
					case -85:
						break;
					case 98:
						{return tok(sym.ID, yytext());}
					case -86:
						break;
					case 99:
						{return tok(sym.ID, yytext());}
					case -87:
						break;
					case 100:
						{return tok(sym.ID, yytext());}
					case -88:
						break;
					case 101:
						{return tok(sym.ID, yytext());}
					case -89:
						break;
					case 102:
						{return tok(sym.ID, yytext());}
					case -90:
						break;
					case 103:
						{return tok(sym.ID, yytext());}
					case -91:
						break;
					case 104:
						{return tok(sym.ID, yytext());}
					case -92:
						break;
					case 105:
						{return tok(sym.ID, yytext());}
					case -93:
						break;
					case 106:
						{return tok(sym.ID, yytext());}
					case -94:
						break;
					case 107:
						{return tok(sym.ID, yytext());}
					case -95:
						break;
					case 108:
						{return tok(sym.ID, yytext());}
					case -96:
						break;
					case 109:
						{return tok(sym.ID, yytext());}
					case -97:
						break;
					case 110:
						{return tok(sym.ID, yytext());}
					case -98:
						break;
					case 111:
						{return tok(sym.ID, yytext());}
					case -99:
						break;
					case 112:
						{return tok(sym.ID, yytext());}
					case -100:
						break;
					case 113:
						{return tok(sym.ID, yytext());}
					case -101:
						break;
					case 114:
						{return tok(sym.ID, yytext());}
					case -102:
						break;
					case 115:
						{return tok(sym.ID, yytext());}
					case -103:
						break;
					case 116:
						{return tok(sym.ID, yytext());}
					case -104:
						break;
					case 117:
						{return tok(sym.ID, yytext());}
					case -105:
						break;
					case 118:
						{return tok(sym.ID, yytext());}
					case -106:
						break;
					case 119:
						{return tok(sym.ID, yytext());}
					case -107:
						break;
					case 120:
						{return tok(sym.ID, yytext());}
					case -108:
						break;
					case 121:
						{return tok(sym.ID, yytext());}
					case -109:
						break;
					case 122:
						{return tok(sym.ID, yytext());}
					case -110:
						break;
					case 123:
						{return tok(sym.ID, yytext());}
					case -111:
						break;
					case 124:
						{return tok(sym.ID, yytext());}
					case -112:
						break;
					case 125:
						{return tok(sym.ID, yytext());}
					case -113:
						break;
					case 126:
						{return tok(sym.ID, yytext());}
					case -114:
						break;
					case 127:
						{return tok(sym.ID, yytext());}
					case -115:
						break;
					case 128:
						{return tok(sym.ID, yytext());}
					case -116:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}

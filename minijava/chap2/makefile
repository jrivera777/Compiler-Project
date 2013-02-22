# To compile everything, type "make"
# To clean up, type "make clean"

JFLAGS=-g

parse/Main.class: parse/*.java parse/Yylex.java errormsg/ErrorMsg.class
	javac ${JFLAGS} parse/*.java

parse/Yylex.java: parse/MiniJava.lex
	cd parse; java JLex.Main MiniJava.lex; mv MiniJava.lex.java Yylex.java

errormsg/ErrorMsg.class:  errormsg/*.java
	javac ${JFLAGS} errormsg/*.java

clean:
	rm parse/*.class errormsg/*.class parse/Yylex.java


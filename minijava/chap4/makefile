# To compile everything, type "make"
# To clean up, type "make clean"

JFLAGS=-g

main/Main.class: main/*.java errormsg/ErrorMsg.class parse/Parse.class \
		 semant/Exp.class syntaxtree/Program.class \
		 visitor/Visitor.class
	javac ${JFLAGS} main/*.java

errormsg/ErrorMsg.class:  errormsg/*.java
	javac ${JFLAGS} errormsg/*.java

parse/Parse.class: parse/*.java parse/Yylex.java parse/Grm.java
	javac ${JFLAGS} parse/*.java

parse/Yylex.java: parse/MiniJava.lex
	cd parse; java JLex.Main MiniJava.lex; mv MiniJava.lex.java Yylex.java

parse/Grm.java: parse/Grm.cup
	cd parse; java java_cup.Main -parser Grm -expect 0 \
	-dump_grammar -dump_states <Grm.cup >Grm.out 2>Grm.err

semant/Exp.class: semant/*.java
	javac ${JFLAGS} semant/*.java

syntaxtree/Program.class: syntaxtree/*.java
	javac ${JFLAGS} syntaxtree/*.java

visitor/Visitor.class: visitor/*.java
	javac ${JFLAGS} visitor/*.java

clean:
	rm */*.class parse/Grm.err parse/Grm.out parse/Grm.java \
	parse/Yylex.java parse/sym.java


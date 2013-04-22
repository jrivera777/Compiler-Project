# To compile everything, type "make"
# To clean up, type "make clean"

JFLAGS=-g

main/Main.class: main/*.java assem/Instr.class canon/Canon.class \
		 errormsg/ErrorMsg.class frame/Frame.class \
		 parse/Parse.class semant/Exp.class sparc/SparcFrame.class \
		 syntaxtree/Program.class temp/Temp.class tree/Exp.class \
		 util/BoolList.class visitor/Visitor.class
	javac ${JFLAGS} main/*.java

assem/Instr.class: assem/*.java
	javac ${JFLAGS} assem/*.java

canon/Canon.class: canon/*.java
	javac ${JFLAGS} canon/*.java

errormsg/ErrorMsg.class:  errormsg/*.java
	javac ${JFLAGS} errormsg/*.java

frame/Frame.class : frame/*.java
	javac ${JFLAGS} frame/*.java

parse/Parse.class: parse/*.java parse/Yylex.java parse/Grm.java
	javac ${JFLAGS} parse/*.java

parse/Yylex.java: parse/MiniJava.lex
	cd parse; java JLex.Main MiniJava.lex; mv MiniJava.lex.java Yylex.java

parse/Grm.java: parse/Grm.cup
	cd parse; java java_cup.Main -parser Grm -expect 0 \
	-dump_grammar -dump_states <Grm.cup >Grm.out 2>Grm.err

semant/Exp.class: semant/*.java
	javac ${JFLAGS} semant/*.java

sparc/SparcFrame.class : sparc/*.java
	javac ${JFLAGS} sparc/*.java

syntaxtree/Program.class: syntaxtree/*.java
	javac ${JFLAGS} syntaxtree/*.java

temp/Temp.class: temp/*.java
	javac ${JFLAGS} temp/*.java

tree/Exp.class: tree/*.java
	javac ${JFLAGS} tree/*.java

util/BoolList.class: util/*.java
	javac ${JFLAGS} util/*.java

visitor/Visitor.class: visitor/*.java
	javac ${JFLAGS} visitor/*.java

clean:
	rm */*.class parse/Grm.err parse/Grm.out parse/Grm.java \
	parse/Yylex.java parse/sym.java


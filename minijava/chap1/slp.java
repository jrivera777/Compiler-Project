
abstract class Stm {}

class CompoundStm extends Stm {
   final Stm stm1, stm2;
   CompoundStm(Stm s1, Stm s2) {stm1=s1; stm2=s2;}
}

class AssignStm extends Stm {
   final String id; final Exp exp;
   AssignStm(String i, Exp e) {id=i; exp=e;}
}

class PrintStm extends Stm {
   final ExpList exps;
   PrintStm(ExpList e) {exps=e;}
}

abstract class Exp {}

class IdExp extends Exp {
   final String id;
   IdExp(String i) {id=i;}
}

class NumExp extends Exp {
   final int num;
   NumExp(int n) {num=n;}
}

class OpExp extends Exp {
   final Exp left, right; final int oper;
   static final int Plus=1,Minus=2,Times=3,Div=4;
   OpExp(Exp l, int o, Exp r) {left=l; oper=o; right=r;}
}

class EseqExp extends Exp {
   final Stm stm; final Exp exp;
   EseqExp(Stm s, Exp e) {stm=s; exp=e;}
}

abstract class ExpList {}

class PairExpList extends ExpList {
   final Exp head; final ExpList tail;
   PairExpList(Exp h, ExpList t) {head=h; tail=t;}
}

class LastExpList extends ExpList {
   final Exp head; 
   LastExpList(Exp h) {head=h;}
}

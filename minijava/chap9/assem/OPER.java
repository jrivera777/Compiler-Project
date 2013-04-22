package assem;

public class OPER extends Instr {
   public temp.TempList dst;   
   public temp.TempList src;
   public Targets jump;

   public OPER(String a, temp.TempList d, temp.TempList s, temp.LabelList j) {
      assem=a; dst=d; src=s; jump=new Targets(j);
   }
   public OPER(String a, temp.TempList d, temp.TempList s) {
      assem=a; dst=d; src=s; jump=null;
   }

   public temp.TempList use() {return src;}
   public temp.TempList def() {return dst;}
   public Targets jumps() {return jump;}
}

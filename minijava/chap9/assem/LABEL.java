package assem;

public class LABEL extends Instr {
   public temp.Label label;

   public LABEL(String a, temp.Label l) {
      assem=a; label=l;
   }

   public temp.TempList use() {return null;}
   public temp.TempList def() {return null;}
   public Targets jumps()     {return null;}

}

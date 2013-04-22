package assem;

public class MOVE extends Instr {
   public temp.Temp dst;   
   public temp.Temp src;

   public MOVE(String a, temp.Temp d, temp.Temp s) {
      assem=a; dst=d; src=s;
   }
   public temp.TempList use() {return new temp.TempList(src,null);}
   public temp.TempList def() {return new temp.TempList(dst,null);}
   public Targets jumps()     {return null;}
}

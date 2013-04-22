package syntaxtree;

import java.util.Vector;

public class ExpList {
   private Vector<Exp> list;

   public ExpList() {
      list = new Vector<Exp>();
   }

   public void addElement(Exp n) {
      list.addElement(n);
   }

   public Exp elementAt(int i)  { 
      return list.elementAt(i); 
   }

   public int size() { 
      return list.size(); 
   }
}

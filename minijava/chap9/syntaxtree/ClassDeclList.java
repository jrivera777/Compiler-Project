package syntaxtree;

import java.util.Vector;

public class ClassDeclList {
   private Vector<ClassDecl> list;

   public ClassDeclList() {
      list = new Vector<ClassDecl>();
   }

   public void addElement(ClassDecl n) {
      list.addElement(n);
   }

   public ClassDecl elementAt(int i)  { 
      return list.elementAt(i); 
   }

   public int size() { 
      return list.size(); 
   }
}

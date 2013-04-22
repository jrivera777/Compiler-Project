package semant;

import java.util.Hashtable;

public class SymbolTable {
  private Hashtable<String, ClassInfo> hashtable;
      
  public SymbolTable() {
    hashtable = new Hashtable<String, ClassInfo>();
  }

  // Add a new class to the symbol table.  But if a class with the same
  // name already exists, return false and leave the symbol table unchanged.
  public boolean addClass(String id, ClassInfo ci) {
    if (hashtable.containsKey(id))
      return false;
    hashtable.put(id, ci);
    return true;
  }

  public ClassInfo get(String id) {
    return hashtable.get(id);
  }
}

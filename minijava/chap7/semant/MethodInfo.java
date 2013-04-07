package semant;

import syntaxtree.Type;
import java.util.Hashtable;
import java.util.Vector;

class MethodInfo {
    private String name;
    private Type rt;				  // return type
    private Vector<Type> formals;		          // the types of the parameters
    private Hashtable<String, VariableInfo> locals; // maps String to VariableInfo

    public MethodInfo(String n, Type r) {
	name = n;
	rt = r;
	formals = new Vector<Type>();
	locals = new Hashtable<String, VariableInfo>();
    }

    public String getName () {
	return name;
    }

    public Type getReturnType() {
	return rt;
    }

    // Return a String like "(int, boolean, Foo)" describing the formals' types.
    public String getFormalsTypes() {
	String s = formals.toString();
	return "(" + s.substring(1, s.length()-1) + ")";
    }

    public boolean addFormal(String id, VariableInfo vi) {
	formals.addElement(vi.type);   // Do this even if id is a duplicate name!
	if (locals.containsKey(id))
	    return false;
	locals.put(id, vi);
	return true;
    }

    public boolean addVar(String id, VariableInfo vi) {
	if (locals.containsKey(id))
	    return false;
	locals.put(id, vi);
	return true;
   }

    public VariableInfo getVar(String id) {
	return (VariableInfo) locals.get(id);i) {
	if (locals.containsKey(id))
	    return false;
	locals.put(id, vi);
	return true;
   }

    public VariableInfo getVar(String id) {
	return (VariableInfo) locals.get(id);
    }

    }

}

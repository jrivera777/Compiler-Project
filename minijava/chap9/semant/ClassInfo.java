package semant;

import java.util.Hashtable;

class ClassInfo {

    private String name;
    private Hashtable<String, VariableInfo> fields;
    private Hashtable<String, MethodInfo> methods;

    public ClassInfo(String n) {
	name = n;
	fields = new Hashtable<String, VariableInfo>();
	methods = new Hashtable<String, MethodInfo>();
    }

    public String getName(){
	return name;
    }

    // Add a new field to the class.  But if a field with the same name
    // already exists, return false and leave 'fields' unchanged.
    public boolean addField(String id, VariableInfo vi) {
	if (fields.containsKey(id))
	    return false;
	fields.put(id, vi);
	return true;
    }

    public VariableInfo getField(String id) {
	return fields.get(id);
    }

    // Return the number of fields in the class.
    public int getFieldsCount() {
	return fields.size();
    }

    // Add a new method to the class.  But if a method with the same name
    // already exists, return false and leave 'methods' unchanged.
    public boolean addMethod(String id, MethodInfo mi) {
	if (methods.containsKey(id))
	    return false;
	methods.put(id, mi);
	return true;
    }

    public MethodInfo getMethod(String id) {
	return methods.get(id);
    }
}

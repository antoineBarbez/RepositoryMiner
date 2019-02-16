package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class ClassObject extends CodeComponent {
	private boolean _Interface = false;
	private Set<FieldObject> fields = new HashSet<FieldObject>();
	private Set<InnerClassObject> innerClasses = new HashSet<InnerClassObject>();
	private Set<MethodObject> methods = new HashSet<MethodObject>();
	
	public ClassObject(String name) {
		super(name);
	}
	
	public void addField(FieldObject field) {
		fields.add(field);
	}
	
	public void addInnerClass(InnerClassObject innerClass) {
		innerClasses.add(innerClass);
	}
	
	public void addMethod(MethodObject m) {
		methods.add(m);
	}
	
	public Set<FieldObject> getFields() {
		return fields;
	}
	
	public Set<InnerClassObject> getInnerClasses() {
		return innerClasses;
	}
	
	public Set<MethodObject> getMethods() {
		return methods;
	}
	
	public boolean isDataClass() {
		int nbFields = fields.size();
		int nbNonAccessorMethods = 0;
		for (MethodObject m: methods) {
			if (!m.isAccessor()) {
				nbNonAccessorMethods++;
			}
		}
		nbNonAccessorMethods = Integer.max(1, nbNonAccessorMethods);
		
		double ratio = nbFields/nbNonAccessorMethods;
		if (ratio >= 7) {
			return true;
		}
		return false;
	}
	
	public boolean isInterface() {
		return _Interface;
	}
	
	public void setInterface(boolean _interface) {
		this._Interface = _interface;
	}
}

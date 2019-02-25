package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class ClassObject extends CodeComponent {
	private boolean _interface;
	private String packageName;
	private String superClass = null;
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
	
	public String getName() {
		return packageName + "." + getIdentifier();
	}
	
	public String getPackage() {
		return packageName;
	}
	
	public String getSuperClass() {
		return superClass;
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
		return _interface;
	}
	
	public boolean isRelatedTo(ClassObject aClass) {
		if (aClass.getName().startsWith(this.getName()) || aClass.getName().equals(superClass)) {
			return true;
		}
		
		return false;
	}
	
	public void setInterface(boolean _interface) {
		this._interface = _interface;
	}
	
	public void setPackage(String packageName) {
		this.packageName = packageName;
	}
	
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}
}

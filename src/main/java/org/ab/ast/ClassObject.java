package org.ab.ast;

import java.util.HashSet;
import java.util.Set;

public class ClassObject extends CodeComponent {
	private boolean _interface;
	private String superClassName = null;
	private FileObject file;
	private Set<FieldObject> fields = new HashSet<FieldObject>();
	private Set<InnerClassObject> innerClasses = new HashSet<InnerClassObject>();
	private Set<MethodObject> methods = new HashSet<MethodObject>();
	
	public ClassObject(String identifier) {
		super(identifier);
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
	
	public FileObject getFile() {
		return file;
	}
	
	public Set<InnerClassObject> getInnerClasses() {
		return innerClasses;
	}
	
	public Set<MethodObject> getMethods() {
		return methods;
	}
	
	@Override
	public String getName() {
		String packageName = this.getFile().getPackage();
		if (packageName != null) {
			return packageName + "." + getIdentifier();
		}
		return getIdentifier();
	}
	
	public ClassObject getSuperClass() {
		if (superClassName == null) {
			return null;
		}
		
		SystemObject s = SystemObject.getInstance();
		return s.getClassByName(superClassName);
	}
	
	public boolean inheritFrom(ClassObject aClass) {
		ClassObject superClass = getSuperClass();
		if (aClass == null || superClass == null) {
			return false;
		}
		
		while (superClass != null) {
			if (superClass.getName().equals(aClass.getName())) {
				return true;
			}
			superClass = superClass.getSuperClass();
		}
		return false;
	}
	
	public boolean isDataClass() {
		int nbFields = fields.size();
		int nbMethods = 0;
		for (MethodObject m: methods) {
			if (!m.isAccessor() && !m.isConstructor()) {
				nbMethods++;
			}
		}
		nbMethods = Integer.max(1, nbMethods);
		
		double ratio = nbFields/nbMethods;
		if (ratio >= 6) {
			return true;
		}
		return false;
	}
	
	public boolean isInterface() {
		return _interface;
	}
	
	public boolean isRelatedTo(ClassObject aClass) {
		// Check if it is the same class
		if (aClass.getName().equals(getName())) {
			return true;
		}
		
		// Check if it is an inner class of the current class
		if (aClass.getName().startsWith(getName() + ".")) {
			return true;
		}
		
		// Check for inheritance relationship
		if (aClass.inheritFrom(this) || this.inheritFrom(aClass)) {
			return true;
		}
		
		return false;
	}
	
	public void setFile(FileObject file) {
		this.file = file;
	}
	
	public void setInterface(boolean _interface) {
		this._interface = _interface;
	}
	
	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}
}

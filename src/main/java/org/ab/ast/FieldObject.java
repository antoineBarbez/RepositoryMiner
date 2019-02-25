package org.ab.ast;

public class FieldObject extends CodeComponent {
	private ClassObject declaringClass = null;
	
	public FieldObject(String identifier) {
		super(identifier);
	}
	
	public ClassObject getDeclaringClass() {
		return declaringClass;
	}
	
	public String getName() {
		return declaringClass.getName() + "." + getIdentifier();
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
}

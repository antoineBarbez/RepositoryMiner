package org.ab.ast;

public class FieldObject extends CodeComponent {
	private ClassObject declaringClass = null;
	
	public FieldObject(String name) {
		super(name);
	}
	
	public ClassObject getDeclaringClass() {
		return declaringClass;
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
}

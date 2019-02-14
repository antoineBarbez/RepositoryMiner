package org.ab.ast;

public class InnerClassObject extends ClassObject {
	private ClassObject declaringClass;
	
	public InnerClassObject(String name) {
		super(name);
	}
	
	public ClassObject getDeclaringClass() {
		return this.declaringClass;
	}
	
	public void setDeclaringClass(ClassObject declaringClass) {
		this.declaringClass = declaringClass;
	}
}
